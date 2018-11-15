package com.cleveroad.droidart

import android.os.Parcel
import android.os.Parcelable

/**
 * Additional class with rotate settings
 */
internal class RotateSettings : BaseDrawingSettings {

    companion object {

        private const val FULL_CIRCLE_DEGREES = 360
        private const val DEFAULT_ROTATION = 0F
        private const val CLOCKWISE_ROTATION = 1.0
        private const val COUNTERCLOCKWISE_ROTATION = -1.0

        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<RotateSettings> = object : Parcelable.Creator<RotateSettings> {
            override fun createFromParcel(source: Parcel): RotateSettings = RotateSettings(source)
            override fun newArray(size: Int): Array<RotateSettings?> = arrayOfNulls(size)
        }
    }

    private var multiTouchModule = MultiTouchRotateModule()

    private var storedRotation = DEFAULT_ROTATION

    private val triangleProcessor = TriangleProcessor(object : TriangleData {

        override fun getCenterXForProcess() = translatedCenterPoint.x

        override fun getCenterYForProcess() = translatedCenterPoint.y

        override fun getStartXForProcess() = startPoint.x

        override fun getStartYForProcess() = startPoint.y

        override fun getEndXForProcess() = endPoint.x

        override fun getEndYForProcess() = endPoint.y
    })

    constructor() : super()

    constructor(source: Parcel?) : super(source) {
        source?.apply {
            storedRotation = readFloat()
        }
    }

    /**
     * Sets the default value for rotation
     */
    internal fun resetRotation(){
        storedRotation = DEFAULT_ROTATION
    }

    /**
     * Return value for rotation
     *
     * @return [Float] value of rotation
     */
    internal fun rotation(): Float {
        return if (initialized) {
            return if (inMultiTouch) {
                calculateMultiTouchRotation()
            } else {
                calculateRotation()
            }
        } else {
            DEFAULT_ROTATION
        }
    }

    /**
     * Calculate rotation in multi touch mode
     *
     * @return [Float] value of rotation
     */
    private fun calculateMultiTouchRotation(): Float {
        storedRotation = (storedRotation + multiTouchModule.calculateRotation()) % FULL_CIRCLE_DEGREES
        return storedRotation
    }

    /**
     * Calculate rotation in normal mode
     *
     * @return [Float] value of rotation
     */
    private fun calculateRotation() =
            ((storedRotation + getMultiplier() * Math.toDegrees(triangleProcessor.angle)) % FULL_CIRCLE_DEGREES).toFloat()

    override fun store() {
        storedRotation = rotation()
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
     * Calculate multiplier based on direction of rotation
     *
     * @return [CLOCKWISE_ROTATION] or [COUNTERCLOCKWISE_ROTATION] based on rotation direction
     */
    private fun getMultiplier() = if (isPseudoScalarPositive()) CLOCKWISE_ROTATION else COUNTERCLOCKWISE_ROTATION

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        super.writeToParcel(dest, flags)
        dest?.apply {
            writeFloat(storedRotation)
        }
    }
}
