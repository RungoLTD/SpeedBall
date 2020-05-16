package com.rungo.speedball.features.speedball

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
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
import androidx.camera.core.*
import androidx.camera.core.impl.PreviewConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.view.drawToBitmap
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rungo.speedball.R
import com.rungo.speedball.databinding.ActivitySpeedballBinding
import com.rungo.speedball.features.base.BaseActivity
import com.rungo.speedball.utils.*
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.ScreenCaptureListener
import kotlinx.android.synthetic.main.activity_speedball.*
import org.koin.android.viewmodel.ext.android.getViewModel
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max

private const val IMMERSIVE_FLAG_TIMEOUT = 500L

class SpeedBallActivity : BaseActivity(), LifecycleOwner {

    private val binding: ActivitySpeedballBinding by binding(R.layout.activity_speedball)

    private val viewModel by lazy {
        getViewModel<SpeedBallViewModel>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupNavigation()
    }

    private fun setupNavigation() {
        binding.fragmentContainer.postDelayed({
            binding.fragmentContainer.systemUiVisibility = FLAGS_FULLSCREEN
        }, IMMERSIVE_FLAG_TIMEOUT)
    }

    private fun deleteFile() {
        viewModel.getUriImage()?.let {
            if (it.path != null) {
                val deleteFile = File(it.path!!)
                if (deleteFile.exists()) {
                    deleteFile.canonicalFile.delete()
                }
            }
        }
    }

    companion object {
        const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val PHOTO_EXTENSION = ".jpg"
        const val RATIO_4_3_VALUE = 4.0 / 3.0
        const val RATIO_16_9_VALUE = 16.0 / 9.0

        /** Helper function used to create a timestamped file */
        fun createFile(baseFolder: File, format: String, extension: String) =
            File(
                baseFolder, SimpleDateFormat(format, Locale.US)
                    .format(System.currentTimeMillis()) + extension
            )

        fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteFile()
    }
}