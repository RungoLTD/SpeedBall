package com.rungo.speedball

import android.app.Application
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        setupTimber()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.uprootAll()
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    var newTag = tag
                    if (tag != null) newTag = "RUNGO: $tag"
                    super.log(priority, newTag, message, t)
                }
            })
        }
    }
}