package com.cleveroad.wordart

import android.view.MotionEvent

/**
 * Interface for handling touch event
 */
interface TouchHandler {

    /**
     * Handle [MotionEvent]
     * @param event Instance of [MotionEvent] to handle
     * @return true if event was handled
     */
    fun handleTouch(event: MotionEvent): Boolean
}