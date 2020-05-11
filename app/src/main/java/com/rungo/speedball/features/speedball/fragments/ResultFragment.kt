package com.rungo.speedball.features.speedball.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rungo.speedball.R
import com.rungo.speedball.databinding.FragmentResultBinding
import com.rungo.speedball.features.base.BaseFragment
import com.rungo.speedball.features.speedball.SpeedBallActivity
import java.io.File
import java.io.FileOutputStream

class ResultFragment : BaseFragment() {

    lateinit var binding: FragmentResultBinding
    private lateinit var outputDirectory: File

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = binding(layoutInflater, R.layout.fragment_result, container)

        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        outputDirectory =
            SpeedBallActivity.createFile(
                SpeedBallActivity.getOutputDirectory(requireContext()),
                SpeedBallActivity.FILENAME,
                SpeedBallActivity.PHOTO_EXTENSION
            )

        val outputStream = FileOutputStream(outputDirectory)

        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnShare.setOnClickListener {

        }
    }
}