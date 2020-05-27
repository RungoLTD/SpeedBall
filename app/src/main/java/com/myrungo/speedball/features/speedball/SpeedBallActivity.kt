package com.myrungo.speedball.features.speedball

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.myrungo.speedball.BuildConfig
import com.myrungo.speedball.R
import com.myrungo.speedball.databinding.ActivitySpeedballBinding
import com.myrungo.speedball.features.base.BaseActivity
import com.myrungo.speedball.utils.FLAGS_FULLSCREEN
import org.koin.android.viewmodel.ext.android.getViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

private const val IMMERSIVE_FLAG_TIMEOUT = 500L

class SpeedBallActivity : BaseActivity(), LifecycleOwner {

    private val binding: ActivitySpeedballBinding by binding(R.layout.activity_speedball)

    private val viewModel by lazy {
        getViewModel<SpeedBallViewModel>()
    }

    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupNavigation()
        MobileAds.initialize(this) {}
        mInterstitialAd = InterstitialAd(this)

        if (BuildConfig.DEBUG) {
            mInterstitialAd.adUnitId = getString(R.string.admob_id_debug)
        } else {
            mInterstitialAd.adUnitId = getString(R.string.admob_id_release)
        }

        mInterstitialAd.loadAd(AdRequest.Builder().build())
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
        if (isFinishing) {
            mInterstitialAd.show()
            deleteFile()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFinishing) {
            mInterstitialAd.show()
            deleteFile()
        }
    }
}