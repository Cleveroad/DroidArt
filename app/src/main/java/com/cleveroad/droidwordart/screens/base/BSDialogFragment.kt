package com.cleveroad.droidwordart.screens.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.cleveroad.bootstrap.kotlin_mvp_loader.BSAbstractDialogFragment
import com.cleveroad.droidwordart.models.DialogSize

abstract class BSDialogFragment : BSAbstractDialogFragment() {

    protected abstract val layoutId: Int

    protected abstract val gravity: Int

    protected abstract val dialogSize: DialogSize

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) =
            super.onCreateDialog(savedInstanceState).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
            }

    @SuppressLint("MissingSuperCall")
    override fun onResume() {
        super.onResume()
        with(dialog.window) {
            attributes.gravity = gravity
            setLayout(dialogSize.width, dialogSize.height)
        }
    }

    protected fun setClickListeners(listener: View.OnClickListener, vararg views: View) {
        views.forEach { view -> view.setOnClickListener(listener) }
    }
}