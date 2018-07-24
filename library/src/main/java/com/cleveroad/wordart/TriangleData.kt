package com.cleveroad.wordart

/**
 * Interface for encapsulation data about triangle created by center point,
 * default point (start) and point of user tap (end).
 */
internal interface TriangleData {

    /**
     * @return X coordinate for center point
     */
    fun getCenterXForProcess(): Float

    /**
     * @return Y coordinate for center point
     */
    fun getCenterYForProcess(): Float

    /**
     * @return X coordinate for start point
     */
    fun getStartXForProcess(): Float

    /**
     * @return Y coordinate for start point
     */
    fun getStartYForProcess(): Float

    /**
     * @return X coordinate for end point
     */
    fun getEndXForProcess(): Float

    /**
     * @return Y coordinate for end point
     */
    fun getEndYForProcess(): Float
}