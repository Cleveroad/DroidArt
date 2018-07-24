package com.cleveroad.droidart

/**
 * Enum with different types of actions for touch processing
 */
internal enum class TouchActions {

    /**
     * View doesn't need to process this action
     */
    NO_ACTION,

    /**
     * View must be dragged
     */
    DRAG,

    /**
     * View must be scaled and rotated
     */
    SCALE_ROTATE,

    /**
     * View must be scaled and rotated in multi touch mode
     */
    MULTI_TOUCH,

    /**
     * View must be change view text
     */
    CHANGE_VIEW_TEXT
}