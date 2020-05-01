package com.rungo.speedball.features.speedball

import android.os.Bundle
import com.rungo.speedball.R
import com.rungo.speedball.databinding.ActivitySpeedballBinding
import com.rungo.speedball.features.base.BaseActivity
import com.rungo.speedball.utils.showToast

class SpeedBallActivity : BaseActivity() {

    private val binding: ActivitySpeedballBinding by binding(R.layout.activity_speedball)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupListeners()
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

    }
}