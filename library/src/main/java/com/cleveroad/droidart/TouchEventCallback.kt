package com.cleveroad.droidart

/**
 * Interface for listener to the touch event
 */
interface TouchEventCallback {

    /**
     * Sends callback to the listeners that have subscribed
     *
     * @param       changeText  The enum with different types of actions for interacting with text
     * @param selectorPosition  The enum for indicating the position of the touch event
     */
    fun touchEvent(changeText: ChangeText, selectorPosition: SelectorPosition)

    /**
     * Sends callback to the listeners that have subscribed
     *
     * @param       changeText  The enum with different types of actions for interacting with text
     * @param selectorPosition  The enum for indicating the position of the touch event
     */
    fun longTouchEvent(changeText: ChangeText, selectorPosition: SelectorPosition)
}
