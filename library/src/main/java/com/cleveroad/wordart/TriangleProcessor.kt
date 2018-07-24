package com.cleveroad.wordart

/**
 * Class for encapsulation operations with triangle
 */
internal class TriangleProcessor(private val triangleData: TriangleData) {
    companion object {

        private const val DEFAULT_TEMP_COS = 0.0
        private const val MIN_TEMP_COS = -1.0
        private const val MAX_TEMP_COS = 1.0
        private const val SECOND_DEGREE = 2.0
        private const val DOUBLE_MULTIPLIER = 2
    }

    private var tempCos = DEFAULT_TEMP_COS

    /**
     * Distance from center X coordinate to start X coordinate
     */
    private val centerToStartX: Double
        get() = (triangleData.getCenterXForProcess() - triangleData.getStartXForProcess()).toDouble()

    /**
     * Distance from center Y coordinate to start Y coordinate
     */
    private val centerToStartY: Double
        get() = (triangleData.getCenterYForProcess() - triangleData.getStartYForProcess()).toDouble()

    /**
     * Distance from center X coordinate to end X coordinate
     */
    private val centerToEndX: Double
        get() = (triangleData.getCenterXForProcess() - triangleData.getEndXForProcess()).toDouble()

    /**
     * Distance from center Y coordinate to end Y coordinate
     */
    private val centerToEndY: Double
        get() = (triangleData.getCenterYForProcess() - triangleData.getEndYForProcess()).toDouble()

    /**
     * Distance from start X coordinate to end X coordinate
     */
    private val startToEndX: Double
        get() = (triangleData.getStartXForProcess() - triangleData.getEndXForProcess()).toDouble()

    /**
     * Distance from start Y coordinate to end Y coordinate
     */
    private val startToEndY: Double
        get() = (triangleData.getStartYForProcess() - triangleData.getEndYForProcess()).toDouble()

    /**
     * Length of triangle edge with apexes in center and start points
     */
    private val ab: Double
        get() = Math.hypot(centerToStartX, centerToStartY)

    /**
     * Length of triangle edge with apexes in center and end points
     */
    private val ac: Double
        get() = Math.hypot(centerToEndX, centerToEndY)

    /**
     * Length of triangle edge with apexes in start and end points
     */
    private val bc: Double
        get() = Math.hypot(startToEndX, startToEndY)

    /**
     * Cosine of angle of center apex
     */
    private val cosA: Double
        get() {
            tempCos = (Math.pow(ab, SECOND_DEGREE) + Math.pow(ac, SECOND_DEGREE) - Math.pow(bc, SECOND_DEGREE)) / (ab * ac * DOUBLE_MULTIPLIER)
            // It's for fix calculation accuracy
            tempCos = Math.max(tempCos, MIN_TEMP_COS)
            tempCos = Math.min(tempCos, MAX_TEMP_COS)

            return tempCos
        }

    /**
     * Angle of center apex in radians
     */
    internal val angle: Double
        get() = Math.acos(cosA)

    /**
     * Angle of center apex in degrees
     */
    internal val angleInDegrees: Double
        get() = Math.toDegrees(angle)
}

