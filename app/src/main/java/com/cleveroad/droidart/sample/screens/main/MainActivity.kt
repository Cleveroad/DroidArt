package com.cleveroad.droidart.sample.screens.main

import android.content.Intent
import android.os.Bundle
import com.cleveroad.droidart.sample.R
import com.cleveroad.droidart.sample.screens.base.BSActivity
import com.cleveroad.droidart.sample.screens.main.pick_image.PickImageFragment

class MainActivity : BSActivity<MainPresenter>(), MainView, OnStartActivityForResultListener {
    override val layoutId: Int
        get() = R.layout.activity_main
    override val containerId: Int
        get() = R.id.rootContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState ?: replaceFragment(PickImageFragment.newInstance(), PickImageFragment::class.java.simpleName, false)
    }

    override fun onStartActivityForResult(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
    }

    override fun init() = initPresenter(LoaderId.MAIN(), this) { MainPresenterImpl() }
}