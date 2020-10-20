package com.vojtkovszky.singleactivitynavigation

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment

@Suppress("unused")
abstract class BaseSingleActivity: AppCompatActivity() {

    /**
     * Main fragments that are added to container but are not part of a back stack.
     * At least one is required,
     */
    abstract val rootFragments: List<BaseSingleFragment>

    /**
     * An id of a container view where all main and secondary fragments will be added to.
     */
    abstract val fragmentContainerId: Int

    /**
     * Represents a secondary fragment currently of the top of back stack, or null if back stack is empty.
     */
    val currentSecondaryFragment: BaseSingleFragment?
        get() = supportFragmentManager.fragments.filterIsInstance<BaseSingleFragment>().lastOrNull()

    /**
     * Represents a currently opened dialog fragment or null if dialog is not opened.
     */
    var currentDialogFragment: BaseSingleDialogFragment? = null

    /**
     * Represents a currently opened bottom sheet fragment or null if bottom sheet is not opened.
     */
    var currentBottomSheetFragment: BaseSingleBottomSheetFragment? = null

    /**
     * Define a default custom animation settings for fragment transaction animations.
     * If you want fragment to behave differently, simply
     */
    @SuppressWarnings("WeakerAccess")
    val customAnimationSettings = CustomAnimationSettings()

    /**
     * pop until the fragment with given [fragmentName] is found, or all the way back to root
      */
    fun navigateBackToFragment(fragmentName: String) {
        supportFragmentManager.let {
            for (i in it.backStackEntryCount - 1 downTo 0) {
                if (supportFragmentManager.getBackStackEntryAt(i).name == fragmentName) {
                    return
                }
                it.popBackStack()
            }
        }
    }

    /**
     * Pop all the way back to main fragment
     */
    @SuppressWarnings("WeakerAccess")
    fun navigateBackToRoot() {
        supportFragmentManager.let {
            for (i in it.backStackEntryCount - 1 downTo 0) {
                it.popBackStack()
            }
        }
    }

    /**
     * Will select [rootFragments] on [positionIndex].
     * Calling this while secondary fragment in in front will first pop whole stack.
     */
    fun selectMainFragment(positionIndex: Int) {
        if (positionIndex <= rootFragments.lastIndex) {
            navigateBackToRoot()
            commitTransaction(rootFragments[positionIndex], false)
        }
    }

    /**
     * Navigate to a fragment which will be added to the top of back stack as a secondary fragment
     * @ignore
     */
    fun navigateTo(fragment: BaseSingleFragment, ignoreIfAlreadyInStack: Boolean = false) {
        if (ignoreIfAlreadyInStack) {
            for (fragmentInStack in supportFragmentManager.fragments) {
                if (fragment::class == fragmentInStack::class) {
                    return
                }
            }
        }
        commitTransaction(fragment, true)
    }

    /**
     * Open a given fragment as a bottom sheet
     */
    fun openBottomSheet(fragment: BaseSingleFragment) {
        closeCurrentlyOpenBottomSheet()
        with(BaseSingleBottomSheetFragment()) {
            currentBottomSheetFragment = this
            this.fragment = fragment
            this.fragment.isInBottomSheet = true
            this.show(supportFragmentManager, fragment::class.simpleName)
        }
    }

    /**
     * Open a given fragment as a dialog.
     * If [anchorView] is provided, it will try to anchor dialog position to it.
     */
    fun openDialog(fragment: BaseSingleFragment, anchorView: View? = null) {
        closeCurrentlyOpenDialog()
        with(BaseSingleDialogFragment.newInstance(anchorView)) {
            currentDialogFragment = this
            this.fragment = fragment
            this.fragment.isInDialog = true
            this.show(supportFragmentManager, fragment::class.simpleName)
        }
    }

    /**
     * Closes (dismisses) [currentBottomSheetFragment] if open.
     * [currentBottomSheetFragment] will be null after this.
     */
    @SuppressWarnings("WeakerAccess")
    fun closeCurrentlyOpenBottomSheet() {
        dismissDialog(currentBottomSheetFragment)
        currentBottomSheetFragment = null
    }

    /**
     * Closes (dismisses) [currentDialogFragment] if open.
     * [currentDialogFragment] will be null after this.
     */
    @SuppressWarnings("WeakerAccess")
    fun closeCurrentlyOpenDialog() {
        dismissDialog(currentDialogFragment)
        currentDialogFragment = null
    }

    // logic to dismiss a dialog fragment
    private fun dismissDialog(dialogFragment: AppCompatDialogFragment?) {
        dialogFragment?.let {
            if (it.isResumed) {
                it.dismissAllowingStateLoss()
            }
        }
    }

    // logic to commit transaction
    private fun commitTransaction(fragment: BaseSingleFragment,
                                  addToBackStack: Boolean) {
        // if fragment will be added to back stack, it need to get in front of the fragment replacing
        // it in order for animations to play nicely
        if (addToBackStack) {
            fragment.translationZ = supportFragmentManager.backStackEntryCount + 1f
        }

        // setup transaction
        supportFragmentManager.beginTransaction().apply {
            // handle animations. Check if fragment has own defined
            if (fragment.hasCustomAnimations()) {
                setCustomAnimations(
                    fragment.animationEnter, fragment.animationExit,
                    fragment.animationPopEnter, fragment.animationPopExit)
            }
            // or use global settings otherwise
            else {
                when {
                    // root fragment
                    !addToBackStack -> setCustomAnimations(
                        customAnimationSettings.animationRootEnter, customAnimationSettings.animationRootExit,
                        customAnimationSettings.animationRootPopEnter, customAnimationSettings.animationRootPopExit)
                    // modal
                    fragment.isModal -> setCustomAnimations(
                        customAnimationSettings.animationModalEnter, customAnimationSettings.animationModalExit,
                        customAnimationSettings.animationModalPopEnter, customAnimationSettings.animationModalPopExit)
                    // default secondary
                    else -> setCustomAnimations(
                        customAnimationSettings.animationSecondaryEnter, customAnimationSettings.animationSecondaryExit,
                        customAnimationSettings.animationSecondaryPopEnter, customAnimationSettings.animationSecondaryPopExit)
                }
            }

            // back stack add always, except on root views (when switching bottom tabs)
            if (addToBackStack) {
                addToBackStack(fragment::class.simpleName)
            }
            // replace and commit
            replace(fragmentContainerId, fragment, fragment::class.simpleName)
            commitAllowingStateLoss()
        }
    }
}