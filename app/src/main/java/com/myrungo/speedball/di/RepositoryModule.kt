package com.myrungo.speedball.di

import com.myrungo.speedball.data.repository.SpeedBallRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { SpeedBallRepository(get(), get()) }
}