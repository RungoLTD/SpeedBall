package com.rungo.speedball.features.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.rungo.speedball.R
import com.rungo.speedball.databinding.ActivityMainBinding
import com.rungo.speedball.databinding.DialogSettingsBinding
import com.rungo.speedball.features.base.BaseActivity
import com.rungo.speedball.features.distance.DistanceActivity
import com.rungo.speedball.features.speedball.SpeedBallActivity
import com.rungo.speedball.features.statistics.StatisticActivity
import com.rungo.speedball.utils.Constants
import com.rungo.speedball.utils.showToast
import jp.pocket7878.switcherview.SwitcherView
import org.koin.android.viewmodel.ext.android.getViewModel

class MainActivity : BaseActivity() {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)

    private val viewModel by lazy { getViewModel<MainViewModel>() }

    private val settingsDialog by lazy {
        AlertDialog.Builder(this).create()
    }

    private val dialogBinding by lazy {
        DialogSettingsBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupListeners()
        intiComponents()
    }

    private fun setPercent(progress: String): String {
        return String.format("$progress %s", "%")
    }

    private fun intiComponents() {
        viewModel.getSensitive()?.let {
            dialogBinding.tvPercent.text = setPercent(it.toString())
            dialogBinding.sbSensitive.progress = it
        }

        if (viewModel.getSpeedUnit()) {
            dialogBinding.switchToggle.switchToRightChoice()
        } else {
            dialogBinding.switchToggle.switchToLeftChoice()
        }
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
                    .setCancelable(false)
                    .create()
                    .show()
            }
        }

        binding.btnCalculate.setOnClickListener {
            val intent = Intent(this, DistanceActivity::class.java)
            startActivity(intent)
        }

        binding.ivSettings.setOnClickListener {
            settingsDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            settingsDialog.setView(dialogBinding.root)
            settingsDialog.show()
        }

        binding.ivStatistics.setOnClickListener {
            val intent = Intent(this, StatisticActivity::class.java)
            startActivity(intent)
        }

        dialogBinding.ivClose.setOnClickListener {
            settingsDialog.dismiss()
        }

        dialogBinding.sbSensitive.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                dialogBinding.tvPercent.text = setPercent(progress.toString())
                viewModel.setSensitive(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        dialogBinding.switchToggle.setOnSwitchSelectChangeListener(object : SwitcherView.OnSwitchSelectChangeListener {
            override fun onFinishSwitchUserControl() {}

            override fun onLeftChoiceSelected() {
                viewModel.setSpeedUnit(false)
            }

            override fun onRightChoiceSelected() {
                viewModel.setSpeedUnit(true)
            }

            override fun onStartSwitchUserControl() {}
        })
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            Constants.REQUEST_CODE
        )
    }

    private fun checkPermissions(): Boolean {
        return when {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED -> false
            ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> false
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED -> false
            else -> true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
                showToast(getString(R.string.alert_message))
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
