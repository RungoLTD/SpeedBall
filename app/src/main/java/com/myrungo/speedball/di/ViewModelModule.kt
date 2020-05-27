package com.myrungo.speedball.di

import com.myrungo.speedball.features.main.MainViewModel
import com.myrungo.speedball.features.speedball.SpeedBallViewModel
import com.myrungo.speedball.features.statistics.StatisticViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SpeedBallViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { StatisticViewModel(get()) }
}