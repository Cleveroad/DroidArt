package com.cleveroad.droidart.sample.screens.main

import android.content.Intent

interface OnStartActivityForResultListener {

    fun onStartActivityForResult(intent: Intent, requestCode: Int)

}