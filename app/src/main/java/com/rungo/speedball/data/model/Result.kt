package com.rungo.speedball.data.model

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

data class Result(
    val speed: Int,
    val date: LocalDateTime
) {

    fun getFormattedDate(): String = date.format(formatter)
}

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
