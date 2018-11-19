package com.cleveroad.droidart.sample.screens.main.create_word_dialog

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.cleveroad.bootstrap.kotlin_core.utils.misc.MiscellaneousUtils.getExtra
import com.cleveroad.colorpicker.CircleProperty
import com.cleveroad.colorpicker.ColorPickerAdapter
import com.cleveroad.colorpicker.OnSelectedColorListener
import com.cleveroad.colorpicker.R.array.material_colors
import com.cleveroad.droidart.sample.R
import com.cleveroad.droidart.sample.models.DialogSize
import com.cleveroad.droidart.sample.screens.base.BSDialogFragment
import kotlinx.android.synthetic.main.dialog_fragment_create_word.*
import java.lang.ref.WeakReference

class CreateWordDialogFragment : BSDialogFragment(), OnSelectedColorListener, View.OnClickListener {
    override val layoutId: Int
        get() = R.layout.dialog_fragment_create_word
    override val gravity: Int
        get() = Gravity.CENTER
    override val dialogSize: DialogSize
        get() = DialogSize(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    private var colorPickerAdapter: ColorPickerAdapter? = null

    companion object {
        val WORD_EXTRA = getExtra("WORD", CreateWordDialogFragment::class.java)
        val WORD_COLOR_EXTRA = getExtra("WORD_COLOR", CreateWordDialogFragment::class.java)
        val WORD_EDIT_EXTRA = getExtra("WORD_EDIT", CreateWordDialogFragment::class.java)

        private const val OFFSET_ITEM_POSITION = 1

        fun newInstance(targetFragment: Fragment, requestCode: Int, word: String? = null, @ColorInt currentColor: Int = Color.WHITE): CreateWordDialogFragment =
                CreateWordDialogFragment().apply {
                    arguments = Bundle().apply {
                        word?.let { putString(WORD_EDIT_EXTRA, it) }
                        putInt(WORD_COLOR_EXTRA, currentColor)
                    }
                    setTargetFragment(targetFragment, requestCode)
                }
    }

    override fun onResume() {
        super.onResume()
        etWord.requestFocus()
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onSelectedColor(color: Int) {
        etWord.setTextColor(color)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners(this, ivBack, tvDone)

        arguments?.takeIf { it.containsKey(WORD_EDIT_EXTRA) }?.let { etWord.setText(it.getString(WORD_EDIT_EXTRA)) }
        arguments?.takeIf { it.containsKey(WORD_COLOR_EXTRA) }?.let { etWord.setTextColor(it.getInt(WORD_COLOR_EXTRA)) }

        context?.let {
            rvColorPicker.layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
            ).apply { scrollToPositionWithOffset(OFFSET_ITEM_POSITION, resources.getDimensionPixelOffset(R.dimen.item_offset)) }
            colorPickerAdapter = ColorPickerAdapter(
                    it,
                    it.resources.getIntArray(material_colors).map { CircleProperty(it, Color.WHITE) },
                    WeakReference(this))
            rvColorPicker.adapter = colorPickerAdapter
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvDone -> {
                val text = etWord.text.toString().trim()
                if (text.isNotBlank()) {
                    etWord.clearFocus()
                    targetFragment?.onActivityResult(
                            targetRequestCode,
                            RESULT_OK,
                            Intent().putExtra(WORD_EXTRA, text)
                                    .putExtra(WORD_COLOR_EXTRA, etWord.currentTextColor))
                    dismiss()
                } else {
                    showEmptyTextError()
                }
            }
            R.id.ivBack -> {
                targetFragment?.onActivityResult(targetRequestCode, RESULT_CANCELED, Intent())
                dismiss()
            }
        }
    }

    override fun onCancel(dialog: DialogInterface?) {
        targetFragment?.onActivityResult(targetRequestCode, RESULT_CANCELED, Intent())
        super.onCancel(dialog)
    }

    override fun onStop() {
        activity?.let {
            (it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(it.currentFocus?.windowToken, 0)
        }
        super.onStop()
    }

    private fun showEmptyTextError(){
        Toast.makeText(context, R.string.empty_text_error, Toast.LENGTH_SHORT).show()
    }
}
