package com.cleveroad.droidart


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.RequiresApi
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.AppCompatDrawableManager
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.cleveroad.droidart.MultiTouchScaleModule.Companion.calculateDistance


class EditorView : View {

    companion object {

        private const val FIRST_TOUCH = 0
        private const val SECOND_TOUCH = 1
        private const val DEFAULT_TEXT_SIZE = 25F
        private const val DEFAULT_ELEVATION_PERCENT = 0
        private const val DEFAULT_BLUR = 1F
        private const val STROKE_WIDTH_DASH_PAINT = 1F
        private const val STROKE_WIDTH_BUTTONS_PAINT = 4F
        private const val STROKE_WIDTH_SELECTOR_PAINT = 4F
        private const val STROKE_WIDTH_TEXT_PAINT = 10F
        private const val OFFSET_FROM_START_POSITION = 0F
        private const val HALF_DELIMITER = 2F
        private const val QUARTER_DELIMITER = 4F
        private const val DIVIDE_INTO_QUARTERS = 3
        private const val DIVIDE_INTO_HALF = 1
        private const val DEFAULT_OFFSET = 0F
        private const val DEFAULT_SHADOW_OFFSET = 0F
        private const val DASH_PATH_ON_DISTANCE = 30F
        private const val DASH_PATH_OFF_DISTANCE = 10F
        private const val DEFAULT_MAX_DIFFERENCE = 0F
        private const val DEFAULT_PHASE = 0F
        private const val DEFAULT_CENTER_CANVAS = 0F
        private const val LEFT_DRAWABLE_BOUNDING = 0
        private const val RIGHT_DRAWABLE_BOUNDING = 0
    }

    private var scaleBitmap: Bitmap? = null
    private var changeViewTextBitmap: Bitmap? = null
    private var resetChangeViewTextBitmap: Bitmap? = null

    private var rotatedSelectorRectangle = RotatedRectangle()
    private var drawSelectorRectangle = RotatedRectangle()

    private var translationSettings = TranslationSettings()
    private var textSettings = TextSettings()
    private var selectorSettings = SelectorSettings()
    private var buttonSettings = ButtonSettings()
    private var rotateSettings = RotateSettings()
    private var changeViewTextSettings = ChangeViewTextSettings()
    private var scaleSettings = ScaleSettings()
    private var shadowSettings = ShadowSettings()
    private var offsetCenterCanvas = PointF()

    // we don't need to save this variables in state
    private val metrics = DisplayMetrics()
    private var currentAction = TouchActions.NO_ACTION
    private var previousAction = TouchActions.NO_ACTION
    private val scaleRotateRect = RectF()
    private var xPos = DEFAULT_CENTER_CANVAS
    private var yPos = DEFAULT_CENTER_CANVAS
    private var needToRestoreTranslations = false
    private var initOffsetCenterCanvas = true
    private var maxDiff = DEFAULT_MAX_DIFFERENCE
    private val onDistance = DASH_PATH_ON_DISTANCE
    private val offDistance = DASH_PATH_OFF_DISTANCE
    private val phase = DEFAULT_PHASE
    private var shadowOffset = DEFAULT_SHADOW_OFFSET

    private var offsetX = DEFAULT_OFFSET
    private var offsetY = DEFAULT_OFFSET

    private var handled = true
    private var initCenterPoint = false
    private var isShowScaleRotateButton = ShowButtonOnSelector.SHOW_BUTTON
    private var isShowChangeViewTextButton = ShowButtonOnSelector.SHOW_BUTTON
    private var isShowResetViewTextButton = ShowButtonOnSelector.SHOW_BUTTON

    private val touchHandlers = mapOf(getTouchHandlerPair(MotionEvent.ACTION_DOWN),
            getTouchHandlerPair(MotionEvent.ACTION_POINTER_DOWN),
            getTouchHandlerPair(MotionEvent.ACTION_POINTER_UP),
            getTouchHandlerPair(MotionEvent.ACTION_MOVE))

    private val upOrCancelTouchHandler = getTouchHandler()

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val selectorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val buttonsPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dashPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val linePath = Path()
    private val pathRect = Path()
    private var changeViewTextModeCallback: HashSet<ChangeViewTextCallback> = hashSetOf()
    private var touchCallback: HashSet<TouchEventCallback> = hashSetOf()
    private var touchListener = GestureDetector(context, TouchListener())

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    override fun onSaveInstanceState(): Parcelable {
        val outState = super.onSaveInstanceState()
        return SavedState(outState, this)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        (state as? SavedState)?.let {
            super.onRestoreInstanceState(it.superState)
            scaleBitmap = it.scaleBitmap
            changeViewTextBitmap = it.changeViewTextBitmap
            resetChangeViewTextBitmap = it.changeBackViewTextBitmap
            it.rotatedSelectorRectangle?.let { rotatedSelectorRectangle = it }
            it.translationSettings?.let { translationSettings = it }
            it.currentAction?.let { currentAction = it }
            it.previousAction?.let { previousAction = it }
            it.textSettings?.let { textSettings = it }
            it.selectorSettings?.let { selectorSettings = it }
            it.buttonSettings?.let { buttonSettings = it }
            it.rotateSettings?.let { rotateSettings = it }
            it.changeViewTextSettings?.let { changeViewTextSettings = it }
            it.scaleSettings?.let { scaleSettings = it }
            it.shadowSettings?.let { shadowSettings = it }
            it.offsetCenterCanvas?.let { offsetCenterCanvas = it }
            initTextPaint()
            initSelectorPaint()
            initButtonsPaint()
            initDashPaint()
            needToRestoreTranslations = true
        } ?: run {
            super.onRestoreInstanceState(state)
        }
    }

    /**
     * Method encapsulate logic of restoring translations after state restoring
     *
     * @param x X coordinate of center point
     * @param y Y coordinate of center point
     */
    private fun restoreTranslations(x: Float, y: Float) {
        if (needToRestoreTranslations) {
            with(rotateSettings) {
                setTranslation(translationSettings.translationX, translationSettings.translationY)
                init(rotatedSelectorRectangle.centerX(), rotatedSelectorRectangle.centerY(), x, y)
                store()
                reset()
            }
            with(scaleSettings) {
                setTranslation(translationSettings.translationX, translationSettings.translationY)
                init(rotatedSelectorRectangle.centerX(), rotatedSelectorRectangle.centerY(), x, y)
                store()
                reset()
            }
            needToRestoreTranslations = false
        }
    }

    /**
     * Main view init. init properties with values for attributes or with default values
     */
    private fun init(context: Context, attrs: AttributeSet?) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditorView)
        with(typedArray) {
            with(textSettings) {
                textColor = getColor(R.styleable.EditorView_text_color, Color.BLACK)
                text = getString(R.styleable.EditorView_text) ?: ""
                textSize = getDimension(R.styleable.EditorView_text_size, convertSpToFloat(getContext(), DEFAULT_TEXT_SIZE))
                if (typedArray.hasValue(R.styleable.EditorView_font_id)) {
                    fontId = getResourceId(R.styleable.EditorView_font_id, Typeface.DEFAULT.style)
                }
                invalidateSelectorVisibility(text)
            }
            with(shadowSettings) {
                elevationPercent = getInteger(R.styleable.EditorView_text_elevation_percent, DEFAULT_ELEVATION_PERCENT)
                shadowColor = getColor(R.styleable.EditorView_text_shadow_color, ShadowSettings.NO_SHADOW)
                shadowBlurRadius = getFloat(R.styleable.EditorView_text_shadow_blur_radius, DEFAULT_BLUR)
            }
            with(selectorSettings) {
                selectorColor = getColor(R.styleable.EditorView_selector_color, Color.RED)
                measure(textSettings.textSize)
            }
            with(buttonSettings) {
                buttonColor = getColor(R.styleable.EditorView_button_color, Color.RED)
            }

            scaleBitmap = initBitmaps(
                    typedArray.getResourceId(R.styleable.EditorView_icon_scale_button, R.drawable.arrow_expand_all))
            changeViewTextBitmap = initBitmaps(
                    typedArray.getResourceId(R.styleable.EditorView_icon_change_view_button, R.drawable.change_view_text))
            resetChangeViewTextBitmap = initBitmaps(
                    typedArray.getResourceId(R.styleable.EditorView_icon_reset_button, R.drawable.reset_change_view_text))

            recycle()
        }
        initTextPaint()
        initSelectorPaint()
        initButtonsPaint()
        initDashPaint()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        display.getMetrics(metrics)
        val width = metrics.widthPixels.toFloat()
        val height = metrics.heightPixels.toFloat()
        maxDiff = if (height > width) width else height
    }

    /**
     * Init Paint for selector drawing on canvas
     *
     * @see [Paint]
     */
    private fun initSelectorPaint() = with(selectorPaint) {
        color = selectorSettings.selectorColor
        strokeWidth = STROKE_WIDTH_SELECTOR_PAINT
        style = Paint.Style.STROKE
    }


    /**
     * Init Paint for button drawing on canvas
     *
     * @see [Paint]
     */
    private fun initButtonsPaint() = with(buttonsPaint) {
        color = buttonSettings.buttonColor
        strokeWidth = STROKE_WIDTH_BUTTONS_PAINT
        style = Paint.Style.FILL_AND_STROKE
    }

    /**
     * Init Paint for button drawing on canvas
     *
     * @see [Paint]
     */
    private fun initDashPaint() = with(dashPaint) {
        color = Color.GRAY
        strokeWidth = STROKE_WIDTH_DASH_PAINT
        style = Paint.Style.STROKE
        setPathEffect(DashPathEffect(floatArrayOf(onDistance, offDistance), phase))
    }

    /**
     * Init Paint for text drawing on Canvas
     *
     * @see [Paint]
     */
    private fun initTextPaint() {
        initFont()
        with(textPaint) {
            textSize = textSettings.textSize
            color = textSettings.textColor
            strokeWidth = STROKE_WIDTH_TEXT_PAINT
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL
            textShadowColor = shadowSettings.shadowColor
        }
        textSettings.measureText(textPaint)
    }

    /**
     * Init text font from font id
     */
    private fun initFont() = with(textSettings) {
        // TODO: this operation can throw Runtime exception if id is incorrect, think about catching it
        if (fontId != Typeface.DEFAULT.style) {
            textPaint.typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                resources.getFont(fontId)
            } else {
                ResourcesCompat.getFont(context, fontId)
            }
        } else {
            textPaint.typeface = Typeface.DEFAULT
        }
    }

    /**
     * Init all bitmaps from resources for drawing on Canvas
     *
     * @see [Bitmap]
     */
    @SuppressLint("RestrictedApi")
    private fun initBitmaps(@DrawableRes resId: Int): Bitmap {
        var drawable = AppCompatDrawableManager.get().getDrawable(context, resId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // if won't be changed - remove
            drawable = DrawableCompat.wrap(drawable).mutate()
        }
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(LEFT_DRAWABLE_BOUNDING, RIGHT_DRAWABLE_BOUNDING, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchListener.onTouchEvent(event)
        handled = (touchHandlers[event.actionMasked]
                ?: upOrCancelTouchHandler)
                .handleTouch(event)
        if (handled) {
            invalidate()
        }
        return handled
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        xPos = canvas.width / HALF_DELIMITER
        yPos = canvas.height / HALF_DELIMITER

        if ((xPos - offsetCenterCanvas.x) != DEFAULT_OFFSET || (yPos - offsetCenterCanvas.y) != DEFAULT_OFFSET) {
            initOffsetCenterCanvas = true
            changeViewTextSettings.isSetOffsetCenterCanvas = false
        }

        changeViewTextSettings.setOffsetCenterCanvas(xPos - offsetCenterCanvas.x, yPos - offsetCenterCanvas.y)

        setupSelectorRect(xPos, yPos)
        rotationRect()
        rotationPointCurve()
        changeViewTextSettings.computeMinMaxBezierCurve(
                scaleSettings.diff,
                textSettings.textSize,
                textSettings.textMeasurement)

        val path = changeViewTextSettings.computePathBezierCurve()
        drawText(canvas, path)
        drawSelector(canvas, path)

        if (initOffsetCenterCanvas) {
            offsetCenterCanvas.set(xPos, yPos)
            initOffsetCenterCanvas = !initOffsetCenterCanvas
        }
    }

    /**
     * Setup RotatedRectangle for Selector
     *
     * @param x center coordinate of selector
     * @param y center coordinate of selector
     *
     */
    private fun setupSelectorRect(x: Float, y: Float) {
        rotatedSelectorRectangle.set(
                (x - textSettings.textMeasurement - textSettings.textSize - changeViewTextSettings.distanceLeft),
                (y - textSettings.textSize - changeViewTextSettings.distanceTop),
                (x + textSettings.textMeasurement + textSettings.textSize + changeViewTextSettings.distanceRight),
                (y + textSettings.textSize + changeViewTextSettings.distanceBottom))

        restoreTranslations(x, y)
        scaleSettings.computeScale(maxDiff)

        changeViewTextSettings.toRatioPoint(rotatedSelectorRectangle, scaleSettings.diff)
        // set current difference
        scaleSettings.scaleRectangle(rotatedSelectorRectangle)
        changeViewTextSettings.fromRatioPoint(rotatedSelectorRectangle)

        val translateX = translationSettings.translationX + offsetX
        val translateY = translationSettings.translationY + offsetY

        // set translation and offset to RotatedRectangle
        with(rotatedSelectorRectangle) {
            leftTopPoint.set(
                    leftTopPoint.x + translateX,
                    leftTopPoint.y + translateY
            )
            rightTopPoint.set(
                    rightTopPoint.x + translateX,
                    rightTopPoint.y + translateY
            )
            rightBottomPoint.set(
                    rightBottomPoint.x + translateX,
                    rightBottomPoint.y + translateY
            )
            leftBottomPoint.set(
                    leftBottomPoint.x + translateX,
                    leftBottomPoint.y + translateY
            )
        }

        changeViewTextSettings.initChangeViewTextSettings(rotatedSelectorRectangle, textSettings.textMeasurement)

        // set center point for RotateSettings
        if (!initCenterPoint) {
            rotateSettings.setCenterPoint(rotatedSelectorRectangle.centerX(), rotatedSelectorRectangle.centerY())
            initCenterPoint = !initCenterPoint
        }
    }

    /**
     * Method for saving rotation
     */
    private fun rotationRect() {
        val angleInRadians = degreesToRadians(rotateSettings.rotation())

        with(drawSelectorRectangle) {
            setLefTop(
                    rotateX(rotatedSelectorRectangle.leftTopPoint.x,
                            rotatedSelectorRectangle.leftTopPoint.y,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians),
                    rotateY(rotatedSelectorRectangle.leftTopPoint.x,
                            rotatedSelectorRectangle.leftTopPoint.y,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians))

            setLeftBottom(
                    rotateX(rotatedSelectorRectangle.leftBottomPoint.x,
                            rotatedSelectorRectangle.leftBottomPoint.y,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians),
                    rotateY(rotatedSelectorRectangle.leftBottomPoint.x,
                            rotatedSelectorRectangle.leftBottomPoint.y,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians))

            setRightTop(
                    rotateX(rotatedSelectorRectangle.rightTopPoint.x,
                            rotatedSelectorRectangle.rightTopPoint.y,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians),
                    rotateY(rotatedSelectorRectangle.rightTopPoint.x,
                            rotatedSelectorRectangle.rightTopPoint.y,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians))

            setRightBottom(
                    rotateX(rotatedSelectorRectangle.rightBottomPoint.x,
                            rotatedSelectorRectangle.rightBottomPoint.y,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians),
                    rotateY(rotatedSelectorRectangle.rightBottomPoint.x,
                            rotatedSelectorRectangle.rightBottomPoint.y,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians))
        }
    }

    /**
     * Method for rotating points along which the Bezier curve will be constructed
     */
    private fun rotationPointCurve() {
        val translateX = translationSettings.translationX + offsetX
        val translateY = translationSettings.translationY + offsetY
        val angleInRadians = degreesToRadians(rotateSettings.rotation())

        val diffChangeViewTextSettingsX = calculateDistance(changeViewTextSettings.leftPoint, changeViewTextSettings.rightPoint)
        val diffRotatedSelectorRectangleX = calculateDistance(rotatedSelectorRectangle.leftTopPoint, rotatedSelectorRectangle.rightTopPoint)
        val diffBetweenRectAndCurve = (diffRotatedSelectorRectangleX - diffChangeViewTextSettingsX) / HALF_DELIMITER

        with(changeViewTextSettings) {
            setLeftPoint(
                    rotateX(leftPoint.x + translateX - diffBetweenRectAndCurve,
                            leftPoint.y + translateY,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians),
                    rotateY(leftPoint.x + translateX - diffBetweenRectAndCurve,
                            leftPoint.y + translateY,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians))

            setRightPoint(
                    rotateX(rightPoint.x + translateX + diffBetweenRectAndCurve,
                            rightPoint.y + translateY,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians),
                    rotateY(rightPoint.x + translateX + diffBetweenRectAndCurve,
                            rightPoint.y + translateY,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians))

            setFirstPoint(
                    rotateX(firstPoint.drawPoint.x + translateX,
                            firstPoint.drawPoint.y + translateY,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians),
                    rotateY(firstPoint.drawPoint.x + translateX,
                            firstPoint.drawPoint.y + translateY,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians))

            setSecondPoint(
                    rotateX(secondPoint.drawPoint.x + translateX,
                            secondPoint.drawPoint.y + translateY,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians),
                    rotateY(secondPoint.drawPoint.x + translateX,
                            secondPoint.drawPoint.y + translateY,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians))

            setThirdPoint(
                    rotateX(thirdPoint.drawPoint.x + translateX,
                            thirdPoint.drawPoint.y + translateY,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians),
                    rotateY(thirdPoint.drawPoint.x + translateX,
                            thirdPoint.drawPoint.y + translateY,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians))

            setFourthPoint(
                    rotateX(fourthPoint.drawPoint.x + translateX,
                            fourthPoint.drawPoint.y + translateY,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians),
                    rotateY(fourthPoint.drawPoint.x + translateX,
                            fourthPoint.drawPoint.y + translateY,
                            rotateSettings.centerX,
                            rotateSettings.centerY,
                            angleInRadians))
        }
    }

    /**
     * Draw text with translation
     *
     * @param  canvas Canvas for text drawing
     */
    private fun drawText(canvas: Canvas, path: Path) {
        scaleSettings.computeScaleFactor(textSettings, rotatedSelectorRectangle)
        rotateSettings.setEndToStart()
        textPaint.textSize = textSettings.textSize * scaleSettings.scaleFactor
        setupShadow()

        canvas.drawTextOnPath(textSettings.text,
                path,
                OFFSET_FROM_START_POSITION,
                textPaint.textSize / QUARTER_DELIMITER,
                textPaint)
    }

    private fun setupShadow() {
        if (shadowSettings.needToDrawShadow) {
            shadowOffset = shadowSettings.calculateShadowOffset(textPaint.textSize)
            textPaint.setShadowLayer(shadowSettings.shadowBlurRadius, shadowOffset, shadowOffset, textShadowColor)
        } else {
            textPaint.clearShadowLayer()
        }
    }

    /**
     * Draw selector around text
     *
     * @param  canvas Canvas for selector drawing
     */
    private fun drawSelector(canvas: Canvas, path: Path) {
        if (selectorSettings.needToShow) {

            pathRect.reset()
            pathRect.moveTo(drawSelectorRectangle.leftTopPoint.x, drawSelectorRectangle.leftTopPoint.y)
            pathRect.lineTo(drawSelectorRectangle.rightTopPoint.x, drawSelectorRectangle.rightTopPoint.y)
            pathRect.lineTo(drawSelectorRectangle.rightBottomPoint.x, drawSelectorRectangle.rightBottomPoint.y)
            pathRect.lineTo(drawSelectorRectangle.leftBottomPoint.x, drawSelectorRectangle.leftBottomPoint.y)
            pathRect.lineTo(drawSelectorRectangle.leftTopPoint.x, drawSelectorRectangle.leftTopPoint.y)

            canvas.drawPath(pathRect, selectorPaint)

            if (previousAction == TouchActions.CHANGE_VIEW_TEXT) {
                drawDottedLineOnRect(canvas, path)
                if (isShowResetViewTextButton == ShowButtonOnSelector.SHOW_BUTTON) {
                    drawResetChangeViewTextButton(canvas, drawSelectorRectangle.leftBottomPoint.x, drawSelectorRectangle.leftBottomPoint.y)
                }
            } else {
                if (isShowScaleRotateButton == ShowButtonOnSelector.SHOW_BUTTON) {
                    drawScaleRotateButton(canvas, drawSelectorRectangle.rightBottomPoint.x, drawSelectorRectangle.rightBottomPoint.y)
                }
            }
            if (isShowChangeViewTextButton == ShowButtonOnSelector.SHOW_BUTTON) {
                drawChangeViewTextButton(canvas, drawSelectorRectangle.leftTopPoint.x, drawSelectorRectangle.leftTopPoint.y)
            }
        }
    }

    /**
     * Draw button for scaling and rotation options
     *
     * @param  canvas Canvas for selector drawing
     * @param       x Coordinate for selector drawing
     * @param       y Coordinate for selector drawing
     */
    private fun drawScaleRotateButton(canvas: Canvas, x: Float, y: Float) {
        scaleRotateRect.set(x - selectorSettings.halfDiagonal,
                y - selectorSettings.halfDiagonal,
                x + selectorSettings.halfDiagonal,
                y + selectorSettings.halfDiagonal)
        canvas.drawCircle(x, y, selectorSettings.radius, buttonsPaint)
        canvas.drawBitmap(scaleBitmap, null, scaleRotateRect, null)
    }

    /**
     * Draw button for change view text
     *
     * @param  canvas Canvas for selector drawing
     * @param       x Coordinate for selector drawing
     * @param       y Coordinate for selector drawing
     */
    private fun drawChangeViewTextButton(canvas: Canvas, x: Float, y: Float) {
        scaleRotateRect.set(x - selectorSettings.halfDiagonal,
                y - selectorSettings.halfDiagonal,
                x + selectorSettings.halfDiagonal,
                y + selectorSettings.halfDiagonal)
        canvas.drawCircle(x, y, selectorSettings.radius, buttonsPaint)
        canvas.drawBitmap(changeViewTextBitmap, null, scaleRotateRect, null)
    }

    /**
     * Draw button for reset change view text
     *
     * @param  canvas Canvas for selector drawing
     * @param       x Coordinate for selector drawing
     * @param       y Coordinate for selector drawing
     */
    private fun drawResetChangeViewTextButton(canvas: Canvas, x: Float, y: Float) {
        scaleRotateRect.set(x - selectorSettings.halfDiagonal,
                y - selectorSettings.halfDiagonal,
                x + selectorSettings.halfDiagonal,
                y + selectorSettings.halfDiagonal)
        canvas.drawCircle(x, y, selectorSettings.radius, buttonsPaint)
        canvas.drawBitmap(resetChangeViewTextBitmap, null, scaleRotateRect, null)
    }

    /**
     * Draw button for scaling and rotation options
     *
     * @param  canvas Canvas for selector drawing
     */
    private fun drawDottedLineOnRect(canvas: Canvas, path: Path) {
        linePath.reset()
        with(drawSelectorRectangle) {
            linePath.moveTo(
                    computeCoordinatePoint(leftTopPoint.x, rightTopPoint.x, DIVIDE_INTO_QUARTERS),
                    computeCoordinatePoint(leftTopPoint.y, rightTopPoint.y, DIVIDE_INTO_QUARTERS))
            linePath.lineTo(
                    computeCoordinatePoint(leftBottomPoint.x, rightBottomPoint.x, DIVIDE_INTO_QUARTERS),
                    computeCoordinatePoint(leftBottomPoint.y, rightBottomPoint.y, DIVIDE_INTO_QUARTERS))

            linePath.moveTo(
                    computeCoordinatePoint(leftTopPoint.x, rightTopPoint.x, DIVIDE_INTO_HALF),
                    computeCoordinatePoint(leftTopPoint.y, rightTopPoint.y, DIVIDE_INTO_HALF))
            linePath.lineTo(
                    computeCoordinatePoint(leftBottomPoint.x, rightBottomPoint.x, DIVIDE_INTO_HALF),
                    computeCoordinatePoint(leftBottomPoint.y, rightBottomPoint.y, DIVIDE_INTO_HALF))

            linePath.moveTo(
                    computeCoordinatePoint(rightTopPoint.x, leftTopPoint.x, DIVIDE_INTO_QUARTERS),
                    computeCoordinatePoint(rightTopPoint.y, leftTopPoint.y, DIVIDE_INTO_QUARTERS))
            linePath.lineTo(
                    computeCoordinatePoint(rightBottomPoint.x, leftBottomPoint.x, DIVIDE_INTO_QUARTERS),
                    computeCoordinatePoint(rightBottomPoint.y, leftBottomPoint.y, DIVIDE_INTO_QUARTERS))
        }
        canvas.drawPath(linePath, dashPaint)
        canvas.drawPath(path, dashPaint)
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
    private fun computeCoordinatePoint(x1: Float, x2: Float, part: Int) = (x1 + part * x2) / (1 + part)

    /**
     * Checks if touch event [event] has coordinates in area for [TouchActions.DRAG] action
     * @see MotionEvent
     *
     * @param event MotionEvent for checking
     */
    private fun isInDragPosition(event: MotionEvent) =
            drawSelectorRectangle.checkIfContainsPoint(event.getX(FIRST_TOUCH), event.getY(FIRST_TOUCH))

    /**
     * Checks if touch event [event] has coordinates in area for [TouchActions.SCALE_ROTATE] action
     * @see MotionEvent
     *
     * @param event MotionEvent for checking
     */
    private fun isInScaleRotatePosition(event: MotionEvent) =
            isPointInCircle(event.getX(FIRST_TOUCH), event.getY(FIRST_TOUCH),
                    drawSelectorRectangle.rightBottomPoint.x,
                    drawSelectorRectangle.rightBottomPoint.y,
                    selectorSettings.radius)

    /**
     * Checks if touch event [event] has coordinates in area for [TouchActions.CHANGE_VIEW_TEXT] action
     * @see MotionEvent
     *
     * @param event MotionEvent for checking
     */
    private fun isInChangeViewTextPosition(event: MotionEvent) =
            isPointInCircle(event.getX(FIRST_TOUCH), event.getY(FIRST_TOUCH),
                    drawSelectorRectangle.leftTopPoint.x,
                    drawSelectorRectangle.leftTopPoint.y,
                    selectorSettings.radius)

    /**
     * Checks if touch event [event] has coordinates in area for [TouchActions.CHANGE_VIEW_TEXT] action
     * @see MotionEvent
     *
     * @param event MotionEvent for checking
     */
    private fun isInResetChangeViewTextPosition(event: MotionEvent) =
            isPointInCircle(event.getX(FIRST_TOUCH), event.getY(FIRST_TOUCH),
                    drawSelectorRectangle.leftBottomPoint.x,
                    drawSelectorRectangle.leftBottomPoint.y,
                    selectorSettings.radius)

    /**
     * Checks if the point with coordinates [pointX] and [pointY] is in the circle
     * with center in [circleCenterX] and [circleCenterY] and radius = [radius]
     */
    private fun isPointInCircle(pointX: Float, pointY: Float, circleCenterX: Float, circleCenterY: Float, radius: Float) =
            Math.hypot((pointX - circleCenterX).toDouble(), (pointY - circleCenterY).toDouble()) < radius.toDouble()

    private fun invalidateSelectorVisibility(text: String) {
        selectorSettings.needToShow = !TextUtils.isEmpty(text)
    }

    /**
     * Text value for displaying
     */
    // TODO: fix issue with no chars and displaying last char
    var text: String
        get() = textSettings.text
        set(text) {
            textSettings.text = text
            initTextPaint()
            invalidateSelectorVisibility(text)
            invalidate()
        }

    /**
     * Value of color for displaying text
     */
    var textColor: Int
        get() = textSettings.textColor
        set(textColor) {
            textSettings.textColor = textColor
            initTextPaint()
            invalidate()
        }

    /**
     * Value for size of displaying text
     */
    var textSize: Float
        get() = textSettings.textSize
        set(textSize) {
            textSettings.textSize = textSize
            textSettings.measureText(textPaint)
            selectorSettings.measure(textSize)
            initTextPaint()
            invalidate()
        }

    /**
     * Id to the custom font for text
     *
     * Note than if id will be incorrect, you will receive [RuntimeException]
     */
    var fontId: Int
        get() = textSettings.fontId
        set(fontId) {
            textSettings.fontId = fontId
            initTextPaint()
            invalidate()
        }

    /**
     * Elevation in percent
     *
     * Shadow will be shown only if [textShadowColor] will be set
     */
    var textElevationPercent: Int
        get() = shadowSettings.elevationPercent
        set(elevationPercent) {
            shadowSettings.elevationPercent = elevationPercent
            invalidate()
        }

    /**
     * Color of the shadow of the text
     */
    var textShadowColor: Int
        get() = shadowSettings.shadowColor
        set(shadowColor) {
            shadowSettings.shadowColor = shadowColor
            invalidate()
        }

    /**
     * Radius for shadow blur
     */
    var textShadowBlurRadius: Float
        get() = shadowSettings.shadowBlurRadius
        set(radius) {
            shadowSettings.shadowBlurRadius = radius
            invalidate()
        }

    /**
     * Use the method to subscribe to receive a callback when a touch event occurs
     *
     * @param touchEventCallback  The interface for listener to the touch event
     */
    fun subscribeTouchEventCallback(touchEventCallback: TouchEventCallback) {
        touchCallback.add(touchEventCallback)
    }

    /**
     * Use the method to subscribe to receive a callback when a touch event occurs
     *
     * @param changeViewTextCallback  The interface for listener to the change view text mode
     */
    fun subscribeChangeViewTextCallback(changeViewTextCallback: ChangeViewTextCallback) {
        changeViewTextModeCallback.add(changeViewTextCallback)
    }

    /**
     * Use the method to cancel the subscription of receiving a callback when a touch event occurs
     *
     * @param touchEventCallback  The interface for listener to the touch event
     */
    fun unsubscribeTouchEventCallback(touchEventCallback: TouchEventCallback) {
        touchCallback.remove(touchEventCallback)
    }

    /**
     * Use the method to cancel the subscription of receiving a callback when a change view text mode occurs
     *
     * @param changeViewTextCallback  The interface for listener to the change view text mode
     */
    fun unsubscribeChangeViewTextCallback(changeViewTextCallback: ChangeViewTextCallback) {
        changeViewTextModeCallback.remove(changeViewTextCallback)
    }

    /**
     * Use the method to set [PathEffect] to the selector paint
     * @see PathEffect
     */
    fun setPathEffectForSelector(effect: PathEffect) = with(selectorPaint) {
        pathEffect = effect
        invalidate()
    }

    /**
     * Use the method to set [Color] to the selector rectangle
     * @see Color
     */
    fun setColorForSelector(@ColorInt intColor: Int) = with(selectorPaint) {
        color = intColor
        invalidate()
    }

    /**
     * Use the method to set [Color] to the dash paint
     * @see Color
     */
    fun setColorForDashLine(@ColorInt intColor: Int) = with(dashPaint) {
        color = intColor
        invalidate()
    }

    /**
     * Use the method to set [Color] to control buttons
     * @see Color
     */
    fun setColorForSelectorButton(@ColorInt intColor: Int) = with(buttonsPaint) {
        color = intColor
        invalidate()
    }

    /**
     * Use the method to set [Color] to shadow
     * @see Color
     */
    fun setColorForTextShadow(@ColorInt intColor: Int) = with(shadowSettings) {
        shadowColor = intColor
        invalidate()
    }

    /**
     * Use the method to set the width for stroking
     *
     * @param width set the paint's stroke width, used whenever the paint's
     *              style is Stroke or StrokeAndFill.
     */
    fun setStrokeWidthForSelector(width: Float) = with(selectorPaint) {
        strokeWidth = width
        invalidate()
    }

    /**
     * Use the method to set the width for stroking
     *
     * @param width set the paint's stroke width, used whenever the paint's
     *              style is Stroke or StrokeAndFill.
     */
    fun setStrokeWidthForDashLine(width: Float) = with(dashPaint) {
        strokeWidth = width
        invalidate()
    }

    /**
     *
     *
     * @param resId  The resource id for the button icon
     */
    fun setBitmapChangeViewTextButton(@DrawableRes resId: Int) {
        changeViewTextBitmap = initBitmaps(resId)
        invalidate()
    }

    /**
     * Use the method to set the icon for the scale button
     *
     * @param resId  The resource id for the button icon
     */
    fun setBitmapScaleButton(@DrawableRes resId: Int) {
        scaleBitmap = initBitmaps(resId)
        invalidate()
    }

    /**
     * Use the method to set the icon for the reset button
     *
     * @param resId  The resource id for the button icon
     */
    fun setBitmapResetChangeViewTextButton(@DrawableRes resId: Int) {
        resetChangeViewTextBitmap = initBitmaps(resId)
        invalidate()
    }

    /**
     * Use the method to obtain current [ChangeText] mod
     */
    fun getChangeViewTextMode() = when (currentAction) {
        TouchActions.NO_ACTION -> ChangeText.OFF_CHANGE_VIEW_TEXT
        TouchActions.CHANGE_VIEW_TEXT -> ChangeText.ON_CHANGE_VIEW_TEXT
        else -> ChangeText.OFF_CHANGE_VIEW_TEXT
    }

    /**
     * Use the method for set the [ShowButtonOnSelector] mode for the button
     *
     * @param showButtonOnSelector  The enum for show a button on the selector
     */
    fun showScaleRotateButton(showButtonOnSelector: ShowButtonOnSelector) {
        isShowScaleRotateButton = showButtonOnSelector
        invalidate()
    }

    /**
     * Use the method for set the [ShowButtonOnSelector] mode for the button
     *
     * @param showButtonOnSelector  The enum for show a button on the selector
     */
    fun showChangeViewTextButton(showButtonOnSelector: ShowButtonOnSelector) {
        isShowChangeViewTextButton = showButtonOnSelector
        invalidate()
    }

    /**
     * Use the method for set the [ShowButtonOnSelector] mode for the button
     *
     * @param showButtonOnSelector  The enum for show a button on the selector
     */
    fun showResetViewTextButton(showButtonOnSelector: ShowButtonOnSelector) {
        isShowResetViewTextButton = showButtonOnSelector
        invalidate()
    }

    /**
     * Use the method for set the [ChangeText] mode for text
     *
     * @param changeText  The enum with different types of actions for interacting with text
     */
    fun changeViewTextMode(changeText: ChangeText) {
        previousAction = when (changeText) {
            ChangeText.ON_CHANGE_VIEW_TEXT -> TouchActions.NO_ACTION
            ChangeText.OFF_CHANGE_VIEW_TEXT -> TouchActions.CHANGE_VIEW_TEXT
        }
        changeViewTextMode()
        computeOffset()
        invalidate()
    }

    /**
     * Use the method for set the initial value for the Bezier curve
     */
    fun resetViewText() {
        resetChangeViewText()
        invalidate()
        postInvalidate()
    }

    /**
     * Use the method to draw the text in the bitmap
     *@see Bitmap
     *
     * @param bitmap  The bitmap that will be drawn below the text
     */
    fun saveResult(bitmap: Bitmap): Bitmap {
        val tempBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val resultBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(tempBitmap)
        val resultCanvas = Canvas(resultBitmap)
        val leftOffset = xPos - bitmap.width / HALF_DELIMITER
        val topOffset = yPos - bitmap.height / HALF_DELIMITER

        tempCanvas.drawBitmap(bitmap, leftOffset, topOffset, Paint())
        drawText(tempCanvas)

        resultCanvas.drawBitmap(tempBitmap, -leftOffset, -topOffset, Paint())

        return resultBitmap
    }

    /**
     * Draw text on the canvas
     * @see Canvas
     */
    private fun drawText(canvas: Canvas) {
        val path = changeViewTextSettings.computePathBezierCurve()
        drawText(canvas, path)
    }

    /**
     * Switches the [ChangeText] mode for text
     */
    private fun changeViewTextMode() {
        previousAction = if (previousAction == TouchActions.CHANGE_VIEW_TEXT) TouchActions.NO_ACTION else TouchActions.CHANGE_VIEW_TEXT
        currentAction = TouchActions.CHANGE_VIEW_TEXT
        sendChangeViewTextCallback(previousAction)
    }

    /**
     * Set the initial value for the Bezier curve
     */
    private fun resetChangeViewText() {
        val translateX = translationSettings.translationX + offsetX
        val translateY = translationSettings.translationY + offsetY
        changeViewTextSettings.resetPoint(
                rotatedSelectorRectangle,
                textSettings.textMeasurement,
                translateX,
                translateY,
                textSettings.textSize,
                scaleSettings.diff)
    }

    /**
     * Send callback with a down touch
     */
    private fun sendTouchEventCallback(touchAction: TouchActions, isInSelectorPosition: Boolean) {
        touchCallback.forEach { it.touchEvent(getChangeTextMode(touchAction), getSelectorPosition(isInSelectorPosition)) }
    }

    /**
     * Send callback with a long touch
     */
    private fun sendLongPressCallback(touchAction: TouchActions, isInSelectorPosition: Boolean) {
        touchCallback.forEach { it.longTouchEvent(getChangeTextMode(touchAction), getSelectorPosition(isInSelectorPosition)) }
    }

    /**
     * Send callback with a change view text mode
     */
    private fun sendChangeViewTextCallback(touchAction: TouchActions) {
        changeViewTextModeCallback.forEach { it.changeViewTextMode(getChangeTextMode(touchAction)) }
    }

    private fun getChangeTextMode(touchAction: TouchActions) =
            if (touchAction == TouchActions.CHANGE_VIEW_TEXT) ChangeText.ON_CHANGE_VIEW_TEXT else ChangeText.OFF_CHANGE_VIEW_TEXT

    private fun getSelectorPosition(isInSelectorPosition: Boolean) =
            if (isInSelectorPosition) SelectorPosition.INSIDE_SELECTOR else SelectorPosition.OUTSIDE_SELECTOR

    /**
     * Return rotated value of x coordinate
     *
     * @param              x    Coordinate for rotation
     * @param              y    Coordinate for rotation
     * @param        centerX    Coordinate for center of rotation
     * @param        centerY    Coordinate for center of rotation
     * @param angleInRadians    Angle of the rotation in radians
     *
     * @return                  Rotated x coordinate
     */
    private fun rotateX(x: Float, y: Float, centerX: Float, centerY: Float, angleInRadians: Double) =
            (Math.cos(angleInRadians) * (x - centerX) - Math.sin(angleInRadians) * (y - centerY) + centerX).toFloat()

    /**
     * Return rotated value of y coordinate
     *
     * @param              x    Coordinate for rotation
     * @param              y    Coordinate for rotation
     * @param        centerX    Coordinate for center of rotation
     * @param        centerY    Coordinate for center of rotation
     * @param angleInRadians    Angle of the rotation in radians
     *
     * @return                  Rotated y coordinate
     */
    private fun rotateY(x: Float, y: Float, centerX: Float, centerY: Float, angleInRadians: Double) =
            (Math.sin(angleInRadians) * (x - centerX) + Math.cos(angleInRadians) * (y - centerY) + centerY).toFloat()

    private fun degreesToRadians(angleInDegrees: Float) = Math.toRadians(angleInDegrees.toDouble())

    private fun computeOffset() {
        offsetX += drawSelectorRectangle.centerX() - rotatedSelectorRectangle.centerX()
        offsetY += drawSelectorRectangle.centerY() - rotatedSelectorRectangle.centerY()

        initCenterPoint = false
    }

    /**
     * Class to save and restore the state of the class EditorView
     * in {@link android.view.View # onSaveInstanceState ()}.
     *
     */
    private class SavedState : BaseSavedState {

        companion object {
            @Suppress("unused")
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: Parcel): SavedState = SavedState(source)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }

        internal var scaleBitmap: Bitmap? = null
            private set
        internal var changeViewTextBitmap: Bitmap? = null
            private set
        internal var changeBackViewTextBitmap: Bitmap? = null
            private set
        internal var rotatedSelectorRectangle: RotatedRectangle? = null
            private set
        internal var currentAction: TouchActions? = null
            private set
        internal var previousAction: TouchActions? = null
            private set
        internal var translationSettings: TranslationSettings? = null
            private set
        internal var textSettings: TextSettings? = null
            private set
        internal var selectorSettings: SelectorSettings? = null
            private set
        internal var buttonSettings: ButtonSettings? = null
            private set
        internal var rotateSettings: RotateSettings? = null
            private set
        internal var changeViewTextSettings: ChangeViewTextSettings? = null
            private set
        internal var scaleSettings: ScaleSettings? = null
            private set
        internal var shadowSettings: ShadowSettings? = null
            private set
        internal var offsetCenterCanvas: PointF? = null
            private set

        constructor(superState: Parcelable?, editorView: EditorView) : super(superState) {
            scaleBitmap = editorView.scaleBitmap
            changeViewTextBitmap = editorView.changeViewTextBitmap
            changeBackViewTextBitmap = editorView.resetChangeViewTextBitmap
            rotatedSelectorRectangle = editorView.rotatedSelectorRectangle
            translationSettings = editorView.translationSettings
            textSettings = editorView.textSettings
            selectorSettings = editorView.selectorSettings
            buttonSettings = editorView.buttonSettings
            rotateSettings = editorView.rotateSettings
            changeViewTextSettings = editorView.changeViewTextSettings
            scaleSettings = editorView.scaleSettings
            shadowSettings = editorView.shadowSettings
            previousAction = editorView.previousAction
            currentAction = editorView.currentAction
            offsetCenterCanvas = editorView.offsetCenterCanvas
        }

        constructor(source: Parcel?) : super(source) {
            source?.apply {
                scaleBitmap = readParcelable(RectF::class.java.classLoader)
                changeViewTextBitmap = readParcelable(RectF::class.java.classLoader)
                changeBackViewTextBitmap = readParcelable(RectF::class.java.classLoader)
                rotatedSelectorRectangle = readParcelable(RectF::class.java.classLoader)
                translationSettings = readParcelable(TranslationSettings::class.java.classLoader)
                textSettings = readParcelable(TextSettings::class.java.classLoader)
                selectorSettings = readParcelable(SelectorSettings::class.java.classLoader)
                buttonSettings = readParcelable(ButtonSettings::class.java.classLoader)
                rotateSettings = readParcelable(RotateSettings::class.java.classLoader)
                changeViewTextSettings = readParcelable(ChangeViewTextSettings::class.java.classLoader)
                scaleSettings = readParcelable(ScaleSettings::class.java.classLoader)
                shadowSettings = readParcelable(ShadowSettings::class.java.classLoader)
                previousAction = TouchActions.valueOf(readString())
                currentAction = TouchActions.valueOf(readString())
                offsetCenterCanvas = readParcelable(PointF::class.java.classLoader)
            }
        }

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel?, flags: Int) {
            dest?.apply {
                writeParcelable(scaleBitmap, flags)
                writeParcelable(changeViewTextBitmap, flags)
                writeParcelable(changeBackViewTextBitmap, flags)
                writeParcelable(rotatedSelectorRectangle, flags)
                writeParcelable(translationSettings, flags)
                writeParcelable(textSettings, flags)
                writeParcelable(selectorSettings, flags)
                writeParcelable(buttonSettings, flags)
                writeParcelable(rotateSettings, flags)
                writeParcelable(changeViewTextSettings, flags)
                writeParcelable(scaleSettings, flags)
                writeParcelable(shadowSettings, flags)
                writeString(previousAction?.name)
                writeString(currentAction?.name)
                writeParcelable(offsetCenterCanvas, flags)
            }
        }
    }

    /**
     * Return Touch Handler for action mask
     * @param action - action mask, can be null
     * @return Handler for this mask or null if there isn't any handler for this action
     */
    private fun getTouchHandler(action: Int? = null) = when (action) {
        MotionEvent.ACTION_DOWN -> DownHandler()
        MotionEvent.ACTION_POINTER_DOWN -> PointerDownHandler()
        MotionEvent.ACTION_POINTER_UP -> PointerUpHandler()
        MotionEvent.ACTION_MOVE -> MoveHandler()
        else -> CancelOrUpHandler()
    }

    /**
     * Return Pair with action and [TouchHandler] for this action
     * @param action  - action mask
     * @return Instance of [Pair] with action and Handler for it
     */
    private fun getTouchHandlerPair(action: Int) = Pair(action, getTouchHandler(action))

    /**
     * Implementation of [TouchHandler] for [MotionEvent.ACTION_DOWN]
     */
    private inner class DownHandler : BaseTouchHandler() {

        override fun processHandling(event: MotionEvent): Boolean {
            if (isInScaleRotatePosition(event) && previousAction == TouchActions.NO_ACTION && isShowScaleRotateButton == ShowButtonOnSelector.SHOW_BUTTON) {
                currentAction = TouchActions.SCALE_ROTATE
                with(rotateSettings) {
                    setTranslation(translationSettings.translationX, translationSettings.translationY)
                    init(rotatedSelectorRectangle.centerX(), rotatedSelectorRectangle.centerY(), xTouch, yTouch)
                }
                with(scaleSettings) {
                    setTranslation(translationSettings.translationX, translationSettings.translationY)
                    init(rotatedSelectorRectangle.centerX(), rotatedSelectorRectangle.centerY(), xTouch, yTouch)
                }

            } else if (isInChangeViewTextPosition(event) && isShowChangeViewTextButton == ShowButtonOnSelector.SHOW_BUTTON) {
                changeViewTextMode()
            } else if (isInResetChangeViewTextPosition(event) && currentAction == TouchActions.CHANGE_VIEW_TEXT && isShowResetViewTextButton == ShowButtonOnSelector.SHOW_BUTTON) {
                resetChangeViewText()
            } else if (isInDragPosition(event) && previousAction == TouchActions.NO_ACTION) {
                currentAction = TouchActions.DRAG
                translationSettings.setStart(xTouch, yTouch)
                translationSettings.setEnd(xTouch, yTouch)
            } else if (changeViewTextSettings.isInFirstPointPosition(event.getX(FIRST_TOUCH), event.getY(FIRST_TOUCH), drawSelectorRectangle)
                    && currentAction == TouchActions.CHANGE_VIEW_TEXT) {
                changeViewTextSettings.firstPoint.isChangePoint = true
            } else if (changeViewTextSettings.isInSecondPointPosition(event.getX(FIRST_TOUCH), event.getY(FIRST_TOUCH), drawSelectorRectangle)
                    && currentAction == TouchActions.CHANGE_VIEW_TEXT) {
                changeViewTextSettings.secondPoint.isChangePoint = true
            } else if (changeViewTextSettings.isInThirdPointPosition(event.getX(FIRST_TOUCH), event.getY(FIRST_TOUCH), drawSelectorRectangle)
                    && currentAction == TouchActions.CHANGE_VIEW_TEXT) {
                changeViewTextSettings.thirdPoint.isChangePoint = true
            } else if (changeViewTextSettings.isInFourthPointPosition(event.getX(FIRST_TOUCH), event.getY(FIRST_TOUCH), drawSelectorRectangle)
                    && currentAction == TouchActions.CHANGE_VIEW_TEXT) {
                changeViewTextSettings.fourthPoint.isChangePoint = true
            }

            return handled
        }
    }

    /**
     * Implementation of [TouchHandler] for [MotionEvent.ACTION_POINTER_DOWN]
     */
    private inner class PointerDownHandler : BaseTouchHandler() {

        override fun processHandling(event: MotionEvent): Boolean {
            if (isInDragPosition(event)) {
                currentAction = TouchActions.MULTI_TOUCH
                changeViewTextSettings.reset()

                with(scaleSettings) {
                    inMultiTouch = true
                    initMultiTouch(xTouch, yTouch, event.getX(SECOND_TOUCH), event.getY(SECOND_TOUCH))
                }
                with(rotateSettings) {
                    inMultiTouch = true
                    setTranslation(translationSettings.translationX, translationSettings.translationY)
                    init(rotatedSelectorRectangle.centerX(), rotatedSelectorRectangle.centerY(), xTouch, yTouch)
                    initMultiTouch(xTouch, yTouch, event.getX(SECOND_TOUCH), event.getY(SECOND_TOUCH))
                }
            }
            return handled
        }
    }

    /**
     * Implementation of [TouchHandler] for [MotionEvent.ACTION_POINTER_UP]
     */
    private inner class PointerUpHandler : BaseTouchHandler() {

        override fun processHandling(event: MotionEvent): Boolean {
            with(scaleSettings) {
                inMultiTouch = false
            }
            with(rotateSettings) {
                store()
                reset()
                inMultiTouch = false
            }
            computeOffset()
            currentAction = if (previousAction != TouchActions.CHANGE_VIEW_TEXT) TouchActions.NO_ACTION else TouchActions.CHANGE_VIEW_TEXT
            return handled
        }
    }

    /**
     * Implementation of [TouchHandler] for [MotionEvent.ACTION_MOVE]
     */
    private inner class MoveHandler : BaseTouchHandler() {

        override fun processHandling(event: MotionEvent): Boolean {
            when (currentAction) {
                TouchActions.MULTI_TOUCH -> {
                    scaleSettings.setCurrentPoints(xTouch, yTouch, event.getX(SECOND_TOUCH), event.getY(SECOND_TOUCH))
                    rotateSettings.setCurrentPoints(xTouch, yTouch, event.getX(SECOND_TOUCH), event.getY(SECOND_TOUCH))
                }
                TouchActions.SCALE_ROTATE -> {
                    with(rotateSettings) {
                        setEndToStart()
                        setEnd(xTouch, yTouch)
                    }
                    with(scaleSettings) {
                        setEndToStart()
                        setEnd(xTouch, yTouch)
                    }

                }
                TouchActions.CHANGE_VIEW_TEXT -> when {
                    changeViewTextSettings.firstPoint.isChangePoint -> {
                        with(changeViewTextSettings) {
                            computeFirstPoint(rotateSettings.rotation(), xTouch, yTouch)
                            secondPoint.isMoveInPointPosition = false
                            thirdPoint.isMoveInPointPosition = false
                            fourthPoint.isMoveInPointPosition = false
                        }
                    }
                    changeViewTextSettings.secondPoint.isChangePoint -> {
                        with(changeViewTextSettings) {
                            computeSecondPoint(rotateSettings.rotation(), xTouch, yTouch)
                            firstPoint.isMoveInPointPosition = false
                            thirdPoint.isMoveInPointPosition = false
                            fourthPoint.isMoveInPointPosition = false
                        }
                    }
                    changeViewTextSettings.thirdPoint.isChangePoint -> {
                        with(changeViewTextSettings) {
                            computeThirdPoint(rotateSettings.rotation(), xTouch, yTouch)
                            firstPoint.isMoveInPointPosition = false
                            secondPoint.isMoveInPointPosition = false
                            fourthPoint.isMoveInPointPosition = false
                        }
                    }
                    changeViewTextSettings.fourthPoint.isChangePoint -> {
                        with(changeViewTextSettings) {
                            computeFourthPoint(rotateSettings.rotation(), xTouch, yTouch)
                            firstPoint.isMoveInPointPosition = false
                            secondPoint.isMoveInPointPosition = false
                            thirdPoint.isMoveInPointPosition = false
                        }
                    }
                }
                TouchActions.DRAG -> {
                    translationSettings.setEnd(xTouch, yTouch)
                    initCenterPoint = false
                }
                else -> handled = false
            }
            return handled
        }
    }

    /**
     * Implementation of [TouchHandler] for [MotionEvent.ACTION_UP] or [MotionEvent.ACTION_CANCEL]
     */
    private inner class CancelOrUpHandler : BaseTouchHandler() {

        override fun processHandling(event: MotionEvent): Boolean {
            when (currentAction) {
                TouchActions.SCALE_ROTATE -> {
                    with(rotateSettings) {
                        store()
                        reset()
                    }
                    with(scaleSettings) {
                        inMultiTouch = false
                        store()
                        reset()
                    }
                }
                TouchActions.DRAG -> with(translationSettings) {
                    storeTranslations()
                    reset()
                    computeOffset()
                }
                TouchActions.CHANGE_VIEW_TEXT -> {
                    computeOffset()
                    changeViewTextSettings.reset()
                }
                else -> handled = false
            }

            if (previousAction != TouchActions.CHANGE_VIEW_TEXT) {
                currentAction = TouchActions.NO_ACTION
            }

            return handled
        }

    }

    /**
     *  Implementation of [GestureDetector.SimpleOnGestureListener] for [MotionEvent.ACTION_DOWN] or [MotionEvent.ACTION_UP]
     */
    private inner class TouchListener : GestureDetector.SimpleOnGestureListener() {

        override fun onLongPress(event: MotionEvent) = sendLongPressCallback(previousAction, isInSelectorPosition(event))

        override fun onSingleTapUp(event: MotionEvent): Boolean {
            sendTouchEventCallback(previousAction, isInSelectorPosition(event))
            return true
        }

        private fun isInSelectorPosition(event: MotionEvent): Boolean {
            val considerChangeViewTextButton = (isShowChangeViewTextButton == ShowButtonOnSelector.SHOW_BUTTON &&
                    isInChangeViewTextPosition(event))
            val considerResetViewTextButton = (currentAction == TouchActions.CHANGE_VIEW_TEXT &&
                    isShowResetViewTextButton == ShowButtonOnSelector.SHOW_BUTTON &&
                    isInResetChangeViewTextPosition(event))
            val considerScaleRotateButton = (currentAction != TouchActions.CHANGE_VIEW_TEXT &&
                    isShowScaleRotateButton == ShowButtonOnSelector.SHOW_BUTTON &&
                    isInScaleRotatePosition(event))
            return isInDragPosition(event) || considerChangeViewTextButton || considerResetViewTextButton || considerScaleRotateButton
        }
    }
}
