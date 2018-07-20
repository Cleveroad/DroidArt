package com.cleveroad.wordart

import android.os.Parcel
import android.os.Parcelable

/**
 * Additional class with settings for button
 */
internal class ButtonSettings() : Parcelable {

    companion object {

        private const val DEFAULT_BUTTON_COLOR = 0

        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<ButtonSettings> = object : Parcelable.Creator<ButtonSettings> {
            override fun createFromParcel(source: Parcel): ButtonSettings = ButtonSettings(source)
            override fun newArray(size: Int): Array<ButtonSettings?> = arrayOfNulls(size)
        }
    }

    /**
     * Color of button
     */
    internal var buttonColor = DEFAULT_BUTTON_COLOR

    constructor(source: Parcel?) : this() {
        source?.apply { buttonColor = readInt() }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply { writeInt(buttonColor) }
    }
}
