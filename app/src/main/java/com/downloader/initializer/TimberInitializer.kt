package com.downloader.initializer

import android.content.Context
import androidx.startup.Initializer
import com.downloader.BuildConfig
import timber.log.Timber

/**
 * Created by Rahul Abrol on 15/9/20.
 */
class TimberInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.d("TimberInitializer is initialized.")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}