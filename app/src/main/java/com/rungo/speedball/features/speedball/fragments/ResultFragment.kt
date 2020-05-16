package com.rungo.speedball.features.speedball.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rungo.speedball.R
import com.rungo.speedball.data.model.getSpeedEmoji
import com.rungo.speedball.data.model.getSpeedStatus
import com.rungo.speedball.data.model.getSpeedType
import com.rungo.speedball.databinding.FragmentResultBinding
import com.rungo.speedball.features.base.BaseFragment
import com.rungo.speedball.features.speedball.SpeedBallViewModel
import org.koin.android.viewmodel.ext.android.getViewModel
import java.text.DecimalFormat

class ResultFragment : BaseFragment() {

    lateinit var binding: FragmentResultBinding

    private val viewModel by lazy {
        getViewModel<SpeedBallViewModel>()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = binding(layoutInflater, R.layout.fragment_result, container)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()

        binding.ivResult.setImageURI(viewModel.getUriImage())

        val speed = if (viewModel.getSpeedUnit()) {
            viewModel.getResult()?.speed?.div(1.6).toString()
        } else {
            viewModel.getResult()?.speed.toString()
        }

        binding.tvSpeedCount.text = speed

        binding.tvStatus.text = viewModel.getResult()?.speed?.let {
            getSpeedStatus(getSpeedType(it)) + " " + getSpeedEmoji(getSpeedType(it))
        }

        binding.tvSpeedParameter.text = if (viewModel.getSpeedUnit()) {
            getString(R.string.mile_h)
        } else {
            getString(R.string.km_h)
        }
    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnShare.setOnClickListener {

        }
    }
}