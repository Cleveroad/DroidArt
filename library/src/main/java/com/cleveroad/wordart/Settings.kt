package com.cleveroad.wordart

import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable

/**
 * Default class for settings encapsulation
 */
internal open class Settings() : Parcelable {

    companion object {

        private const val DEFAULT_X = 0F
        private const val DEFAULT_Y = 0F

        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<Settings> = object : Parcelable.Creator<Settings> {
            override fun createFromParcel(source: Parcel): Settings = Settings(source)
            override fun newArray(size: Int): Array<Settings?> = arrayOfNulls(size)
        }
    }

    /**
     * Point of start touch (previous)
     */
    protected var startPoint = PointF()

    /**
     * X coordinate of start touch (previous)
     */
    protected val xStart: Float
        get() = startPoint.x

    /**
     * Y coordinate of start touch (previous)
     */
    protected val yStart: Float
        get() = startPoint.y

    /**
     * Point of end touch (current)
     */
    protected var endPoint = PointF()

    /**
     * X coordinate of end touch (current)
     */
    protected val xEnd: Float
        get() = endPoint.x

    /**
     * Y coordinate of end touch (current)
     */
    protected val yEnd: Float
        get() = endPoint.y

    protected constructor(source: Parcel?) : this() {
        source?.apply {
            startPoint = readParcelable(PointF::class.java.classLoader)
            endPoint = readParcelable(PointF::class.java.classLoader)
        }
    }

    /**
     * Set start coordinates for canvas drawing
     *
     * @param x Value for x coordinate
     * @param y Value for y coordinate
     */
    internal fun setStart(x: Float, y: Float) = startPoint.set(x, y)

    /**
     * Set end coordinates for canvas drawing
     *
     * @param x Value for x coordinate
     * @param y Value for y coordinate
     */
    internal fun setEnd(x: Float, y: Float) = endPoint.set(x, y)

    /**
     * Set start and end coordinates to default
     */
    internal open fun reset() {
        setStart(DEFAULT_X, DEFAULT_Y)
        setEnd(DEFAULT_X, DEFAULT_Y)
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeParcelable(startPoint, flags)
            writeParcelable(endPoint, flags)
        }
    }
}