package com.cleveroad.droidwordart.screens.main.remove_image_dialog

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.cleveroad.droidwordart.R
import com.cleveroad.droidwordart.models.DialogSize
import com.cleveroad.droidwordart.screens.base.BSDialogFragment
import kotlinx.android.synthetic.main.dialog_fragment_remove_image.*

class RemoveImageDialogFragment : BSDialogFragment(), View.OnClickListener {
    override val layoutId: Int
        get() = R.layout.dialog_fragment_remove_image
    override val gravity: Int
        get() = Gravity.BOTTOM
    override val dialogSize: DialogSize
        get() = DialogSize(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    companion object {

        fun newInstance(): RemoveImageDialogFragment = RemoveImageDialogFragment().apply {
            arguments = Bundle()
        }

        fun newInstance(targetFragment: Fragment, requestCode: Int): RemoveImageDialogFragment =
                newInstance().apply { setTargetFragment(targetFragment, requestCode) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners(this, llRemoveImage, llCancelDialog)
    }

    override fun onResume() {
        super.onResume()
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.llRemoveImage) targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, Intent())
        dismiss()
    }
}
