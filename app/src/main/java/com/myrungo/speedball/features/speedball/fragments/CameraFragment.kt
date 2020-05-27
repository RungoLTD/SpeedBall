package com.myrungo.speedball.features.speedball.fragments

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.myrungo.speedball.R
import com.myrungo.speedball.data.model.Result
import com.myrungo.speedball.databinding.FragmentCameraBinding
import com.myrungo.speedball.features.base.BaseFragment
import com.myrungo.speedball.features.speedball.SpeedBallActivity
import com.myrungo.speedball.features.speedball.SpeedBallActivity.Companion.FILENAME
import com.myrungo.speedball.features.speedball.SpeedBallActivity.Companion.PHOTO_EXTENSION
import com.myrungo.speedball.features.speedball.SpeedBallActivity.Companion.RATIO_16_9_VALUE
import com.myrungo.speedball.features.speedball.SpeedBallActivity.Companion.RATIO_4_3_VALUE
import com.myrungo.speedball.features.speedball.SpeedBallActivity.Companion.createFile
import com.myrungo.speedball.features.speedball.SpeedBallViewModel
import com.myrungo.speedball.utils.*
import org.koin.android.viewmodel.ext.android.getViewModel
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraFragment : BaseFragment(), SpeedManagerListener {

    private lateinit var binding: FragmentCameraBinding

    private var displayId: Int = -1
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private lateinit var outputDirectory: File

    private lateinit var speedManager: SpeedManager

    private val displayManager by lazy {
        requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    private val viewModel by lazy {
        getViewModel<SpeedBallViewModel>()
    }

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = binding(layoutInflater, R.layout.fragment_camera, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupDefaultValues()
        setupObservers()

        cameraExecutor = Executors.newSingleThreadExecutor()

        outputDirectory = SpeedBallActivity.getOutputDirectory(requireContext())

        // Every time the orientation of device changes, update rotation for use cases
        displayManager.registerDisplayListener(displayListener, null)

        binding.viewFinder.post {
            binding.viewFinder.display?.let {
                displayId = it.displayId
            }
            updateCameraUi()
            bindCameraUseCases()
        }

        speedManager = SpeedManager(requireActivity(), viewModel.sensitive, this)

        Thread(speedManager).start()
    }

    private fun setupObservers() {
        viewModel.apply {
            isPhotoCompleted.observe(viewLifecycleOwner, Observer {
                if (it && isSpeedDetected.value!!) {
                    openResultScreen()
                }
            })

            isSpeedDetected.observe(viewLifecycleOwner, Observer {
                if (it && isPhotoCompleted.value!!) {
                    openResultScreen()
                }
            })
        }
    }

    private fun bindCameraUseCases() {
        val metrics = DisplayMetrics().also { binding.viewFinder.display.getRealMetrics(it) }

        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)

        val rotation = binding.viewFinder.display.rotation

        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // CameraProvider
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()

            // ImageCapture
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Timber.d("Average luminosity: $luma")
                    })
                }

            cameraProvider.unbindAll()

            try {
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalyzer)
                preview?.setSurfaceProvider(binding.viewFinder.createSurfaceProvider(camera?.cameraInfo))
            } catch(exc: Exception) {
                Timber.e("Use case binding failed $exc")
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun updateCameraUi() {
        binding.cameraContainer.let {
            binding.cameraContainer.removeView(it)
        }
    }

    private fun takePicture() {
        imageCapture?.let {
            // Create output file to hold the image
            val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)

            // Setup image capture metadata
            val metadata = ImageCapture.Metadata().apply {
                isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
            }

            // Create output options object which contains file + metadata
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
                .setMetadata(metadata)
                .build()

            it.takePicture(outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                    viewModel.setUriImage(savedUri)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        binding.cameraContainer.postDelayed({
                            binding.cameraContainer.foreground = ColorDrawable(Color.WHITE)
                            binding.cameraContainer.postDelayed(
                                { binding.cameraContainer.foreground = null }, ANIMATION_FAST_MILLIS)
                        }, ANIMATION_SLOW_MILLIS)
                    }

                    requireActivity().runOnUiThread {
                        viewModel.isPhotoCompleted.value = true
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Timber.d(exception)
                }
            })
        }
    }

    private fun openResultScreen() {

        Thread(speedManager).interrupt()

        NavHostFragment.findNavController(this).navigate(R.id.resultFragment)
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@CameraFragment.displayId) {
                Timber.d("Rotation changed: ${view.display.rotation}")
                imageCapture?.targetRotation = view.display.rotation
                imageAnalyzer?.targetRotation = view.display.rotation
            }
        } ?: Unit
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateCameraUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        Thread(speedManager).interrupt()
        displayManager.unregisterDisplayListener(displayListener)
    }

    override fun requestPermission() {

    }

    override fun onError(error: String?) {

    }

    override fun didDetectedFirstTime() {
        try {
            takePicture()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun didDetectedLastTime(firstTime: Long, lastTime: Long, delay: Long) {
        val finalSpeed = (100f / (delay.toFloat() / 100) * 3.6f).toInt()

        val result = Result(speed = finalSpeed, date = LocalDateTime.now())
        viewModel.setResult(result)
        viewModel.isSpeedDetected.value = true
    }

    private fun setupDefaultValues() {
        viewModel.apply {
            isPhotoCompleted.value = false
            isSpeedDetected.value = false
        }
    }
}