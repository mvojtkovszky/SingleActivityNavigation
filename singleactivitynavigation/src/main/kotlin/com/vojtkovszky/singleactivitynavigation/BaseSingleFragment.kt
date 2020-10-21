package com.vojtkovszky.singleactivitynavigation

import android.os.Bundle
import android.view.View
import androidx.annotation.AnimRes
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment

@Suppress("unused")
abstract class BaseSingleFragment: Fragment() {
    /**
     * Determine if a fragment is a modal.
     * That usually effects the animation behaviour.
     */
    open var isModal: Boolean = false

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
    val baseSingleActivity: BaseSingleActivity
        get() = requireActivity() as BaseSingleActivity

    // region animations
    /**
     * Default enter animation. If none defined, [BaseSingleActivity.customAnimationSettings] is used
     */
    @AnimRes
    open val animationEnter = 0

    /**
     * Default exit animation. If none defined, [BaseSingleActivity.customAnimationSettings] is used
     */
    @AnimRes
    open val animationExit = 0

    /**
     * Default pop enter animation. If none defined, [BaseSingleActivity.customAnimationSettings] is used
     */
    @AnimRes
    open val animationPopEnter = 0

    /**
     * Default pop exit animation. If none defined, [BaseSingleActivity.customAnimationSettings] is used
     */
    @AnimRes
    open val animationPopExit = 0

    /**
     * True if at least one animation resource is set
     */
    fun hasCustomAnimations(): Boolean =
            animationEnter != 0 || animationExit != 0 || animationPopEnter != 0 || animationPopExit != 0
    // endregion animations

    // region shortcuts to baseSingleActivity methods
    /**
     * Shortcut to [BaseSingleActivity.navigateBackToFragment]
     */
    fun navigateBackToFragment(fragmentName: String) {
        baseSingleActivity.navigateBackToFragment(fragmentName)
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateBackToFragment]
     */
    fun navigateBack(backIfBackStackEmpty: Boolean = true) {
        baseSingleActivity.navigateBack(backIfBackStackEmpty)
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateBackToRoot]
     */
    fun navigateBackToRoot(closeDialogsAndSheets: Boolean = true) {
        baseSingleActivity.navigateBackToRoot(closeDialogsAndSheets)
    }

    /**
     * Shortcut to [BaseSingleActivity.selectRootFragment]
     */
    fun selectRootFragment(positionIndex: Int = 0,
                           popStack: Boolean = true,
                           closeDialogsAndSheets: Boolean = true) {
        baseSingleActivity.selectRootFragment(positionIndex, popStack, closeDialogsAndSheets)
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateTo]
     */
    fun navigateTo(fragment: BaseSingleFragment,
                   ignoreIfAlreadyInStack: Boolean = false,
                   closeDialogsAndSheets: Boolean = true) {
        baseSingleActivity.navigateTo(fragment, ignoreIfAlreadyInStack, closeDialogsAndSheets)
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