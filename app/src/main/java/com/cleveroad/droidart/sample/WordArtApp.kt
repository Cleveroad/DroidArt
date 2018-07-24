package com.cleveroad.droidart.sample

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.cleveroad.bootstrap.kotlin_core.utils.misc.MiscellaneousUtils

class WordArtApp : Application() {
    companion object {
        lateinit var instance: WordArtApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MiscellaneousUtils.defaultPackageName = packageName
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}