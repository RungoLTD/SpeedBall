package com.rungo.speedball.features.speedball

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rungo.speedball.R
import com.rungo.speedball.databinding.ActivitySpeedballBinding
import com.rungo.speedball.features.base.BaseActivity
import com.rungo.speedball.utils.ANIMATION_FAST_MILLIS
import com.rungo.speedball.utils.ANIMATION_SLOW_MILLIS
import com.rungo.speedball.utils.LuminosityAnalyzer
import com.rungo.speedball.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max

val EXTENSION_WHITELIST = arrayOf("JPG")

class SpeedBallActivity : BaseActivity() {

    private val binding: ActivitySpeedballBinding by binding(R.layout.activity_speedball)

    private lateinit var outputDirectory: File
    private lateinit var broadcastManager: LocalBroadcastManager

    private var displayId: Int = -1
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null

    private val displayManager by lazy {
        getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    private lateinit var cameraExecutor: ExecutorService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupListeners()
        initComponents()
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnShare.setOnClickListener {
            showToast("Share")
        }
    }

    private fun initComponents() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Every time the orientation of device changes, update rotation for use cases
        displayManager.registerDisplayListener(displayListener, null)

        // Determine the output directory
        outputDirectory = getOutputDirectory(this)

        // Wait for the views to be properly laid out
        binding.viewFinder.post {

            // Keep track of the display in which this view is attached
            displayId = binding.viewFinder.display.displayId

            // Build UI controls
            updateCameraUi()

            // Bind use cases
            bindCameraUseCases()
        }
    }

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = window.decorView.let { view ->
            if (displayId == displayId) {
                Timber.d("Rotation changed: ${view.display.rotation}")
                imageCapture?.targetRotation = view.display.rotation
                imageAnalyzer?.targetRotation = view.display.rotation
            }
        }
    }

    override fun onStop() {
        super.onStop()
        cameraExecutor.shutdown()
        displayManager.unregisterDisplayListener(displayListener)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    /** Declare and bind preview, capture and analysis use cases
     * Объявите и свяжите варианты использования предварительного просмотра, захвата и анализа
     * */
    private fun bindCameraUseCases() {

        // Получить метрики экрана, используемые для настройки камеры на полное разрешение экрана
        val metrics = DisplayMetrics().also { binding.viewFinder.display.getRealMetrics(it) }
        Timber.d("Screen metrics: ${metrics.widthPixels} x ${metrics.heightPixels}")

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        Timber.d("Preview aspect ratio: $screenAspectRatio")

        val rotation = binding.viewFinder.display.rotation

        // Свяжите CameraProvider с LifeCycleOwner
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                // Мы запрашиваем соотношение сторон, но без разрешения
                .setTargetAspectRatio(screenAspectRatio)
                // Установить начальную цель вращения
                .setTargetRotation(rotation)
                .build()

            // ImageCapture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                // We request aspect ratio but no resolution to match preview config, but letting
                // CameraX optimize for whatever specific resolution best fits our use cases
                .setTargetAspectRatio(screenAspectRatio)
                //  Мы запрашиваем соотношение сторон, но без разрешения, чтобы соответствовать конфигурации предварительного просмотра, но позволяем
                //  CameraX оптимизирует для любого конкретного разрешения лучше всего подходит для наших случаев использования
                .setTargetRotation(rotation)
                .build()

            // ImageAnalysis
            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                // Устанавливаем начальное целевое вращение, нам нужно будет вызвать его снова, если вращение изменится
                // в течение жизненного цикла этого варианта использования
                .setTargetRotation(rotation)
                .build()
                // Затем анализатор можно назначить экземпляру
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        // Значения, возвращаемые нашим анализатором, передаются подключенному слушателю.
                        // Здесь мы регистрируем результаты анализа изображений - вы должны сделать что-то полезное
                        // вместо этого!
                        Timber.d("Average luminosity: $luma")
                    })
                }

            // Must unbind the use-cases before rebinding them
            cameraProvider.unbindAll()

            try {
                // Переменное количество вариантов использования может быть передано здесь -
                // камера обеспечивает доступ к CameraControl & CameraInfo
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer)

                // Прикрепите провайдера поверхности видоискателя для предварительного просмотра варианта использования
                preview?.setSurfaceProvider(binding.viewFinder.createSurfaceProvider(camera?.cameraInfo))
            } catch(exc: Exception) {
                Timber.e( "Use case binding failed")
            }

        }, ContextCompat.getMainExecutor(this))
    }


    /**
     *  [androidx.camera.core.ImageAnalysisConfig] requires enum value of
     *  [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
     *
     *  Определение наиболее подходящего отношения для размеров, предоставляемых в @params,
     *  путем подсчета абсолютного отношения предварительного просмотра к одному из предоставленных значений.
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / kotlin.math.min(width, height)

        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    /** Method used to re-draw the camera UI controls, called every time configuration changes. */
    private fun updateCameraUi() {

        // Remove previous UI if any
        binding.cameraContainer.removeView(binding.cameraContainer)
    }

    private fun takePicture() {
        // Get a stable reference of the modifiable image capture use case
        imageCapture?.let { imageCapture ->

            // Create output file to hold the image
            val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)

            // Setup image capture metadata
            val metadata = ImageCapture.Metadata().apply {

                // Mirror image when using the front camera
                isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
            }

            // Create output options object which contains file + metadata
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                .setMetadata(metadata)
                .build()

            // Setup image capture listener which is triggered after photo has been taken
            imageCapture.takePicture(
                outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Timber.e("Photo capture failed: ${exc.message}")
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val savedUri = output.savedUri ?: Uri.fromFile(photoFile)

                        // Implicit broadcasts will be ignored for devices running API level >= 24
                        // so if you only target API level 24+ you can remove this statement
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            sendBroadcast(Intent(android.hardware.Camera.ACTION_NEW_PICTURE, savedUri))
                        }

                        // If the folder selected is an external media directory, this is
                        // unnecessary but otherwise other apps will not be able to access our
                        // images unless we scan them using [MediaScannerConnection]
                        val mimeType = MimeTypeMap.getSingleton()
                            .getMimeTypeFromExtension(savedUri.toFile().extension)
                        MediaScannerConnection.scanFile(
                            this@SpeedBallActivity,
                            arrayOf(savedUri.toFile().absolutePath),
                            arrayOf(mimeType)
                        ) { _, uri ->
                            Timber.d("Image capture scanned into media store: $uri")
                        }
                    }
                })

            // We can only change the foreground Drawable using API level 23+ API
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                // Display flash animation to indicate that photo was captured
                binding.cameraContainer.postDelayed({
                    binding.cameraContainer.foreground = ColorDrawable(Color.WHITE)
                    binding.cameraContainer.postDelayed(
                        { binding.cameraContainer.foreground = null }, ANIMATION_FAST_MILLIS)
                }, ANIMATION_SLOW_MILLIS)
            }
        }
    }

    companion object {

        private const val TAG = "CameraXBasic"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension)

        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }
}