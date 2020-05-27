package com.myrungo.speedball

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.databinding.library.BuildConfig
import com.jakewharton.threetenabp.AndroidThreeTen
import com.myrungo.speedball.di.persistenceModule
import com.myrungo.speedball.di.repositoryModule
import com.myrungo.speedball.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        setupModules()
        setupPolicy()
        AndroidThreeTen.init(this);
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

    private fun setupPolicy() {
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }
}