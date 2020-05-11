package com.rungo.speedball.data.model

fun getDescription(speed: Int): String? {
    return when {
        speed < 40 -> "Отстой"
        speed < 80 -> "Нормально"
        speed < 100 -> "Красава"
        speed < 115 -> "Огонь"
        speed < 130 -> "Пушка"
        else -> "Пуля!"
    }
}