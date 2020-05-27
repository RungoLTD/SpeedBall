package com.myrungo.speedball.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity(tableName = "result")
data class Result(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val speed: Int,
    val date: LocalDateTime
)