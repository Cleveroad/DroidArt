package com.cleveroad.wordart

import android.os.Parcel
import android.os.Parcelable

/**
 * Additional class with settings for selector
 */
internal class SelectorSettings() : Parcelable {

    companion object {

        private const val DEFAULT_SELECTOR_COLOR = 0
        private const val DEFAULT_RADIUS = 0F
        private const val DEFAULT_HALF_DIAGONAL = 0F
        private const val TRUE_VALUE = 1
        private const val FALSE_VALUE = 0
        private const val HALF_DIAGONAL_DELIMITER = 2
        private const val RADIUS_DELIMITER = 1.5F

        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<SelectorSettings> = object : Parcelable.Creator<SelectorSettings> {
            override fun createFromParcel(source: Parcel): SelectorSettings = SelectorSettings(source)
            override fun newArray(size: Int): Array<SelectorSettings?> = arrayOfNulls(size)
        }
    }

    /**
     * Color of selector
     */
    internal var selectorColor = DEFAULT_SELECTOR_COLOR

    /**
     * Value representing necessity for selector displaying
     */
    internal var needToShow = true

    /**
     * Radius for button circle
     */
    internal var radius = DEFAULT_RADIUS
        private set

    /**
     * Half diagonal for Rectangle for bitmap
     */
    internal var halfDiagonal = DEFAULT_HALF_DIAGONAL
        private set

    constructor(source: Parcel?) : this() {
        source?.apply {
            selectorColor = readInt()
            needToShow = readInt() == TRUE_VALUE
            radius = readFloat()
            halfDiagonal = readFloat()
        }
    }

    /**
     * Calculate values for [halfDiagonal] and [radius] by textSize
     *
     * @param textSize Size of text to measure
     */
    internal fun measure(textSize: Float) {
        radius = textSize / RADIUS_DELIMITER
        halfDiagonal = textSize / HALF_DIAGONAL_DELIMITER
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeInt(selectorColor)
            writeInt(if (needToShow) TRUE_VALUE else FALSE_VALUE)
            writeFloat(radius)
            writeFloat(halfDiagonal)
        }
    }

}