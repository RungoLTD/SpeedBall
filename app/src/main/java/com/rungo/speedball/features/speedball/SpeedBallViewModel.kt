package com.rungo.speedball.features.speedball

import android.net.Uri
import com.rungo.speedball.data.model.Result
import com.rungo.speedball.data.repository.SpeedBallRepository
import com.rungo.speedball.features.base.BaseViewModel

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

    fun setResult(result: Result) {
        repository.result = result
    }

    fun getResult(): Result? {
        return repository.result
    }
}