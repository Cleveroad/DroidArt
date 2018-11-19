package com.cleveroad.droidart.sample.screens.main.remove_image_dialog

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.cleveroad.bootstrap.kotlin_core.utils.misc.MiscellaneousUtils.getExtra
import com.cleveroad.droidart.sample.R
import com.cleveroad.droidart.sample.models.ActionType
import com.cleveroad.droidart.sample.models.DialogSize
import com.cleveroad.droidart.sample.screens.base.BSDialogFragment
import kotlinx.android.synthetic.main.dialog_fragment_remove_image.*

class RemoveImageDialogFragment : BSDialogFragment(), View.OnClickListener {
    override val layoutId: Int
        get() = R.layout.dialog_fragment_remove_image
    override val gravity: Int
        get() = Gravity.BOTTOM
    override val dialogSize: DialogSize
        get() = DialogSize(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    companion object {
        val ACTION_EXTRA = getExtra("PICK_IMAGE_TYPE", RemoveImageDialogFragment::class.java)

        fun newInstance(): RemoveImageDialogFragment = RemoveImageDialogFragment().apply {
            arguments = Bundle()
        }

        fun newInstance(targetFragment: Fragment, requestCode: Int): RemoveImageDialogFragment =
                newInstance().apply { setTargetFragment(targetFragment, requestCode) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners(this, llRemoveImage, llCancelDialog, llMoveToCenter)
    }

    override fun onResume() {
        super.onResume()
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.llRemoveImage -> callOnActivityResultTargetFragment(ActionType.DELETE)
            R.id.llMoveToCenter -> callOnActivityResultTargetFragment(ActionType.RESET)
        }
        dismiss()
    }

    private fun callOnActivityResultTargetFragment(actionType: ActionType) {
        targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, Intent().putExtra(ACTION_EXTRA, actionType))
    }
}
