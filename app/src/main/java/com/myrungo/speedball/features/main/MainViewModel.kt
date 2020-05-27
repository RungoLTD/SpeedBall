package com.myrungo.speedball.features.main

import com.myrungo.speedball.data.repository.SpeedBallRepository
import com.myrungo.speedball.features.base.BaseViewModel

class MainViewModel(
    private val speedBallRepository: SpeedBallRepository
) : BaseViewModel() {

    fun setSensitive(progress: Int) {
        speedBallRepository.setSensitive(progress)
    }

    fun getSensitive(): Int? {
        return speedBallRepository.getSensitive()
    }

    fun getSpeedUnit(): Boolean {
        return speedBallRepository.getSpeedUnit()
    }

    fun setSpeedUnit(speedUnit: Boolean) {
        speedBallRepository.setSpeedUnit(speedUnit)
    }
}