package com.rungo.speedball.di

import com.rungo.speedball.data.repository.SpeedBallRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { SpeedBallRepository(get()) }
}