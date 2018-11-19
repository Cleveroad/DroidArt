package com.cleveroad.droidart

import android.graphics.Path
import android.graphics.PointF
import android.os.Parcel
import android.os.Parcelable
import com.cleveroad.droidart.model.DeviationPoint

/**
 * Additional class with change view text settings
 */
internal class ChangeViewTextSettings : Parcelable {

    companion object {

        private const val DEFAULT_PATH_LENGTH = 0F
        private const val DEFAULT_DISTANCE = 0F
        private const val DOUBLE_VALUE = 2
        private const val MIN_POSITIVE = 0F
        private const val DEFAULT_STEP = 0.0025F
        private const val START_POSITION_CURVE = 0F
        private const val END_POSITION_CURVE = 1F
        private const val DEFAULT_NUMBER_VERTEX = 0
        private const val DEFAULT_ARRAY_VALUE = 0F
        private const val INDEX_COOR_X = 0
        private const val INDEX_COOR_Y = 1
        private const val COMPLETE = 1F
        private const val HALF_DELIMITER = 2
        private const val DIVIDE_INTO_QUARTERS = 3
        private const val DIVIDE_INTO_HALF = 1
        private const val DEFAULT_TEXT_MEASURE = 0F
        private const val DEFAULT_COOR_RECTANGLE = 0F
        private const val DEFAULT_DIFFERENCE = 0F

        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<ChangeViewTextSettings> = object : Parcelable.Creator<ChangeViewTextSettings> {
            override fun createFromParcel(source: Parcel): ChangeViewTextSettings = ChangeViewTextSettings(source)
            override fun newArray(size: Int): Array<ChangeViewTextSettings?> = arrayOfNulls(size)
        }
    }

    internal var leftPoint = PointF()
        private set
    internal var rightPoint = PointF()
        private set
    internal var firstPoint = DeviationPoint()
        private set
    internal var secondPoint = DeviationPoint()
        private set
    internal var thirdPoint = DeviationPoint()
        private set
    internal var fourthPoint = DeviationPoint()
        private set

    internal var distanceTop = DEFAULT_DISTANCE
        private set
    internal var distanceBottom = DEFAULT_DISTANCE
        private set
    internal var distanceLeft = DEFAULT_DISTANCE
        private set
    internal var distanceRight = DEFAULT_DISTANCE
        private set

    private var textMeasure = DEFAULT_TEXT_MEASURE
        private set

    private val pseudoScalars = mutableListOf<Float>()
    private var ratioLeftPoint = PointF()
    private var ratioRightPoint = PointF()
    private var pathLength = DEFAULT_PATH_LENGTH
    private var startPath = DEFAULT_PATH_LENGTH
    private var endPath = DEFAULT_PATH_LENGTH
    private var tempPath = DEFAULT_PATH_LENGTH

    var leftPointCurve = PointF()
    var rightPointCurve = PointF()
    private var firstPointCurve = PointF()
    private var secondPointCurve = PointF()
    private var thirdPointCurve = PointF()
    private var fourthPointCurve = PointF()

    /**
     * The path the text should follow for its baseline
     */
    private var path = Path()
    private var minX = Float.POSITIVE_INFINITY
    private var maxX = Float.NEGATIVE_INFINITY
    private var minY = Float.POSITIVE_INFINITY
    private var maxY = Float.NEGATIVE_INFINITY
    private var yTop = DEFAULT_COOR_RECTANGLE
    private var yBottom = DEFAULT_COOR_RECTANGLE
    private var xLeft = DEFAULT_COOR_RECTANGLE
    private var xRight = DEFAULT_COOR_RECTANGLE
    private var previousDiff = DEFAULT_DIFFERENCE
    private var isInitialized = false
    internal var isSetOffsetCenterCanvas = true

    internal fun setLeftPoint(x: Float, y: Float) = leftPointCurve.set(x, y)

    internal fun setRightPoint(x: Float, y: Float) = rightPointCurve.set(x, y)

    internal fun setFirstPoint(x: Float, y: Float) = firstPointCurve.set(x, y)

    internal fun setSecondPoint(x: Float, y: Float) = secondPointCurve.set(x, y)

    internal fun setThirdPoint(x: Float, y: Float) = thirdPointCurve.set(x, y)

    internal fun setFourthPoint(x: Float, y: Float) = fourthPointCurve.set(x, y)

    internal fun initChangeViewTextSettings(rotatedRectangle: RotatedRectangle, textMeasurement: Float) {
        if (!isInitialized) {
            init(rotatedRectangle, textMeasurement)

            yTop = rotatedRectangle.leftTopPoint.y
            yBottom = rotatedRectangle.leftBottomPoint.y
            xLeft = rotatedRectangle.leftBottomPoint.x
            xRight = rotatedRectangle.rightBottomPoint.x

            isInitialized = !isInitialized
        }
    }

    internal fun computePathBezierCurve(): Path {
        val flow: List<Array<Float>>
        val arr = arrayOf(
                arrayOf(leftPointCurve.x, leftPointCurve.y),
                arrayOf(firstPointCurve.x, firstPointCurve.y),
                arrayOf(secondPointCurve.x, secondPointCurve.y),
                arrayOf(thirdPointCurve.x, thirdPointCurve.y),
                arrayOf(fourthPointCurve.x, fourthPointCurve.y),
                arrayOf(rightPointCurve.x, rightPointCurve.y)
        )

        flow = getBezierCurve(listOf(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]))

        val size = 0 until flow.size

        path.reset()
        path.moveTo(leftPointCurve.x, leftPointCurve.y)

        for (index in size) {
            path.lineTo(flow[index][INDEX_COOR_X], flow[index][INDEX_COOR_Y])
        }

        return path
    }

    /**
     * Compute path of the bezier curve
     *
     * @param     diff    The current scaling
     * @param textSize    The current text size
     *
     * @return Path of the bezier curve
     */
    internal fun computeMinMaxBezierCurve(diff: Float, textSize: Float, textMeasurement: Float) {
        val flow: List<Array<Float>>
        val arr = arrayOf(
                arrayOf(leftPoint.x - diff - (textMeasurement - textMeasure), leftPoint.y),
                arrayOf(firstPoint.drawPoint.x, firstPoint.drawPoint.y),
                arrayOf(secondPoint.drawPoint.x, secondPoint.drawPoint.y),
                arrayOf(thirdPoint.drawPoint.x, thirdPoint.drawPoint.y),
                arrayOf(fourthPoint.drawPoint.x, fourthPoint.drawPoint.y),
                arrayOf(rightPoint.x + diff + (textMeasurement - textMeasure), rightPoint.y)
        )

        flow = getBezierCurve(listOf(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]))

        val size = 1 until flow.size

        for (index in size) {
            pathLength += Math.hypot(
                    (flow[index][INDEX_COOR_X] - flow[index - 1][INDEX_COOR_X]).toDouble(),
                    (flow[index][INDEX_COOR_Y] - flow[index - 1][INDEX_COOR_Y]).toDouble()
            ).toFloat()
        }

        val halfPath = pathLength / HALF_DELIMITER

        startPath = halfPath - textMeasurement
        endPath = halfPath + textMeasurement
        tempPath = DEFAULT_PATH_LENGTH

        for (index in size) {
            tempPath += Math.hypot(
                    (flow[index][INDEX_COOR_X] - flow[index - 1][INDEX_COOR_X]).toDouble(),
                    (flow[index][INDEX_COOR_Y] - flow[index - 1][INDEX_COOR_Y]).toDouble()
            ).toFloat()

            if (tempPath in startPath..endPath) {
                computeMinAndMax(flow[index][INDEX_COOR_X], flow[index][INDEX_COOR_Y])
            }
        }

        pathLength = DEFAULT_PATH_LENGTH
        computeScale(textSize, textMeasurement)
    }

    internal fun computeFirstPoint(angleInDegrees: Float, xTouch: Float, yTouch: Float) {
        computeRotatePoint(firstPoint, angleInDegrees, xTouch, yTouch)
        computeMovePoint(firstPoint)
    }

    internal fun computeSecondPoint(angleInDegrees: Float, xTouch: Float, yTouch: Float) {
        computeRotatePoint(secondPoint, angleInDegrees, xTouch, yTouch)
        computeMovePoint(secondPoint)
    }

    internal fun computeThirdPoint(angleInDegrees: Float, xTouch: Float, yTouch: Float) {
        computeRotatePoint(thirdPoint, angleInDegrees, xTouch, yTouch)
        computeMovePoint(thirdPoint)
    }

    internal fun computeFourthPoint(angleInDegrees: Float, xTouch: Float, yTouch: Float) {
        computeRotatePoint(fourthPoint, angleInDegrees, xTouch, yTouch)
        computeMovePoint(fourthPoint)
    }

    internal fun isInFirstPointPosition(xTouch: Float, yTouch: Float, rotatedRectangle: RotatedRectangle) = with(rotatedRectangle) {
        isInPosition(
                xTouch,
                yTouch,
                leftTopPoint.x,
                leftTopPoint.y,
                computeCoordinatePoint(rightTopPoint.x, leftTopPoint.x, DIVIDE_INTO_QUARTERS),
                computeCoordinatePoint(rightTopPoint.y, leftTopPoint.y, DIVIDE_INTO_QUARTERS),
                computeCoordinatePoint(rightBottomPoint.x, leftBottomPoint.x, DIVIDE_INTO_QUARTERS),
                computeCoordinatePoint(rightBottomPoint.y, leftBottomPoint.y, DIVIDE_INTO_QUARTERS),
                leftBottomPoint.x,
                leftBottomPoint.y)
    }

    internal fun isInSecondPointPosition(xTouch: Float, yTouch: Float, rotatedRectangle: RotatedRectangle) = with(rotatedRectangle) {
        isInPosition(
                xTouch,
                yTouch,
                computeCoordinatePoint(rightTopPoint.x, leftTopPoint.x, DIVIDE_INTO_QUARTERS),
                computeCoordinatePoint(rightTopPoint.y, leftTopPoint.y, DIVIDE_INTO_QUARTERS),
                computeCoordinatePoint(leftTopPoint.x, rightTopPoint.x, DIVIDE_INTO_HALF),
                computeCoordinatePoint(leftTopPoint.y, rightTopPoint.y, DIVIDE_INTO_HALF),
                computeCoordinatePoint(leftBottomPoint.x, rightBottomPoint.x, DIVIDE_INTO_HALF),
                computeCoordinatePoint(leftBottomPoint.y, rightBottomPoint.y, DIVIDE_INTO_HALF),
                computeCoordinatePoint(rightBottomPoint.x, leftBottomPoint.x, DIVIDE_INTO_QUARTERS),
                computeCoordinatePoint(rightBottomPoint.y, leftBottomPoint.y, DIVIDE_INTO_QUARTERS))
    }

    internal fun isInThirdPointPosition(xTouch: Float, yTouch: Float, rotatedRectangle: RotatedRectangle) = with(rotatedRectangle) {
        isInPosition(
                xTouch,
                yTouch,
                computeCoordinatePoint(leftTopPoint.x, rightTopPoint.x, DIVIDE_INTO_HALF),
                computeCoordinatePoint(leftTopPoint.y, rightTopPoint.y, DIVIDE_INTO_HALF),
                computeCoordinatePoint(leftTopPoint.x, rightTopPoint.x, DIVIDE_INTO_QUARTERS),
                computeCoordinatePoint(leftTopPoint.y, rightTopPoint.y, DIVIDE_INTO_QUARTERS),
                computeCoordinatePoint(leftBottomPoint.x, rightBottomPoint.x, DIVIDE_INTO_QUARTERS),
                computeCoordinatePoint(leftBottomPoint.y, rightBottomPoint.y, DIVIDE_INTO_QUARTERS),
                computeCoordinatePoint(leftBottomPoint.x, rightBottomPoint.x, DIVIDE_INTO_HALF),
                computeCoordinatePoint(leftBottomPoint.y, rightBottomPoint.y, DIVIDE_INTO_HALF))
    }

    internal fun isInFourthPointPosition(xTouch: Float, yTouch: Float, rotatedRectangle: RotatedRectangle) = with(rotatedRectangle) {
        isInPosition(
                xTouch,
                yTouch,
                computeCoordinatePoint(leftTopPoint.x, rightTopPoint.x, DIVIDE_INTO_QUARTERS),
                computeCoordinatePoint(leftTopPoint.y, rightTopPoint.y, DIVIDE_INTO_QUARTERS),
                rightTopPoint.x,
                rightTopPoint.y,
                rightBottomPoint.x,
                rightBottomPoint.y,
                computeCoordinatePoint(leftBottomPoint.x, rightBottomPoint.x, DIVIDE_INTO_QUARTERS),
                computeCoordinatePoint(leftBottomPoint.y, rightBottomPoint.y, DIVIDE_INTO_QUARTERS))
    }

    /**
     * Calculate the ratio of the position of the points of the Bezier curve taking into account the width and height
     * of the rectangle [rotatedRectangle] calculated with the previous value of the difference [previousDiff]
     *
     * @param rotatedRectangle    The rectangle for calculating width and height
     * @param             diff    The current scaling
     */
    internal fun toRatioPoint(rotatedRectangle: RotatedRectangle, diff: Float) {
        val leftX = rotatedRectangle.leftTopPoint.x - previousDiff          // calculating the position of the left edge of the rectangle, taking into account the previous difference
        val rightX = rotatedRectangle.rightTopPoint.x + previousDiff        // calculating the position of the right edge of the rectangle, taking into account the previous difference
        val topY = rotatedRectangle.leftTopPoint.y - previousDiff           // calculating the position of the top edge of the rectangle, taking into account the previous difference
        val bottomY = rotatedRectangle.leftBottomPoint.y + previousDiff     // calculating the position of the bottom edge of the rectangle, taking into account the previous difference

        computeRatioPoint(leftPoint, ratioLeftPoint, leftX, rightX, topY, bottomY)
        computeRatioPoint(rightPoint, ratioRightPoint, leftX, rightX, topY, bottomY)
        computeRatioPoint(firstPoint.drawPoint, firstPoint.ratioPoint, leftX, rightX, topY, bottomY)
        computeRatioPoint(secondPoint.drawPoint, secondPoint.ratioPoint, leftX, rightX, topY, bottomY)
        computeRatioPoint(thirdPoint.drawPoint, thirdPoint.ratioPoint, leftX, rightX, topY, bottomY)
        computeRatioPoint(fourthPoint.drawPoint, fourthPoint.ratioPoint, leftX, rightX, topY, bottomY)

        previousDiff = diff
    }

    /**
     * Calculates the ratio of the position of points for the Bezier curve with respect to the width and height of the [rotatedRectangle] rectangle,
     * taking into account the ratio
     *
     * @param rotatedRectangle    The rectangle for calculating width and height
     */
    internal fun fromRatioPoint(rotatedRectangle: RotatedRectangle) {
        computeDrawPoint(leftPoint, ratioLeftPoint, rotatedRectangle)
        computeDrawPoint(rightPoint, ratioRightPoint, rotatedRectangle)
        computeDrawPoint(firstPoint.drawPoint, firstPoint.ratioPoint, rotatedRectangle)
        computeDrawPoint(secondPoint.drawPoint, secondPoint.ratioPoint, rotatedRectangle)
        computeDrawPoint(thirdPoint.drawPoint, thirdPoint.ratioPoint, rotatedRectangle)
        computeDrawPoint(fourthPoint.drawPoint, fourthPoint.ratioPoint, rotatedRectangle)
    }

    /**
     * Calculates the ratio of the position of [drawPoint] with respect to the width and height of the rectangle
     *
     * @param  drawPoint    The point will be used for calculation of Bezier curve.
     * @param ratioPoint    The point for saving the ratio
     */
    private fun computeRatioPoint(drawPoint: PointF, ratioPoint: PointF, leftX: Float, rightX: Float, topY: Float, bottomY: Float) {
        ratioPoint.set(
                (drawPoint.x - leftX) / (rightX - leftX),
                (drawPoint.y - topY) / (bottomY - topY)
        )
    }

    /**
     * Calculates the position of [drawPoint] with respect to width and height
     * of the [rotatedRectangle] rectangle using [ratioPoint]
     *
     * @param        drawPoint    The point will be used for calculation of Bezier curve.
     * @param       ratioPoint    The point for calculate [drawPoint]
     * @param rotatedRectangle    The rectangle for calculating width and height
     */
    private fun computeDrawPoint(drawPoint: PointF, ratioPoint: PointF, rotatedRectangle: RotatedRectangle) {
        drawPoint.set(
                rotatedRectangle.leftTopPoint.x + ((rotatedRectangle.rightTopPoint.x - rotatedRectangle.leftTopPoint.x) * ratioPoint.x),
                rotatedRectangle.leftTopPoint.y + ((rotatedRectangle.leftBottomPoint.y - rotatedRectangle.leftTopPoint.y) * ratioPoint.y)
        )
    }

    /**
     * Set isChangePoint and isMovePoint flags to default
     */
    internal fun reset() {
        firstPoint.isChangePoint = false
        secondPoint.isChangePoint = false
        thirdPoint.isChangePoint = false
        fourthPoint.isChangePoint = false

        firstPoint.isMoveInPointPosition = false
        secondPoint.isMoveInPointPosition = false
        thirdPoint.isMoveInPointPosition = false
        fourthPoint.isMoveInPointPosition = false
    }

    /**
     * Set isChangePoint and isMovePoint flags to default and
     * initializes points for calculating the Bezier curve
     */
    internal fun resetPoint(selectorRect: RotatedRectangle,
                            textMeasurement: Float,
                            translateX: Float,
                            translateY: Float,
                            textSize: Float,
                            diff: Float) {

        val tempSelectorRect = RotatedRectangle()
        with(selectorRect) {
            tempSelectorRect.set(
                    leftTopPoint.x - translateX,
                    leftTopPoint.y - translateY,
                    rightBottomPoint.x - translateX,
                    rightBottomPoint.y - translateY)
        }

        val currentWeightRect = tempSelectorRect.rightTopPoint.x - tempSelectorRect.leftTopPoint.x
        val minWeightRect = (textMeasurement + textSize) * DOUBLE_VALUE
        val diffWeight = minWeightRect - currentWeightRect

        with(tempSelectorRect) {
            val offsetX = (diffWeight / HALF_DELIMITER) + diff
            leftTopPoint.x -= offsetX
            rightTopPoint.x += offsetX
        }

        xLeft -= textMeasurement - textMeasure
        xRight += textMeasurement - textMeasure

        init(tempSelectorRect, textMeasurement)
        reset()
    }

    internal fun setOffsetCenterCanvas(offsetX: Float, offsetY: Float) {
        if (!isSetOffsetCenterCanvas) {

            leftPoint.set(leftPoint.x + offsetX, leftPoint.y + offsetY)
            rightPoint.set(rightPoint.x + offsetX, rightPoint.y + offsetY)
            firstPoint.drawPoint.set(firstPoint.drawPoint.x + offsetX, firstPoint.drawPoint.y + offsetY)
            secondPoint.drawPoint.set(secondPoint.drawPoint.x + offsetX, secondPoint.drawPoint.y + offsetY)
            thirdPoint.drawPoint.set(thirdPoint.drawPoint.x + offsetX, thirdPoint.drawPoint.y + offsetY)
            fourthPoint.drawPoint.set(fourthPoint.drawPoint.x + offsetX, fourthPoint.drawPoint.y + offsetY)

            xLeft += offsetX
            xRight += offsetX
            yTop += offsetY
            yBottom += offsetY

            isSetOffsetCenterCanvas = !isSetOffsetCenterCanvas
        }
    }

    /**
     * Set points for calculating the Bezier curve
     *
     * @param rotatedRectangle    The rectangle for setting the points of the Bezier curve to its initial position
     * @param  textMeasurement    The current width of the text
     */
    private fun init(rotatedRectangle: RotatedRectangle, textMeasurement: Float) {
        val fourthX = computeCoordinatePoint(rotatedRectangle.rightTopPoint.x, rotatedRectangle.leftTopPoint.x, DIVIDE_INTO_QUARTERS)
        val halfX = computeCoordinatePoint(rotatedRectangle.leftTopPoint.x, rotatedRectangle.rightTopPoint.x, DIVIDE_INTO_HALF)
        val thirdX = computeCoordinatePoint(rotatedRectangle.leftTopPoint.x, rotatedRectangle.rightTopPoint.x, DIVIDE_INTO_QUARTERS)

        val halfY = computeCoordinatePoint(rotatedRectangle.leftTopPoint.y, rotatedRectangle.leftBottomPoint.y, DIVIDE_INTO_HALF)

        firstPoint.drawPoint.set(fourthX, halfY)
        secondPoint.drawPoint.set(halfX, halfY)
        thirdPoint.drawPoint.set(halfX, halfY)
        fourthPoint.drawPoint.set(thirdX, halfY)

        leftPoint.set(rotatedRectangle.leftTopPoint.x, halfY)
        rightPoint.set(rotatedRectangle.rightTopPoint.x, halfY)

        textMeasure = textMeasurement
    }

    /**
     * Calculates the distance to which want to move the rectangle's borders
     * so that the text does not go beyond its borders
     *
     * @param        textSize    The current text size
     * @param textMeasurement    The current width of the text
     */
    private fun computeScale(textSize: Float, textMeasurement: Float) {
        // yTop     The Y coordinate of the top of the rectangle
        // yBottom  The Y coordinate of the bottom of the rectangle
        // xLeft    The X coordinate of the left side of the rectangle
        // xRight   The X coordinate of the right side of the rectangle
        distanceTop = if (minY != Float.POSITIVE_INFINITY) (yTop - (minY - textSize)) else DEFAULT_DISTANCE
        distanceBottom = if (maxY != Float.NEGATIVE_INFINITY) ((maxY + textSize) - yBottom) else DEFAULT_DISTANCE

        val diffTextMeasurement = textMeasurement - textMeasure

        distanceLeft = if (minX != Float.POSITIVE_INFINITY) ((xLeft - diffTextMeasurement) - (minX - textSize)) else DEFAULT_DISTANCE
        distanceRight = if (maxX != Float.NEGATIVE_INFINITY) ((maxX + textSize) - (xRight + diffTextMeasurement)) else DEFAULT_DISTANCE

        // Set minX and maxX to the default value
        minX = Float.POSITIVE_INFINITY
        maxX = Float.NEGATIVE_INFINITY

        // Set minY and maxY to the default value
        minY = Float.POSITIVE_INFINITY
        maxY = Float.NEGATIVE_INFINITY
    }

    private fun computeMinAndMax(x: Float, y: Float) {
        if (x < minX) {
            minX = x
        }
        if (x > maxX) {
            maxX = x
        }

        if (y < minY) {
            minY = y
        }
        if (y > maxY) {
            maxY = y
        }
    }

    /**
     * Check if point is inside this rectangle
     *
     * @param  xTouch    X coordinate of point to check
     * @param  yTouch    Y coordinate of point to check
     * @param      x1    The X coordinate of the left top of the rectangle
     * @param      y1    The Y coordinate of the left top of the rectangle
     * @param      x2    The X coordinate of the right top of the rectangle
     * @param      y2    The Y coordinate of the right top of the rectangle
     * @param      x3    The X coordinate of the left bottom of the rectangle
     * @param      y3    The Y coordinate of the left bottom of the rectangle
     * @param      x4    The X coordinate of the right bottom of the rectangle
     * @param      y4    The Y coordinate of the right bottom of the rectangle
     *
     * @return true if point is inside of this rectangle and false in otherwise
     */
    private fun isInPosition(xTouch: Float, yTouch: Float, x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float, x4: Float, y4: Float): Boolean {
        pseudoScalars.clear()
        // calculate pseudo scalar for every edge of rectangle and checking point in counterclockwise order
        with(pseudoScalars) {
            add(calculatePseudoScalar(xTouch, yTouch, x1, y1, x2, y2))
            add(calculatePseudoScalar(xTouch, yTouch, x2, y2, x3, y3))
            add(calculatePseudoScalar(xTouch, yTouch, x3, y3, x4, y4))
            add(calculatePseudoScalar(xTouch, yTouch, x4, y4, x1, y1))
        }

        pseudoScalars.forEach {
            if (it < MIN_POSITIVE) return false
        }
        return true
    }

    /**
     * Compute the coordinates of an arbitrary point of a segment of formula
     *
     * @param   x1    coordinates of the beginning of the point of a segment
     * @param   x2    coordinates of the end of a point of a segment
     * @param part    coefficient to be divided
     *
     * @return coordinates of a point on a line
     */
    private fun computeCoordinatePoint(x1: Float, x2: Float, part: Int): Float = (x1 + part * x2) / (1 + part)

    /**
     * The method compute the movement of the [deviationPoint] in pixel
     *
     */
    private fun computeMovePoint(deviationPoint: DeviationPoint) {
        with(deviationPoint) {
            if (!isMoveInPointPosition) {
                touchPoint.set(movePoint)
                currentPoint.set(drawPoint)

                deviationPoint.isMoveInPointPosition = true
            }

            val x = currentPoint.x + movePoint.x - touchPoint.x
            val y = currentPoint.y + movePoint.y - touchPoint.y

            drawPoint.set(x, y)
        }
    }

    /**
     * Compute the rotate for the location of the [deviationPoint]
     *
     * @param         xTouch    X coordinate of point to check
     * @param         yTouch    Y coordinate of point to check
     * @param angleInDegrees    Angle of the rotation in degrees
     */
    private fun computeRotatePoint(deviationPoint: DeviationPoint, angleInDegrees: Float, xTouch: Float, yTouch: Float) {
        val rotateX = rotateX(xTouch, yTouch, degreesToRadians(angleInDegrees))
        val rotateY = rotateY(xTouch, yTouch, degreesToRadians(angleInDegrees))

        deviationPoint.movePoint.set(rotateX, rotateY)
    }

    /**
     * Return rotated value of [x] coordinate
     *
     * @param              x    Coordinate for rotation
     * @param              y    Coordinate for rotation
     * @param angleInRadians    Angle of the rotation in degrees
     *
     * @return                  Rotated x coordinate
     */
    private fun rotateX(x: Float, y: Float, angleInRadians: Double) =
            ((x + (y * Math.cos(angleInRadians) * Math.sin(angleInRadians)) - (x * Math.sin(angleInRadians) * Math.sin(angleInRadians))) /
                    Math.cos(angleInRadians)).toFloat()

    /**
     * Return rotated value of [y] coordinate
     *
     * @param              x    Coordinate for rotation
     * @param              y    Coordinate for rotation
     * @param angleInRadians    Angle of the rotation in degrees
     *
     * @return                  Rotated y coordinate
     */
    private fun rotateY(x: Float, y: Float, angleInRadians: Double) =
            (y * Math.cos(angleInRadians) - x * Math.sin(angleInRadians)).toFloat()

    private fun degreesToRadians(angleInDegrees: Float) = Math.toRadians(angleInDegrees.toDouble())

    /**
     * Calculates an element of the Bernstein polynomial
     *
     * @param   numberVertex    The number of the vertex
     * @param numberVertices    The number of vertices
     * @param  positionCurve    The position of the curve (from 0 to 1)
     *
     * @return i-th element of the Bernstein polynomial
     */
    private fun getBezierBasis(numberVertex: Int, numberVertices: Int, positionCurve: Double): Double {
        fun fact(n: Int): Float {
            return if (n <= END_POSITION_CURVE) COMPLETE else n * fact(n - 1)
        }

        // consider the i-th element of the Bernstein polynomial
        return (fact(numberVertices) / (fact(numberVertex) * fact(numberVertices - numberVertex))
                ) * Math.pow(positionCurve, numberVertex.toDouble()) * Math.pow(1 - positionCurve, (numberVertices - numberVertex).toDouble())
    }

    /**
     * Calculates the list of points of the Bezier curve
     *
     * @param  arr    The array of control points
     * @param step    Is the step in calculating the curve (0 <step <1), default is [DEFAULT_STEP]
     *
     * @return coordinates of the Bezier curve
     */
    private fun getBezierCurve(arr: List<Array<Float>>, step: Float = DEFAULT_STEP): List<Array<Float>> {

        val coordinateCurve = mutableListOf<Array<Float>>()

        var positionCurve = START_POSITION_CURVE
        while (positionCurve < END_POSITION_CURVE + step) {
            if (positionCurve > END_POSITION_CURVE) {
                positionCurve = END_POSITION_CURVE
            }

            val index = coordinateCurve.size

            coordinateCurve.add(index, arrayOf(DEFAULT_ARRAY_VALUE, DEFAULT_ARRAY_VALUE))

            var numberVertex = DEFAULT_NUMBER_VERTEX
            while (numberVertex < arr.size) {
                val b = getBezierBasis(numberVertex, arr.size - 1, positionCurve.toDouble())

                coordinateCurve[index][INDEX_COOR_X] += (arr[numberVertex][INDEX_COOR_X] * b).toFloat()
                coordinateCurve[index][INDEX_COOR_Y] += (arr[numberVertex][INDEX_COOR_Y] * b).toFloat()
                numberVertex++
            }
            positionCurve += step
        }

        return coordinateCurve
    }

    constructor() : super()

    constructor(source: Parcel?) : super() {
        source?.apply {
            leftPoint = readParcelable(PointF::class.java.classLoader)
            rightPoint = readParcelable(PointF::class.java.classLoader)
            firstPoint = readParcelable(PointF::class.java.classLoader)
            secondPoint = readParcelable(PointF::class.java.classLoader)
            thirdPoint = readParcelable(PointF::class.java.classLoader)
            fourthPoint = readParcelable(PointF::class.java.classLoader)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.apply {
            writeParcelable(leftPoint, flags)
            writeParcelable(rightPoint, flags)
            writeParcelable(firstPoint, flags)
            writeParcelable(secondPoint, flags)
            writeParcelable(thirdPoint, flags)
            writeParcelable(fourthPoint, flags)
        }
    }
}