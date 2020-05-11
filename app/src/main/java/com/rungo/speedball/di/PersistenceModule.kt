package com.rungo.speedball.di

import com.rungo.speedball.data.local.SharedPreferencesProvider
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val persistenceModule =  module {

    single { SharedPreferencesProvider(androidApplication()) }
}