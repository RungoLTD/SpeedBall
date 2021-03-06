package com.myrungo.speedball.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myrungo.speedball.data.model.Result
import com.myrungo.speedball.utils.Converters

@Database(entities = [Result::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun resultDao(): ResultDao
}