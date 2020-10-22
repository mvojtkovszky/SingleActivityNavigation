package com.vojtkovszky.singleactivitynavigation

/**
 * Every instance of [BaseSingleFragment] can be of exactly one of these types.
 */
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
    BOTTOM_SHEET
}