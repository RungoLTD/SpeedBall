package com.rungo.speedball

import android.app.Application
import com.rungo.speedball.di.persistenceModule
import com.rungo.speedball.di.repositoryModule
import com.rungo.speedball.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        setupModules()
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

    private fun setupModules() {
        startKoin {
            androidContext(this@App)
            modules(persistenceModule)
            modules(repositoryModule)
            modules(viewModelModule)
        }
    }
}