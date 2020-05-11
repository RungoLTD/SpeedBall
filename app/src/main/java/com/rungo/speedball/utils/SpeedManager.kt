package com.rungo.speedball.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.core.app.ActivityCompat

class SpeedManager(private val context: Activity, private val sensitive: Int, val listener: SpeedManagerListener) : Runnable {

    private var requiredAmplitude: Int? = null
    private var topRequiredAmplitude = 22000
    private var recorder: MediaRecorder? = null
    private var isListening = true
    private var firstDetectedTime: Long = -1
    private var lastDetectedTime: Long = -1


    init {
        requiredAmplitude = topRequiredAmplitude + 3000 - topRequiredAmplitude / 100 * sensitive
    }

    override fun run() {
        init()
    }


    private fun init() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            context.runOnUiThread { listener.requestPermission() }
            return
        }
        try {
            recorder = MediaRecorder()
            recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            recorder?.setOutputFile("/dev/null")
            recorder?.prepare()
            recorder?.start()
            runLooper()
        } catch (e: Exception) {
            context.runOnUiThread { listener.onError(e.localizedMessage) }
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    private fun runLooper() {
        while (isListening) {
            Thread.sleep(10)
            val amplitude: Int? = recorder?.maxAmplitude
            println(javaClass.name + " " + amplitude + " | " + requiredAmplitude)
            if (amplitude != null) {
                if (amplitude > requiredAmplitude!!) {
                    if (this.firstDetectedTime == -1L) {
                        this.firstDetectedTime = System.currentTimeMillis()
                        println(javaClass.name + ": Detected first high value: " + this.firstDetectedTime)
                        context.runOnUiThread { listener.didDetectedFirstTime() }
                    } else {
                        this.lastDetectedTime = System.currentTimeMillis()
                        println(javaClass.name + ": Detected last high value: " + this.lastDetectedTime)
                        val currentDelay: Long = lastDetectedTime - firstDetectedTime
                        val delayTime = 150
                        if (currentDelay > delayTime) {
                            this.isListening = false
                            flush()
                            context.runOnUiThread {
                                listener.didDetectedLastTime(
                                    firstDetectedTime,
                                    lastDetectedTime,
                                    currentDelay
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun flush() {
        recorder?.stop()
        recorder?.release()
        recorder = null
    }
}