package com.cleveroad.droidart

import android.graphics.PointF

/**
 * Module for calculation multi touch rotation
 */
internal class MultiTouchRotateModule : MultiTouchModule() {

    companion object {

        private const val DEFAULT_ANGLE = 0F
        private const val FULL_CIRCLE_DEGREES = 360F
        private const val HALF_CIRCLE_DEGREES = 180F
        private const val ANGLE_MULTIPLIER = 4F
    }

    private var angle = DEFAULT_ANGLE

    /**
     * Calculate the angle of rotation between lines created
     * by current two finger's touches and previous
     *
     * @return [Float] value of rotation angle
     */
    internal fun calculateRotation(): Float {
        angle = ((Math.toDegrees((calculateAngleInRadians(firstPointPrevious, secondPointPrevious)
                - (calculateAngleInRadians(firstPoint, secondPoint))))) % FULL_CIRCLE_DEGREES).toFloat()
        if (angle < -HALF_CIRCLE_DEGREES) {
            angle += FULL_CIRCLE_DEGREES
        } else if (angle > HALF_CIRCLE_DEGREES) {
            angle -= FULL_CIRCLE_DEGREES
        }
        return angle / ANGLE_MULTIPLIER
    }

    /**
     * Calculate the angle of the line created by two points
     *
     * @return [Double] value of angle
     */
    private fun calculateAngleInRadians(point1: PointF, point2: PointF) =
            Math.atan2((point2.x - point1.x).toDouble(), (point2.y - point1.y).toDouble())
}
