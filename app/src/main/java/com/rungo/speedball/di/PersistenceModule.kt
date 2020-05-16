package com.rungo.speedball.di

import androidx.room.Room
import com.rungo.speedball.R
import com.rungo.speedball.data.local.SharedPreferencesProvider
import com.rungo.speedball.data.local.db.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val persistenceModule = module {

    single {
        Room
            .databaseBuilder(
                androidApplication(), AppDatabase::class.java,
                androidApplication().getString(R.string.database)
            )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().resultDao() }

    single { SharedPreferencesProvider(androidApplication()) }
}