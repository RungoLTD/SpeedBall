package com.rungo.speedball.features.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.rungo.speedball.R
import com.rungo.speedball.databinding.ActivityMainBinding
import com.rungo.speedball.features.base.BaseActivity
import com.rungo.speedball.features.speedball.SpeedBallActivity
import com.rungo.speedball.utils.Constants
import com.rungo.speedball.utils.showToast

class MainActivity : BaseActivity() {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnStart.setOnClickListener {
            if (checkPermissions()) {
                openSpeedBallScreen()
            } else {
                AlertDialog.Builder(this)
                    .setTitle(R.string.alert_title)
                    .setMessage(R.string.alert_message)
                    .setPositiveButton(R.string.alert_ok) { dialog, _ ->
                        requestPermissions()
                        dialog?.dismiss()
                    }
                    .create()
                    .show()
            }
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

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            Constants.REQUEST_CODE
        )
    }

    private fun checkPermissions(): Boolean {
        return when {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED -> {
                false
            }
            ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> {
                false
            }
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> {
                false
            }
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED -> {
                false
            } else -> {
                true
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults.any {it != PackageManager.PERMISSION_GRANTED }) {
                showToast("FAIL")
            } else {
                openSpeedBallScreen()
            }
        }
    }

    private fun openSpeedBallScreen() {
        val intent = Intent(this, SpeedBallActivity::class.java)
        startActivity(intent)
    }
}
