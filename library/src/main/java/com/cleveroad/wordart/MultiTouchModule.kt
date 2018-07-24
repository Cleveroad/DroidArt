package com.cleveroad.wordart

import android.graphics.PointF

/**
 * Base class for encapsulation processing of multi touch interactions
 */
internal open class MultiTouchModule {

    /**
     * Point for current first touched finger
     */
    protected val firstPoint = PointF()

    /**
     * Point for current second touched finger
     */
    protected val secondPoint = PointF()

    /**
     * Point for previous first touched finger
     */
    protected val firstPointPrevious = PointF()

    /**
     * Point for previous second touched finger
     */
    protected val secondPointPrevious = PointF()

    /**
     * Set current points with arguments
     *
     * @param x1 X coordinate of first finger touch
     * @param y1 Y coordinate of first finger touch
     * @param x2 X coordinate of second finger touch
     * @param y2 Y coordinate of second finger touch
     */
    internal fun setCurrentPoint(x1: Float, y1: Float, x2: Float, y2: Float) {
        firstPoint.set(x1, y1)
        secondPoint.set(x2, y2)
    }

    /**
     * Set previous points with arguments
     *
     * @param x1 X coordinate of first finger touch
     * @param y1 Y coordinate of first finger touch
     * @param x2 X coordinate of second finger touch
     * @param y2 Y coordinate of second finger touch
     */
    internal fun setPreviousPoint(x1: Float, y1: Float, x2: Float, y2: Float) {
        firstPointPrevious.set(x1, y1)
        secondPointPrevious.set(x2, y2)
    }

    /**
     * Gets coordinates from current points and set them to previous
     */
    internal fun setCurrentToPrevious() = setPreviousPoint(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y)

    /**
     * Method for module initialization
     *
     * @param x1 X coordinate of first finger touch
     * @param y1 Y coordinate of first finger touch
     * @param x2 X coordinate of second finger touch
     * @param y2 Y coordinate of second finger touch
     */
    internal fun init(x1: Float, y1: Float, x2: Float, y2: Float) {
        setCurrentPoint(x1, y1, x2, y2)
        setPreviousPoint(x1, y1, x2, y2)
    }
}