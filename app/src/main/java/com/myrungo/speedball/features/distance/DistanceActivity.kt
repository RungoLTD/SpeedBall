package com.myrungo.speedball.features.distance

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.myrungo.speedball.R
import com.myrungo.speedball.databinding.ActivityDistanceBinding
import com.myrungo.speedball.features.base.BaseActivity
import com.myrungo.speedball.utils.Constants
import com.myrungo.speedball.utils.showToast
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class DistanceActivity : BaseActivity(), LocationListener {

    private val binding : ActivityDistanceBinding by binding(R.layout.activity_distance)

    private lateinit var locationManager: LocationManager

    private var isStarted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComponents()
        setupListeners()
    }

    private var lastLocation : Location? = null

    @SuppressLint("MissingPermission")
    private fun initComponents() {
        locationManager =  this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (checkPermissions()) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_CHANGE_FOR_UPDATE.toFloat(), this)
        } else {
            AlertDialog.Builder(this)
                .setTitle(R.string.alert_title)
                .setMessage(R.string.alert_message_gps)
                .setPositiveButton(R.string.alert_ok) { dialog, _ ->
                    requestPermissions()
                    dialog?.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Constants.REQUEST_CODE
        )
    }

    private fun setupListeners() {
        binding.btnStartClose.setOnClickListener {
            if (isStarted) {
                finish()
                return@setOnClickListener
            }
            showToast(getString(R.string.move))
            binding.btnStartClose.text = getString(R.string.close)
            isStarted = true
        }
    }

    private fun checkPermissions(): Boolean {
        return when {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED -> false
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED -> false
            else -> true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location?) {
        if (isStarted) {
            val meters: Double = distance(lastLocation!!.latitude, lastLocation!!.longitude, location?.latitude!!, location.longitude)
            binding.tvMeters.text = String.format("%.2f", meters) + " м."
        } else {
            lastLocation = location
            binding.btnStartClose.isEnabled = true

            binding.tvMeters.visibility = View.VISIBLE
            binding.progress.visibility = View.GONE
            binding.gpsTitle.visibility = View.GONE
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1)) * sin(deg2rad(lat2)) + (cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.REQUEST_CODE && grantResults.isNotEmpty()) {
            if (grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
                showToast(getString(R.string.alert_message_gps))
                finish()
            } else {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_CHANGE_FOR_UPDATE.toFloat(), this)
            }
        }
    }

    companion object {
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATE: Long = 1
        private const val MIN_TIME_FOR_UPDATE: Long = 1000
    }
}