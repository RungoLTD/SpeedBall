package com.myrungo.speedball.data.repository

import android.net.Uri
import com.myrungo.speedball.data.local.SharedPreferencesProvider
import com.myrungo.speedball.data.local.db.ResultDao
import com.myrungo.speedball.data.model.Result

class SpeedBallRepository(
    private val sharedPreferencesProvider: SharedPreferencesProvider,
    private val resultDao: ResultDao
) {

    var uri: Uri? = null

    var result: Result? = null

    fun setSensitive(sensitive: Int) {
        sharedPreferencesProvider.set("sensitive", sensitive)
    }

    fun getSensitive(): Int? {
        return sharedPreferencesProvider.getSensitive("sensitive")
    }

    fun getSpeedUnit(): Boolean {
        return sharedPreferencesProvider.getBoolean("speedUnit")
    }

    fun setSpeedUnit(speedUnit: Boolean) {
        sharedPreferencesProvider.set("speedUnit", speedUnit)
    }

    suspend fun setCurrentResult(result: Result) {
        resultDao.insertResult(result)
    }

    suspend fun getResultList() : List<Result> {
        return resultDao.getResultList()
    }
}