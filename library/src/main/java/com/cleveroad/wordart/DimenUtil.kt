package com.cleveroad.wordart

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

/**
 * Function for converting value of dimens in Sp to Float value (in pixels for current screen)
 */
internal fun convertSpToFloat(context: Context?, textSize: Float): Float {
    val resources = context?.resources ?: Resources.getSystem()
    return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            textSize,
            resources.displayMetrics)
}





