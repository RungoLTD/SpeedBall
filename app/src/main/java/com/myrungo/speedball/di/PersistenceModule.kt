package com.myrungo.speedball.di

import androidx.room.Room
import com.myrungo.speedball.R
import com.myrungo.speedball.data.local.SharedPreferencesProvider
import com.myrungo.speedball.data.local.db.AppDatabase
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