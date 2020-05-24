package com.rungo.speedball.features.speedball.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.mindorks.Screenshot
import com.mindorks.properties.Flip
import com.mindorks.properties.Quality
import com.rungo.speedball.R
import com.rungo.speedball.data.model.getSpeedEmoji
import com.rungo.speedball.data.model.getSpeedStatus
import com.rungo.speedball.data.model.getSpeedType
import com.rungo.speedball.databinding.FragmentResultBinding
import com.rungo.speedball.features.base.BaseFragment
import com.rungo.speedball.features.speedball.SpeedBallActivity
import com.rungo.speedball.features.speedball.SpeedBallViewModel
import com.rungo.speedball.utils.Constants
import org.koin.android.viewmodel.ext.android.getViewModel
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.OutputStream


class ResultFragment : BaseFragment() {

    lateinit var binding: FragmentResultBinding

    private val viewModel by lazy {
        getViewModel<SpeedBallViewModel>()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = binding(layoutInflater, R.layout.fragment_result, container)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()

        binding.ivResult.setImageURI(viewModel.getUriImage())

        val img: Bitmap = getCapturedImage(viewModel.getUriImage()!!)

        binding.ivResult.setImageBitmap(rotateImageIfRequired(img, viewModel.getUriImage()))

        val speed = if (viewModel.getSpeedUnit()) {
            viewModel.getResult()?.speed?.div(1.6).toString()
        } else {
            viewModel.getResult()?.speed.toString()
        }

        binding.tvSpeedCount.text = speed

        binding.tvStatus.text = viewModel.getResult()?.speed?.let {
            getSpeedStatus(requireContext(), getSpeedType(it)) + " " + getSpeedEmoji(getSpeedType(it))
        }

        binding.tvSpeedParameter.text = if (viewModel.getSpeedUnit()) {
            getString(R.string.mile_h)
        } else {
            getString(R.string.km_h)
        }
    }

    private fun getImageUri(): Uri {
        val outputDirectory = SpeedBallActivity.getOutputDirectory(requireContext())

        val photoFile = SpeedBallActivity.createFile(outputDirectory, SpeedBallActivity.FILENAME, SpeedBallActivity.PHOTO_EXTENSION)

        val os: OutputStream = BufferedOutputStream(FileOutputStream(photoFile))
        screenShot(binding.root)?.compress(Bitmap.CompressFormat.PNG, 100, os)
        os.close()
        os.flush()

        return photoFile.toUri()
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnShare.setOnClickListener {
            val bitmapUri = getImageUri()
            val shareIntent = Intent()

            shareIntent.apply {
                action = Intent.ACTION_SEND
                type = "image/jpg"
                putExtra(Intent.EXTRA_STREAM, bitmapUri)
            }

            startActivityForResult(Intent.createChooser(shareIntent, "Speedball"), Constants.SHARE_IMAGE)
        }
    }

    private fun screenShot(view: View): Bitmap? {
        return Screenshot.with(requireActivity())
            .setView(view)
            .setQuality(Quality.HIGH)
            .setFlip(Flip.NOTHING)
            .getScreenshot()
    }

    private fun rotateImageIfRequired(img : Bitmap, selectedImage: Uri?): Bitmap {
        var orientation: Int? = null

        selectedImage?.path?.let {
            orientation = ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        }

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    private fun getCapturedImage(selectedPhotoUri: Uri): Bitmap {
        return when {
            Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver,
                selectedPhotoUri
            )
            else -> {
                val source = ImageDecoder.createSource(requireActivity().contentResolver, selectedPhotoUri)
                ImageDecoder.decodeBitmap(source)
            }
        }
    }
}