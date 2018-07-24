package com.cleveroad.droidart

import android.os.Parcel
import android.os.Parcelable

/**
 * Additional class with settings for translation
 */
internal class TranslationSettings : Settings {

    companion object {

        private const val DEFAULT_TRANSLATION = 0F

        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<TranslationSettings> = object : Parcelable.Creator<TranslationSettings> {
            override fun createFromParcel(source: Parcel): TranslationSettings = TranslationSettings(source)
            override fun newArray(size: Int): Array<TranslationSettings?> = arrayOfNulls(size)
        }
    }

    private var storedTranslationX = DEFAULT_TRANSLATION

    private var storedTranslationY = DEFAULT_TRANSLATION

    /**
     * Get translation for x coordinate
     */
    internal val translationX: Float
        get() = storedTranslationX + xEnd - xStart

    /**
     * Get translation for y coordinate
     */
    internal val translationY: Float
        get() = storedTranslationY + yEnd - yStart

    constructor() : super()

    constructor(source: Parcel?) : super(source) {
        source?.apply {
            storedTranslationX = readFloat()
            storedTranslationY = readFloat()
        }
    }

    /**
     * Store current values of x and y translations
     */
    internal fun storeTranslations() {
        storedTranslationX = translationX
        storedTranslationY = translationY
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        super.writeToParcel(dest, flags)
        dest?.apply {
            writeFloat(storedTranslationX)
            writeFloat(storedTranslationY)
        }
    }
}