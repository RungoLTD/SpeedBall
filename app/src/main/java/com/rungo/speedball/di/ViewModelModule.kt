package com.rungo.speedball.di

import com.rungo.speedball.features.speedball.SpeedBallViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SpeedBallViewModel() }
}