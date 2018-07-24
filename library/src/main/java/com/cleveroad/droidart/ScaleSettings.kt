package com.cleveroad.droidart

import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable

/**
 * Additional class for scale settings
 */
internal class ScaleSettings : BaseDrawingSettings {

    companion object {

        private const val MIN_SCALE_FACTOR = 1F
        private const val DEFAULT_DIFF = 0F
        private const val CENTER_SEGMENT_MULTIPLIER = 0.5F
        private const val DOUBLE_VALUE = 2

        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<ScaleSettings> = object : Parcelable.Creator<ScaleSettings> {
            override fun createFromParcel(source: Parcel): ScaleSettings = ScaleSettings(source)
            override fun newArray(size: Int): Array<ScaleSettings?> = arrayOfNulls(size)
        }
    }

    private var startDiff = DEFAULT_DIFF

    private var endDiff = DEFAULT_DIFF

    internal var diff = DEFAULT_DIFF
        private set

    private var currentDiff = DEFAULT_DIFF

    internal var scaleFactor = MIN_SCALE_FACTOR
        private set

    private var multiTouchModule = MultiTouchScaleModule()

    constructor() : super()

    constructor(source: Parcel?) : super(source) {
        source?.apply {
            startDiff = readFloat()
            endDiff = readFloat()
            diff = readFloat()
            currentDiff = readFloat()
            scaleFactor = readFloat()
        }
    }

    override fun store() {
        // do nothing
    }

    /**
     * Scale rectangle
     *
     * @param rectangle Rectangle for scaling
     *
     * @return scaled [RotatedRectangle]
     */
    internal fun scaleRectangle(rectangle: RotatedRectangle) = rectangle.apply {
        setLefTop(
                leftTopPoint.x - diff,
                leftTopPoint.y - diff)
        setRightTop(
                rightTopPoint.x + diff,
                rightTopPoint.y - diff)
        setRightBottom(
                rightBottomPoint.x + diff,
                rightBottomPoint.y + diff)
        setLeftBottom(
                leftBottomPoint.x - diff,
                leftBottomPoint.y + diff)
    }

    /**
     * Calculate difference, witch we must add to the coordinates to scale something
     *
     * @param maxDiff Max bound for scaling
     */
    internal fun computeScale(maxDiff: Float) {
        currentDiff = if (inMultiTouch) multiTouchModule.calculateDiff() else calculateDiff()
        diff = (currentDiff + diff)

        diff = Math.max(diff, DEFAULT_DIFF)
        diff = Math.min(diff, maxDiff)
    }

    /**
     * Calculate difference for adding to the coordinates
     * to scale something in not multi touch mode
     */
    private fun calculateDiff(): Float {
        // distance from the point of touch to the center of the rectangle
        startDiff = Math.hypot((xStart - getTranslatedCenterX()).toDouble(),
                (yStart - getTranslatedCenterY()).toDouble()).toFloat()
        // distance from the point of moving to the center of the rectangle
        endDiff = Math.hypot((xEnd - getTranslatedCenterX()).toDouble(),
                (yEnd - getTranslatedCenterY()).toDouble()).toFloat()
        return (endDiff - startDiff) * CENTER_SEGMENT_MULTIPLIER
    }

    override fun setCurrentPoints(x1: Float, y1: Float, x2: Float, y2: Float) = with(multiTouchModule) {
        setCurrentToPrevious()
        setCurrentPoint(x1, y1, x2, y2)
    }

    override fun setPreviousPoints(x1: Float, y1: Float, x2: Float, y2: Float) =
            multiTouchModule.setPreviousPoint(x1, y1, x2, y2)

    override fun initMultiTouch(x1: Float, y1: Float, x2: Float, y2: Float) =
            multiTouchModule.init(x1, y1, x2, y2)

    /**
     * Calculate [scaleFactor] based on text settings
     *
     * @param textSettings Instance of [TextSettings]
     * @param rectangle - [RectF] for getting real length of selector
     */
    /* TODO: add commentaries about logic and move calculation
     * (2 * textSettings.textMeasurement + 2 * textSettings.textSize)
     * to more appropriate place for it. Cause this calculation depends
     * to EditorView calculations and it's a bad idea to make calculations based on same
     * geometric approach in different places */
    internal fun computeScaleFactor(textSettings: TextSettings, rectangle: RotatedRectangle) = with(rectangle) {
        // factor is calculated from the ratio of the sum
        // of the width of the rectangle with twice the current difference to the initial width of the rectangle
        val factor = (rightTopPoint.x - leftTopPoint.x + currentDiff * DOUBLE_VALUE) /
                ((textSettings.textMeasurement + textSettings.textSize) * DOUBLE_VALUE)
        scaleFactor = if (factor < MIN_SCALE_FACTOR) MIN_SCALE_FACTOR else factor
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        super.writeToParcel(dest, flags)
        dest?.apply {
            writeFloat(startDiff)
            writeFloat(endDiff)
            writeFloat(diff)
            writeFloat(currentDiff)
            writeFloat(scaleFactor)
        }
    }
}