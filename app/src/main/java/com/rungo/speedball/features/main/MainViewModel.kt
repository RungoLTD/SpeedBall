package com.rungo.speedball.features.main

import com.rungo.speedball.data.repository.SpeedBallRepository
import com.rungo.speedball.features.base.BaseViewModel

class MainViewModel(
    private val speedBallRepository: SpeedBallRepository
) : BaseViewModel() {

    fun setSensitive() {
        if (speedBallRepository.getSensitive() == null) {
            speedBallRepository.setSensitive(50)
        }
    }
}