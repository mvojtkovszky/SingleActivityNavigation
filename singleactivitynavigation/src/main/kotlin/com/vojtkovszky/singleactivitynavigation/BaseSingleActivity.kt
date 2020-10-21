package com.vojtkovszky.singleactivitynavigation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment

@Suppress("unused")
abstract class BaseSingleActivity: AppCompatActivity() {

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
    @SuppressWarnings("WeakerAccess")
    var currentDialogFragment: BaseSingleDialogFragment? = null

    /**
     * Represents a currently opened bottom sheet fragment or null if bottom sheet is not opened.
     */
    @SuppressWarnings("WeakerAccess")
    var currentBottomSheetFragment: BaseSingleBottomSheetFragment? = null

    /**
     * Define a default custom animation settings for fragment transaction animations.
     * If you want fragment to behave differently, simply
     */
    @SuppressWarnings("WeakerAccess")
    val customAnimationSettings = CustomAnimationSettings()

    /**
     * used to hold references to root fragments retrieved from [getNewMainFragmentInstance], which
     * itself is invoked when [selectMainFragment] is called
     */
    private val rootFragments = mutableListOf<BaseSingleFragment?>()

    /**
     * Extending activity is required to define at least one main (root) Fragment,
     * which will not be added to the back stack and will be selected using [selectMainFragment] method.
     *
     * If you have only one such fragment, simply ignore [positionIndex] as it will always be 0.
     */
    abstract fun getNewMainFragmentInstance(positionIndex: Int): BaseSingleFragment?

    /**
     * This function will be invoked whenever back stack count changes, indicating change
     * in navigation.
     * Note that [backStackCount] value 0 will mean we're at the root screen.
     */
    open fun onBackStackChanged(backStackCount: Int) {}

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
    fun navigateBackToRoot(closeDialogsAndSheets: Boolean = true) {
        handleCloseAllDialogsAndSheets(closeDialogsAndSheets)
        navigateBackToFragment("")
    }

    /**
     * Will select [rootFragments] on [positionIndex].
     * Calling this while secondary fragment in in front will first pop whole stack.
     *
     * @param positionIndex corresponding with fragment supplied with [getNewMainFragmentInstance]
     * @param popStack pop whole stack beneath new main fragment
     * @param closeDialogsAndSheets if any dialogs and bottom sheets are open, close those before transaction.
     */
    fun selectMainFragment(positionIndex: Int = 0,
                           popStack: Boolean = true,
                           closeDialogsAndSheets: Boolean = true) {
        handleCloseAllDialogsAndSheets(closeDialogsAndSheets)

        while (rootFragments.lastIndex < positionIndex) {
            rootFragments.add(null)
        }

        if (rootFragments[positionIndex] == null) {
            rootFragments[positionIndex] = getNewMainFragmentInstance(positionIndex)
        }

        rootFragments[positionIndex]?.let {
            if (popStack) {
                navigateBackToRoot()
            }
            commitTransaction(it, false)
        }
    }

    /**
     * Navigate to a fragment which will be added to the top of back stack as a secondary fragment
     * @param fragment fragment to navigate to
     * @param ignoreIfAlreadyInStack if same type of fragment already exists in back stack, transaction will be ignored.
     * @param closeDialogsAndSheets if any dialogs and bottom sheets are open, close those before transaction.
     */
    fun navigateTo(fragment: BaseSingleFragment,
                   ignoreIfAlreadyInStack: Boolean = false,
                   closeDialogsAndSheets: Boolean = true) {
        handleCloseAllDialogsAndSheets(closeDialogsAndSheets)

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
     * Open a given [fragment] in a bottom sheet
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
     * Open a given [fragment] in a dialog.
     * If [anchorView] is provided, it will try to anchor dialog position to it, either above or below it.
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

    // use onCreate to keep track of back stack changed listener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.addOnBackStackChangedListener {
            onBackStackChanged(supportFragmentManager.backStackEntryCount)
        }
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

    private fun handleCloseAllDialogsAndSheets(close: Boolean) {
        if (close) {
            closeCurrentlyOpenBottomSheet()
            closeCurrentlyOpenDialog()
        }
    }
}