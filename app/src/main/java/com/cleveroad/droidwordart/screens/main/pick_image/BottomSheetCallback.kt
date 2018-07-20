package com.cleveroad.droidwordart.screens.main.pick_image

import android.support.design.widget.BottomSheetBehavior
import android.view.View

internal abstract class BottomSheetCallback : BottomSheetBehavior.BottomSheetCallback() {
    override fun onSlide(bottomSheet: View, slideOffset: Float) {
        //do nothing
    }
}