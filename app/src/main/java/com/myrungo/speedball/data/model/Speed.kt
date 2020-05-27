package com.myrungo.speedball.data.model

import android.content.Context
import com.myrungo.speedball.R

fun getSpeedType(speed: Int): SpeedType {
    return when {
        speed < 40 -> SpeedType.TOO_BAD
        speed < 80 -> SpeedType.NORMAL
        speed < 100 -> SpeedType.GOOD
        speed < 115 -> SpeedType.PERFECT
        speed < 130 -> SpeedType.DOMINATING
        else -> SpeedType.IMPOSABLE
    }
}

fun getSpeedStatus(context: Context, type: SpeedType): String {
    return when (type) {
        SpeedType.TOO_BAD -> context.getString(R.string.sucks)
        SpeedType.NORMAL -> context.getString(R.string.fine)
        SpeedType.GOOD -> context.getString(R.string.nice)
        SpeedType.PERFECT -> context.getString(R.string.fire)
        SpeedType.DOMINATING -> context.getString(R.string.gun)
        SpeedType.IMPOSABLE -> context.getString(R.string.bullet)
    }
}

fun getSpeedEmoji(type: SpeedType): String {
    return when (type) {
        SpeedType.TOO_BAD -> String(Character.toChars(0x1F44E))
        SpeedType.NORMAL -> String(Character.toChars(0x1F44D))
        SpeedType.GOOD -> String(Character.toChars(0x263A))
        SpeedType.PERFECT -> String(Character.toChars(0x1F60E))
        SpeedType.DOMINATING -> String(Character.toChars(0x1F525))
        SpeedType.IMPOSABLE -> String(Character.toChars(0x1F680))
    }
}

enum class SpeedType {
    TOO_BAD,
    NORMAL,
    GOOD,
    PERFECT,
    DOMINATING,
    IMPOSABLE
}

fun getSpeedUnit(context: Context, boolean: Boolean): String {
    return if (boolean) {
        context.getString(R.string.mile_h)
    } else {
        context.getString(R.string.km_h)
    }
}