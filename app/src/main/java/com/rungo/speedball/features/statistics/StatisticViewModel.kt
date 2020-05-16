package com.rungo.speedball.features.statistics

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rungo.speedball.data.model.Result
import com.rungo.speedball.data.repository.SpeedBallRepository
import com.rungo.speedball.features.base.BaseViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class StatisticViewModel(
    private val speedBallRepository: SpeedBallRepository
) : BaseViewModel() {

    var resultList = MutableLiveData<List<Result>>().apply { value = null }

    init {
        getResultList()
    }

    private fun getResultList() {
        viewModelScope.launch {
            resultList.value = speedBallRepository.getResultList()
            Timber.d("RESULT LIST $resultList")
        }
    }

    fun getSpeedUnit(): Boolean {
        return speedBallRepository.getSpeedUnit()
    }
}