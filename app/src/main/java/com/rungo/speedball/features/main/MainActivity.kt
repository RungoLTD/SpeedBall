package com.rungo.speedball.features.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.rungo.speedball.R
import com.rungo.speedball.databinding.ActivityMainBinding
import com.rungo.speedball.features.base.BaseActivity
import com.rungo.speedball.utils.showToast

class MainActivity : BaseActivity() {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnStart.setOnClickListener {
            showToast("START")
        }

        binding.btnCalculate.setOnClickListener {
            showToast("CALCULATE")
        }

        binding.ivSettings.setOnClickListener {
            showToast("SETTINGS")
        }

        binding.ivStatistics.setOnClickListener {
            showToast("STATISTICS")
        }
    }
}
