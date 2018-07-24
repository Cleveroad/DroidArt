package com.cleveroad.droidart.sample.screens.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cleveroad.bootstrap.kotlin_mvp_loader.BSAbstractFragmentMvpView
import com.cleveroad.bootstrap.kotlin_mvp_loader.BSMvpPresenter
import com.cleveroad.bootstrap.kotlin_mvp_loader.BSMvpView
import com.cleveroad.droidart.sample.utils.exception.NotImplementedInterfaceException

abstract class BSFragment<TPresenter : BSMvpPresenter<*>> : BSAbstractFragmentMvpView<TPresenter>(),
        BSMvpView<TPresenter> {

    companion object {
        private var currentLoaderId = 0
        private var requestCode = 1
    }

    protected enum class LoaderId {
        PICK_IMAGE;

        private val value = currentLoaderId++

        operator fun invoke() = value
    }

    enum class RequestCode {
        REQUEST_DIALOG_PICK_IMAGE,
        REQUEST_WRITE_EXTERNAL_STORAGE,
        REQUEST_PICK_IMAGE_FROM_GALLERY,
        REQUEST_CAMERA,
        REQUEST_PICK_IMAGE_FROM_CAMERA,
        REQUEST_CREATE_WORD,
        REQUEST_DIALOG_REMOVE_IMAGE,
        REQUEST_EXPORT_IMAGE;

        private val value = requestCode++

        operator fun invoke() = value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(containerId, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        init()
    }

    abstract fun init()

    protected inline fun <reified T> bindInterfaceOrThrow(vararg objects: Any?): T =
            objects.find { it is T }?.let { it as T } ?: throw NotImplementedInterfaceException(T::class.java)

    protected fun setClickListeners(listener: View.OnClickListener, vararg views: View) {
        views.forEach { view -> view.setOnClickListener(listener) }
    }
}