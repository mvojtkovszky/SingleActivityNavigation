package com.vojtkovszky.singleactivitynavigation

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment

abstract class BaseSingleFragment: Fragment() {
    /**
     * Determine if a fragment is a modal.
     * That usually effects the animation behaviour.
     */
    open val isModal: Boolean = false

    /**
     * Indicates if this fragment is contained in a bottom sheet
     */
    var isInBottomSheet = false

    /**
     * Indicates if this fragment is contained in a dialog
     */
    var isInDialog = false

    /**
     * Z translation of the fragment, used internally.
     */
    var translationZ: Float = 0f

    /**
     * Reference to activity holding this fragment
     */
    @SuppressWarnings("WeakerAccess")
    val baseSingleActivity: BaseSingleActivity = requireActivity() as BaseSingleActivity

    // region shortcuts to baseSingleActivity methods
    /**
     * Shortcut to [BaseSingleActivity.navigateBackToFragment]
     */
    fun navigateBackToFragment(fragmentName: String) {
        baseSingleActivity.navigateBackToFragment(fragmentName)
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateBackToRoot]
     */
    fun navigateBackToRoot() {
        baseSingleActivity.navigateBackToRoot()
    }

    /**
     * Shortcut to [BaseSingleActivity.selectMainFragment]
     */
    fun selectMainFragment(positionIndex: Int) {
        baseSingleActivity.selectMainFragment(positionIndex)
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateTo]
     */
    fun navigateTo(fragment: BaseSingleFragment, ignoreIfAlreadyInStack: Boolean = false) {
        baseSingleActivity.navigateTo(fragment, ignoreIfAlreadyInStack)
    }

    /**
     * Shortcut to [BaseSingleActivity.openBottomSheet]
     */
    fun openBottomSheet(fragment: BaseSingleFragment) {
        baseSingleActivity.openBottomSheet(fragment)
    }

    /**
     * Shortcut to [BaseSingleActivity.openDialog]
     */
    fun openDialog(fragment: BaseSingleFragment, anchorView: View? = null) {
        baseSingleActivity.openDialog(fragment, anchorView)
    }

    /**
     * Shortcut to [BaseSingleActivity.closeCurrentlyOpenBottomSheet]
     */
    fun closeCurrentlyOpenBottomSheet() {
        baseSingleActivity.closeCurrentlyOpenBottomSheet()
    }

    /**
     * Shortcut to [BaseSingleActivity.closeCurrentlyOpenDialog]
     */
    fun closeCurrentlyOpenDialog() {
        baseSingleActivity.closeCurrentlyOpenDialog()
    }
    // endregion shortcuts to baseSingleActivity methods

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setTranslationZ(view, translationZ)
        super.onViewCreated(view, savedInstanceState)
    }
}