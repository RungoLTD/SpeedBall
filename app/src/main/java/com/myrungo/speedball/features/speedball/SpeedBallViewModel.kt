package com.myrungo.speedball.features.speedball

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myrungo.speedball.data.model.Result
import com.myrungo.speedball.data.repository.SpeedBallRepository
import com.myrungo.speedball.features.base.BaseViewModel
import kotlinx.coroutines.launch

class SpeedBallViewModel constructor(
    private var repository: SpeedBallRepository
) : BaseViewModel() {

    var sensitive = repository.getSensitive()

    var isPhotoCompleted = MutableLiveData<Boolean>().apply { value = false }
    var isSpeedDetected = MutableLiveData<Boolean>().apply { value = false }

    fun setUriImage(uri: Uri) {
        repository.uri = uri
    }

    fun getUriImage(): Uri? {
        return repository.uri
    }

    fun getResult(): Result? {
        return repository.result
    }

    fun getSpeedUnit(): Boolean {
        return repository.getSpeedUnit()
    }

    fun setResult(result: Result) {
        repository.result = result
        viewModelScope.launch {
            repository.setCurrentResult(result)
        }
    }
}