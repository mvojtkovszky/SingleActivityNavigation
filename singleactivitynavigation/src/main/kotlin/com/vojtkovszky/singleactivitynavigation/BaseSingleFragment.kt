package com.vojtkovszky.singleactivitynavigation

import android.os.Bundle
import android.view.View
import androidx.annotation.AnimRes
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment

@Suppress("unused")
abstract class BaseSingleFragment: Fragment() {

    /**
     * Represents type of this fragment instance. See [FragmentType] for more info.
     */
    var fragmentType: FragmentType = FragmentType.INVALID
        internal set

    /**
     * Reference to activity holding this fragment
     */
    val baseSingleActivity: BaseSingleActivity
        get() = requireActivity() as BaseSingleActivity

    /**
     * In case fragment is always modal, this value will override it, so we don't have to
     * set it in [BaseSingleActivity.navigateTo] every time we navigate to it
     */
    open val isModal = false

    /**
     * Z translation of the fragment, allows fragments to overlap nicely when using animations.
     */
    internal var translationZ: Float = 0f

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
     * Shortcut to [BaseSingleActivity.navigateBackTo]
     */
    fun navigateBackTo(fragmentName: String) {
        baseSingleActivity.navigateBackTo(fragmentName)
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateBack]
     */
    fun navigateBack() {
        baseSingleActivity.navigateBack()
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateBackToRoot]
     */
    fun navigateBackToRoot() {
        baseSingleActivity.navigateBackToRoot()
    }

    /**
     * Shortcut to [BaseSingleActivity.selectRootFragment]
     */
    fun selectRootFragment(positionIndex: Int = 0, popStack: Boolean = true) {
        baseSingleActivity.selectRootFragment(positionIndex, popStack)
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateTo]
     */
    fun navigateTo(fragment: BaseSingleFragment, openAsModal: Boolean = false,
                   ignoreIfAlreadyInStack: Boolean = false) {
        baseSingleActivity.navigateTo(fragment, openAsModal, ignoreIfAlreadyInStack)
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
    fun openDialog(fragment: BaseSingleFragment, anchorView: View? = null, useFullWidth: Boolean = true) {
        baseSingleActivity.openDialog(fragment, anchorView, useFullWidth)
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