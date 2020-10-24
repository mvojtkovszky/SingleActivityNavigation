package com.vojtkovszky.singleactivitynavigation

/**
 * Every instance of [BaseSingleFragment] can be of exactly one of these types.
 */
@Suppress("unused")
enum class FragmentType {
    /**
     * Root fragment is reserved for root screen. Usually the fragment(s) representing
     * base view or included in main navigation.
     */
    ROOT,

    /**
     * Default fragment, part of a regular stack, most common secondary fragment
     */
    DEFAULT,

    /**
     * Same as [DEFAULT], except we wanted fragment to get categorized as modal.
     */
    MODAL,

    /**
     * Fragment opened and contained in a dialog
     */
    DIALOG,

    /**
     * Fragment opened and contained in a bottom sheet
     */
    BOTTOM_SHEET,

    /**
     * Fragment was not committed using a transaction from [BaseSingleActivity].
     * Happens if we use [BaseSingleFragment] class on its own.
     */
    INVALID;

    // region helper methods
    /**
     * Determine if type is [DIALOG] or [BOTTOM_SHEET]
     */
    fun isDialogOrBottomSheet(): Boolean = this == DIALOG || this == BOTTOM_SHEET

    /**
     * Determine if type is [ROOT], [DEFAULT], or [MODAL]
     */
    fun isRegularFragment(): Boolean = this == ROOT || this == DEFAULT || this == MODAL
    // endregion helper methods
}