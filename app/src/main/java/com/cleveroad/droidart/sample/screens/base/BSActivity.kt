package com.cleveroad.droidart.sample.screens.base

import android.os.Bundle
import com.cleveroad.bootstrap.kotlin_mvp_loader.BSAbstractActivityMvpView
import com.cleveroad.bootstrap.kotlin_mvp_loader.BSMvpPresenter

abstract class BSActivity<P : BSMvpPresenter<*>> : BSAbstractActivityMvpView<P>() {
    companion object {
        private var currentLoaderId = 0
    }

    protected abstract val layoutId: Int

    protected enum class LoaderId {
        MAIN;

        private val value = ++currentLoaderId

        operator fun invoke() = value
    }

    abstract fun init()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        init()
    }
}