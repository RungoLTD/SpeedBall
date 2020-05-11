package com.rungo.speedball.utils

interface SpeedManagerListener {
    fun requestPermission()
    fun onError(error: String?)
    fun didDetectedFirstTime()
    fun didDetectedLastTime(firstTime: Long, lastTime: Long, delay: Long)
}