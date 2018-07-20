package com.cleveroad.wordart

import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.ColorRes

/**
 * Settings of the text shadow
 */
internal class ShadowSettings() : Parcelable {

    companion object {

        internal const val NO_SHADOW = -1
        private const val DEFAULT_BLUR_RADIUS = 1F
        private const val FULL_PERCENT = 100
        private const val DEFAULT_ELEVATION_PERCENT = 0

        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<ShadowSettings> = object : Parcelable.Creator<ShadowSettings> {
            override fun createFromParcel(source: Parcel): ShadowSettings = ShadowSettings(source)
            override fun newArray(size: Int): Array<ShadowSettings?> = arrayOfNulls(size)
        }
    }

    /**
     * Elevation in percent
     *
     * Shadow will be shown only if [shadowColor] will be set
     */
    internal var elevationPercent = DEFAULT_ELEVATION_PERCENT

    /**
     * Color of the shadow of the text
     */
    @ColorRes
    internal var shadowColor = NO_SHADOW

    /**
     * Radius for shadow blur
     */
    internal var shadowBlurRadius = DEFAULT_BLUR_RADIUS

    /**
     * Returns true if view needs to draw shadow below text
     */
    internal val needToDrawShadow: Boolean
        get() = shadowColor != NO_SHADOW && elevationPercent > DEFAULT_ELEVATION_PERCENT

    /**
     * Calculate offset for shadow based on textSize
     */
    internal fun calculateShadowOffset(textSize: Float)
            = textSize * elevationPercent / FULL_PERCENT


    constructor(source: Parcel?) : this() {
        source?.apply {
            elevationPercent = readInt()
            shadowColor = readInt()
            shadowBlurRadius = readFloat()
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeInt(elevationPercent)
            writeInt(shadowColor)
            writeFloat(shadowBlurRadius)
        }
    }
}