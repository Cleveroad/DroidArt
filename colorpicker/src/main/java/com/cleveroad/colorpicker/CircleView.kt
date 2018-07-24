package com.cleveroad.colorpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CircleView : View {
    companion object {
        private const val DEFAULT_STYLE = 0
        private const val DEFAULT_STYLE_RES = 0
        private const val DEFAULT_COLOR_CIRCLE = Color.RED
        private const val DEFAULT_COLOR_BORDER = Color.WHITE
        private const val RADIUS_CIRCLE = .6f
        private const val BORDER_CIRCLE = .03f
        private const val BORDER_CIRCLE_ACTIVE = .05f
        private const val HALF_DELIMITER = 2F
    }

    private val paint = Paint()

    var colorLap = DEFAULT_COLOR_CIRCLE
    var colorBorderline = DEFAULT_COLOR_BORDER
    var selection = false

    constructor(context: Context) : super(context) {
        init(null, DEFAULT_STYLE)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, DEFAULT_STYLE)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) = with(context.obtainStyledAttributes(attrs, R.styleable.CircleView, defStyle, DEFAULT_STYLE_RES)) {
        colorLap = getColor(R.styleable.CircleView_word_art_color_circle, DEFAULT_COLOR_CIRCLE)
        colorBorderline = getColor(R.styleable.CircleView_word_art_color_border, DEFAULT_COLOR_BORDER)
        initPaint()
        recycle()
    }

    private fun initPaint() = with(paint) {
        flags = Paint.ANTI_ALIAS_FLAG
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        with(Math.min(widthMeasureSpec, heightMeasureSpec)) {
            setMeasuredDimension(this, this)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val heightF = height.toFloat()
        val widthF = width.toFloat()
        val circleRadius = (Math.min(heightF, widthF) * RADIUS_CIRCLE).half()
        val widthBorder = Math.min(heightF, widthF) * if (selection) BORDER_CIRCLE_ACTIVE else BORDER_CIRCLE

        canvas.drawColor(Color.TRANSPARENT)
        with(paint) {
            style = Paint.Style.FILL
            color = colorBorderline
            canvas.drawCircle(widthF.half(), heightF.half(), circleRadius, this)
            color = colorLap
            canvas.drawCircle(widthF.half(), heightF.half(), circleRadius - widthBorder, this)
        }
    }

    private fun Float.half() = this / HALF_DELIMITER
}