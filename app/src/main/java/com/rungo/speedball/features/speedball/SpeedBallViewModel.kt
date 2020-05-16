package com.rungo.speedball.features.speedball

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.rungo.speedball.data.model.Result
import com.rungo.speedball.data.repository.SpeedBallRepository
import com.rungo.speedball.features.base.BaseViewModel
import kotlinx.coroutines.launch

class SpeedBallViewModel constructor(
    private var repository: SpeedBallRepository
) : BaseViewModel() {

    var sensitive = repository.getSensitive()

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