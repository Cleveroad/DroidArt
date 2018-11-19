package com.cleveroad.droidart.sample

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.cleveroad.bootstrap.kotlin_core.utils.misc.MiscellaneousUtils

class DroidArtApp : Application() {
    companion object {
        lateinit var mInstance: DroidArtApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        MiscellaneousUtils.defaultPackageName = packageName
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}