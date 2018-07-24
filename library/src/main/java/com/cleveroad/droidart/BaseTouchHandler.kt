package com.cleveroad.droidart

import android.view.MotionEvent

/**
 * Base implementation of [TouchHandler]
 */
abstract class BaseTouchHandler : TouchHandler {

    companion object {

        private const val START_POINTER_INDEX = 0
        private const val DEFAULT_ACTION = -1
        private const val DEFAULT_TOUCH = -1F
    }

    protected var handled = true

    private var action = DEFAULT_ACTION

    protected var xTouch = DEFAULT_TOUCH

    protected var yTouch = DEFAULT_TOUCH

    override fun handleTouch(event: MotionEvent): Boolean {
        initParams(event)
        return processHandling(event)

    }

    /**
     * Init fields by event
     * @param event Instance of [MotionEvent]
     */
    open fun initParams(event: MotionEvent) {
        handled = true
        action = event.actionMasked
        xTouch = event.getX(START_POINTER_INDEX)
        yTouch = event.getY(START_POINTER_INDEX)
    }

    /**
     * Process event handling
     * @param event to handle
     */
    abstract fun processHandling(event: MotionEvent): Boolean
}
