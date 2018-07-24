package com.cleveroad.droidart.model

import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable

class DeviationPoint() : Parcelable {

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<DeviationPoint> = object : Parcelable.Creator<DeviationPoint> {
            override fun createFromParcel(source: Parcel): DeviationPoint = DeviationPoint(source)
            override fun newArray(size: Int): Array<DeviationPoint?> = arrayOfNulls(size)
        }
    }

    /**
     * This point is used to draw a Bezier curve
     */
    var drawPoint = PointF()

    /**
     * This point is used to calculate the movement of [touchPoint]
     */
    var touchPoint = PointF()
    var movePoint = PointF()
    var currentPoint = PointF()
    var ratioPoint = PointF()

    var isChangePoint = false
    var isMoveInPointPosition = false

    constructor(source: Parcel?) : this() {
        source?.apply {
            drawPoint = readParcelable(PointF::class.java.classLoader)
            currentPoint = readParcelable(PointF::class.java.classLoader)
            touchPoint = readParcelable(PointF::class.java.classLoader)
            movePoint = readParcelable(PointF::class.java.classLoader)
            ratioPoint = readParcelable(PointF::class.java.classLoader)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeParcelable(drawPoint, flags)
            writeParcelable(currentPoint, flags)
            writeParcelable(touchPoint, flags)
            writeParcelable(movePoint, flags)
            writeParcelable(ratioPoint, flags)
        }
    }
}