package com.cleveroad.wordart

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.ColorRes

/**
 * Additional class with settings for text displaying
 */
internal class TextSettings() : Parcelable {

    companion object {

        private const val DEFAULT_TEXT_MEASUREMENT = 0F
        private const val DEFAULT_TEXT_SIZE = 0F
        private const val DEFAULT_TEXT_COLOR = 0
        private const val DEFAULT_TEXT = ""
        private const val HALF_MEASUREMENT_DELIMITER = 2

        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<TextSettings> = object : Parcelable.Creator<TextSettings> {
            override fun createFromParcel(source: Parcel): TextSettings = TextSettings(source)
            override fun newArray(size: Int): Array<TextSettings?> = arrayOfNulls(size)
        }
    }

    /**
     * value of measured text value for getting left and right borders
     */
    internal var textMeasurement = DEFAULT_TEXT_MEASUREMENT

    /**
     * Text size
     */
    internal var textSize = DEFAULT_TEXT_SIZE

    /**
     * Text for displaying
     */
    internal var text = DEFAULT_TEXT

    /**
     * Text color
     */
    @ColorRes
    internal var textColor = DEFAULT_TEXT_COLOR

    /**
     * Id to font
     */
    internal var fontId: Int = Typeface.DEFAULT.style

    constructor(source: Parcel?) : this() {
        source?.apply {
            textMeasurement = readFloat()
            textSize = readFloat()
            text = readString()
            textColor = readInt()
            fontId = readInt()
        }
    }

    /**
     * Measure text for getting its left and right border
     */
    internal fun measureText(paint: Paint) {
        textMeasurement = paint.measureText(text) / HALF_MEASUREMENT_DELIMITER
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeFloat(textMeasurement)
            writeFloat(textSize)
            writeString(text)
            writeInt(textColor)
            writeInt(fontId)
        }
    }
}