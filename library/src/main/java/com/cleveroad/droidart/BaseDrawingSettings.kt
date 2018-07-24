package com.cleveroad.droidart

import android.graphics.PointF
import android.os.Parcel

/**
 * Base class for settings, witch must contain the information about translated state
 */
internal abstract class BaseDrawingSettings : Settings {
    companion object {

        private const val DEFAULT_TRANSLATION = 0F
        private const val TRUE_VALUE = 1
        private const val FALSE_VALUE = 0
    }

    /**
     * Flag representing if Settings was initiated
     */
    internal var initialized = false

    /**
     * Center point
     */
    private var centerPoint = PointF()

    /**
     * X coordinate of center point
     */
    internal val centerX: Float
        get() = centerPoint.x

    /**
     * Y coordinate of center point
     */
    internal val centerY: Float
        get() = centerPoint.y

    private val tempTranslatedCenterPoint = PointF()

    /**
     * Center point after translation
     */
    internal val translatedCenterPoint: PointF
        get() {
            tempTranslatedCenterPoint.set(getTranslatedCenterX(), getTranslatedCenterY())
            return tempTranslatedCenterPoint
        }

    /**
     * Value of stored transition for X coordinate
     */
    private var transitionX = DEFAULT_TRANSLATION

    /**
     * Value of stored transition for Y coordinate
     */
    private var transitionY = DEFAULT_TRANSLATION

    /**
     * Value representing if settings is in multi touch mode
     */
    internal var inMultiTouch = false

    constructor() : super()

    protected constructor(source: Parcel?) : super(source) {
        source?.apply {
            centerPoint = readParcelable(PointF::class.java.classLoader)
            initialized = readInt() == TRUE_VALUE
            transitionX = readFloat()
            transitionY = readFloat()
        }
    }

    /**
     * Return translated X coordinate of center point
     *
     * @return Translated X coordinate of center point
     */
    internal fun getTranslatedCenterX() = centerPoint.x

    /**
     * Return translated Y coordinate of center point
     *
     * @return Translated Y coordinate of center point
     */
    internal fun getTranslatedCenterY() = centerPoint.y


    /**
     * Set new transition values for X and Y coordinates
     *
     * @param xTransition Value of transition for X coordinate
     * @param yTransition Value of transition for Y coordinate
     */
    internal fun setTranslation(xTransition: Float, yTransition: Float) {
        transitionX = xTransition
        transitionY = yTransition
    }

    /**
     * Set new coordinates for center point
     *
     * @param x X coordinate of center point
     * @param y Y coordinate of center point
     */
    internal fun setCenterPoint(x: Float, y: Float) = centerPoint.set(x, y)

    /**
     * Set values of End coordinates to Start coordinates
     */
    internal fun setEndToStart() {
        store()
        with(endPoint) {
            setStart(x, y)
        }
    }

    /**
     * Calculate pseudo scalar for vectors in Start and and End points started from center point
     */
    private fun calculateCurrentPseudoScalar() =
            calculatePseudoScalar(startPoint, endPoint, translatedCenterPoint)

    /**
     * Checks if pseudo scalar for current fields has positive value
     *
     * @return true if pseudo scalar is positive and false in another case
     */
    internal fun isPseudoScalarPositive() = (calculateCurrentPseudoScalar() >= 0)

    /**
     * Method for storing operation
     */
    abstract fun store()

    /**
     * Method for module initialization
     *
     * @param centerX X coordinate of center point
     * @param centerY Y coordinate of center point
     * @param x X coordinate for starting value of start and end coordinates
     * @param y Y coordinate for starting value of start and end coordinates
     */
    internal open fun init(centerX: Float, centerY: Float, x: Float, y: Float) {
        setCenterPoint(centerX, centerY)
        setStart(x, y)
        setEnd(x, y)
        initialized = true
    }

    /**
     * Set coordinates of current finger touches for multi touch mode
     *
     * @param x1 X coordinate for first finger touch
     * @param y1 Y coordinate for first finger touch
     * @param x2 X coordinate for second finger touch
     * @param y2 Y coordinate for second finger touch
     */
    internal abstract fun setCurrentPoints(x1: Float, y1: Float, x2: Float, y2: Float)

    /**
     * Set coordinates of previous finger touches for multi touch mode
     *
     * @param x1 X coordinate for first finger touch
     * @param y1 Y coordinate for first finger touch
     * @param x2 X coordinate for second finger touch
     * @param y2 Y coordinate for second finger touch
     */
    internal abstract fun setPreviousPoints(x1: Float, y1: Float, x2: Float, y2: Float)

    /**
     * Initialize multi touch mode with finger touches start coordinates coordinates
     *
     * @param x1 X coordinate for first finger touch
     * @param y1 Y coordinate for first finger touch
     * @param x2 X coordinate for second finger touch
     * @param y2 Y coordinate for second finger touch
     */
    internal abstract fun initMultiTouch(x1: Float, y1: Float, x2: Float, y2: Float)

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        super.writeToParcel(dest, flags)
        dest?.apply {
            writeParcelable(centerPoint, flags)
            writeInt(if (initialized) TRUE_VALUE else FALSE_VALUE)
            writeFloat(transitionX)
            writeFloat(transitionY)
        }
    }
}