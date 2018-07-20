package com.cleveroad.wordart

import android.graphics.PointF

/**
 * Calculate pseudo scalar for two vectors started in point with coordinates x = 0 and y = 0
 *
 * @param point1 Point with coordinates of first vector
 * @param point2 Point with coordinates of second vector
 *
 * @return calculated pseudo scalar
 */
internal fun calculatePseudoScalar(point1: PointF, point2: PointF) =
        calculatePseudoScalar(point1.x, point1.y, point2.x, point2.y)

/**
 * Calculate pseudo scalar for two vectors started in predefined point
 *
 * @param point1 Point with coordinates of first vector
 * @param point2 Point with coordinates of second vector
 * @param pointCenter Point with coordinates of vectors start
 *
 * @return calculated pseudo scalar
 */
internal fun calculatePseudoScalar(point1: PointF, point2: PointF, pointCenter: PointF) =
        calculatePseudoScalar(point1, point2, pointCenter.x, pointCenter.y)

/**
 * Calculate pseudo scalar for two vectors started in predefined point
 *
 * @param point1 Point with coordinates of first vector
 * @param point2 Point with coordinates of second vector
 * @param xC X coordinate of start point
 * @param yC Y coordinate of start point
 *
 * @return calculated pseudo scalar
 */
internal fun calculatePseudoScalar(point1: PointF, point2: PointF, xC: Float, yC: Float) =
        calculatePseudoScalar(point1.x, point1.y, point2.x, point2.y, xC, yC)

/**
 * Calculate pseudo scalar for two vectors started in predefined point
 *
 * @param x1 X coordinate of first vector
 * @param y1 Y coordinate of first vector
 * @param x2 X coordinate of second vector
 * @param y2 Y coordinate of second vector
 * @param xC X coordinate of start point
 * @param yC Y coordinate of start point
 *
 * @return calculated pseudo scalar
 */
internal fun calculatePseudoScalar(x1: Float, y1: Float, x2: Float, y2: Float, xC: Float, yC: Float) =
        calculatePseudoScalar(x1 - xC, y1 - yC, x2 - xC, y2 - yC)

/**
 * Calculate pseudo scalar for two vectors started in point with coordinates x = 0 and y = 0
 *
 * @param x1 X coordinate of first vector
 * @param y1 Y coordinate of first vector
 * @param x2 X coordinate of second vector
 * @param y2 Y coordinate of second vector
 *
 * @return calculated pseudo scalar
 */
internal fun calculatePseudoScalar(x1: Float, y1: Float, x2: Float, y2: Float) = (x1 * y2 - x2 * y1)
