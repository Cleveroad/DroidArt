package com.cleveroad.droidart

import android.graphics.Path
import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable

/**
 * Class for encapsulation data about rectangle rotated in space
 */
internal class RotatedRectangle() : Parcelable {

    companion object {
        private const val CENTER_DIAGONAL_MULTIPLIER = 0.5F
        private const val MIN_POSITIVE_VALUE = 0F

        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<RotatedRectangle> = object : Parcelable.Creator<RotatedRectangle> {
            override fun createFromParcel(source: Parcel): RotatedRectangle = RotatedRectangle(source)
            override fun newArray(size: Int): Array<RotatedRectangle?> = arrayOfNulls(size)
        }
    }

    /**
     * Left Top [PointF] of rectangle
     */
    internal var leftTopPoint = PointF()
        private set

    /**
     * Left Bottom [PointF] of rectangle
     */
    internal var leftBottomPoint = PointF()
        private set

    /**
     * Right Top [PointF] of rectangle
     */
    internal var rightTopPoint = PointF()
        private set

    /**
     * Right Bottom [PointF] of rectangle
     */
    internal var rightBottomPoint = PointF()
        private set

    private val path = Path()

    private val pseudoScalars = mutableListOf<Float>()


    constructor(source: Parcel?) : this() {
        source?.apply {
            leftTopPoint = readParcelable(PointF::class.java.classLoader)
            leftBottomPoint = readParcelable(PointF::class.java.classLoader)
            rightTopPoint = readParcelable(PointF::class.java.classLoader)
            rightBottomPoint = readParcelable(PointF::class.java.classLoader)
        }
    }

    /**
     * Set [leftTopPoint] by x and y coordinates
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    internal fun setLefTop(x: Float, y: Float) = leftTopPoint.set(x, y)

    /**
     * Set [leftBottomPoint] by x and y coordinates
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    internal fun setLeftBottom(x: Float, y: Float) = leftBottomPoint.set(x, y)

    /**
     * Set [rightTopPoint] by x and y coordinates
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    internal fun setRightTop(x: Float, y: Float) = rightTopPoint.set(x, y)

    /**
     * Set [rightBottomPoint] by x and y coordinates
     *
     * @param x X coordinate
     * @param y Y coordinate
     */
    internal fun setRightBottom(x: Float, y: Float) = rightBottomPoint.set(x, y)


    operator fun set(left: Float, top: Float, right: Float, bottom: Float) {
        this.setLefTop(left, top)
        this.setRightTop(right, top)
        this.setRightBottom(right, bottom)
        this.setLeftBottom(left, bottom)
    }

    /**
     * @return the horizontal center of the rectangle. This does not check for
     * a valid rectangle (i.e. left <= right)
     */
    internal fun centerX() = (leftTopPoint.x + rightBottomPoint.x) * CENTER_DIAGONAL_MULTIPLIER

    /**
     * @return the vertical center of the rectangle. This does not check for
     * a valid rectangle (i.e. top <= bottom)
     */
    internal fun centerY() = (leftTopPoint.y + rightBottomPoint.y) * CENTER_DIAGONAL_MULTIPLIER

    /**
     * Returns instance of [Path] created by coordinates of this rectangle
     *
     * @return Instance of [Path]
     */
    internal fun createPath() = path.apply {
        reset()
        moveTo(leftTopPoint.x, leftTopPoint.y)
        lineTo(leftBottomPoint.x, leftBottomPoint.y)
        lineTo(rightBottomPoint.x, rightBottomPoint.y)
        lineTo(rightTopPoint.x, rightTopPoint.y)
        close()
    }

    /**
     * Check if point is inside this rectangle
     *
     * @param x X coordinate of point to check
     * @param y Y coordinate of point to check
     *
     * @return true if point is inside of this rectangle and false in otherwise
     */
    internal fun checkIfContainsPoint(x: Float, y: Float): Boolean {
        pseudoScalars.clear()
        // calculate pseudo scalar for every edge of rectangle and checking point in counterclockwise order
        with(pseudoScalars) {
            add(calculatePseudoScalar(x, y, leftTopPoint.x, leftTopPoint.y, rightTopPoint.x, rightTopPoint.y))
            add(calculatePseudoScalar(x, y, rightTopPoint.x, rightTopPoint.y, rightBottomPoint.x, rightBottomPoint.y))
            add(calculatePseudoScalar(x, y, rightBottomPoint.x, rightBottomPoint.y, leftBottomPoint.x, leftBottomPoint.y))
            add(calculatePseudoScalar(x, y, leftBottomPoint.x, leftBottomPoint.y, leftTopPoint.x, leftTopPoint.y))
        }

        // for convex figure (as rectangle) and point inside it all pseudo scalars calculated in counterclockwise order
        // will be positive. If at least one pseudo scalar will be negative - point is outside figure
        pseudoScalars.forEach {
            if (it < MIN_POSITIVE_VALUE) return false
        }
        return true
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeParcelable(leftTopPoint, flags)
            writeParcelable(leftBottomPoint, flags)
            writeParcelable(rightTopPoint, flags)
            writeParcelable(rightBottomPoint, flags)
        }
    }
}