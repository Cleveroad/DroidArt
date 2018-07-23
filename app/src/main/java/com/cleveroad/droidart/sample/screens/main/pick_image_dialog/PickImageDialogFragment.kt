package com.cleveroad.droidart.sample.screens.main.pick_image_dialog

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.cleveroad.bootstrap.kotlin_core.utils.misc.MiscellaneousUtils.getExtra
import com.cleveroad.droidart.sample.R
import com.cleveroad.droidart.sample.models.DialogSize
import com.cleveroad.droidart.sample.models.PickImageType
import com.cleveroad.droidart.sample.screens.base.BSDialogFragment
import kotlinx.android.synthetic.main.dialog_fragment_pick_photo.*

class PickImageDialogFragment : BSDialogFragment(), View.OnClickListener {
    override val layoutId: Int
        get() = R.layout.dialog_fragment_pick_photo
    override val gravity: Int
        get() = Gravity.BOTTOM
    override val dialogSize: DialogSize
        get() = DialogSize(ViewGroup.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.pick_image_dialog_width))

    companion object {
        val PICK_IMAGE_EXTRA = getExtra("PICK_IMAGE_TYPE", PickImageDialogFragment::class.java)

        fun newInstance() = PickImageDialogFragment().apply {
            arguments = Bundle()
        }

        fun newInstance(targetFragment: Fragment, requestCode: Int) =
                newInstance().apply { setTargetFragment(targetFragment, requestCode) }
    }

    override fun onResume() {
        super.onResume()
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners(this, vUploadFromGallery, vUseCamera, vCancel)
    }

    private fun callOnActivityResultTargetFragment(type: PickImageType) {
        targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, Intent().putExtra(PICK_IMAGE_EXTRA, type))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.vUploadFromGallery -> callOnActivityResultTargetFragment(PickImageType.GALLERY)
            R.id.vUseCamera -> callOnActivityResultTargetFragment(PickImageType.CAMERA)
        }
        dismiss()
    }

}