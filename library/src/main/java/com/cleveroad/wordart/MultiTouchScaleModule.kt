package com.cleveroad.wordart

import android.graphics.PointF


/**
 * Module for calculation multi touch scaling
 */
internal class MultiTouchScaleModule : MultiTouchModule() {

    companion object {
        /**
         * Calculate distance between two points
         *
         * @return [Float] value of distance
         */
        @JvmStatic
        fun calculateDistance(point1: PointF, point2: PointF) =
                Math.hypot((point2.x - point1.x).toDouble(), (point2.y - point1.y).toDouble()).toFloat()

    }

    /**
     * Calculate the difference for scaling
     *
     * @return [Float] value of difference between distances of current and previous points
     */
    internal fun calculateDiff() =
            (calculateDistance(firstPoint, secondPoint) - calculateDistance(firstPointPrevious, secondPointPrevious))
}
