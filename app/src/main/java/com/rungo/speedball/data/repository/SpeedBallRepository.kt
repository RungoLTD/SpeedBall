package com.rungo.speedball.data.repository

import android.net.Uri
import com.rungo.speedball.data.local.SharedPreferencesProvider
import com.rungo.speedball.data.model.Result

class SpeedBallRepository(
    var sharedPreferencesProvider: SharedPreferencesProvider
) {

    var uri: Uri? = null

    var result: Result? = null

    fun setSensitive(sensitive: Int) {
        sharedPreferencesProvider.set("sensitive", sensitive)
    }

    fun getSensitive(): Int? {
        return sharedPreferencesProvider.getSensitive("sensitive")
    }
}