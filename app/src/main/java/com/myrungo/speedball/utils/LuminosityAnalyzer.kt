package com.myrungo.speedball.utils

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList

typealias LumaListener = (luma: Double) -> Unit

/*
    * Our custom image analysis class.
    *
    * <p>All we need to do is override the function `analyze` with our desired operations. Here,
    * we compute the average luminosity of the image by looking at the Y plane of the YUV frame.
    */
class LuminosityAnalyzer(listener: LumaListener? = null) : ImageAnalysis.Analyzer {
    private val frameRateWindow = 8
    private val frameTimestamps = ArrayDeque<Long>(5)
    private val listeners = ArrayList<LumaListener>().apply { listener?.let { add(it) } }
    private var lastAnalyzedTimestamp = 0L
    private var framesPerSecond: Double = -1.0

    fun onFrameAnalyzed(listener: LumaListener) = listeners.add(listener)

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }

    override fun analyze(image: ImageProxy) {
        if (listeners.isEmpty()) {
            image.close()
            return
        }

        val currentTime = System.currentTimeMillis()
        frameTimestamps.push(currentTime)

        while (frameTimestamps.size >= frameRateWindow) frameTimestamps.removeLast()
        val timestampFirst = frameTimestamps.peekFirst() ?: currentTime
        val timestampLast = frameTimestamps.peekLast() ?: currentTime
        framesPerSecond = 1.0 / ((timestampFirst - timestampLast) /
                frameTimestamps.size.coerceAtLeast(1).toDouble()) * 1000.0

        lastAnalyzedTimestamp = frameTimestamps.first

        val buffer = image.planes[0].buffer

        val data = buffer.toByteArray()

        val pixels = data.map { it.toInt() and 0xFF }

        val luma = pixels.average()

        listeners.forEach { it(luma) }

        image.close()
    }
}
