package com.cleveroad.wordart

/**
 * Interface for listener to the change view text mode
 */
interface ChangeViewTextCallback {

    /**
     * Sends callback to the listeners that have subscribed
     *
     * @param changeText  The enum with different types of actions for interacting with text
     */
    fun changeViewTextMode(changeText: ChangeText)
}