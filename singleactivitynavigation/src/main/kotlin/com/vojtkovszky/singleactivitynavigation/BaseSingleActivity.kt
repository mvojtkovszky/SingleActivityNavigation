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
     * A modifier allowing to set behaviour while navigating through fragments
     * Setting this to true (default) will mean that on every navigation change, any currently opened
     * dialogs and bottom sheet will be closed, while false will keep those open.
     */
    var closeDialogsAndSheetsWhileNavigating: Boolean = true

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
     * used to hold references to root fragments retrieved from [getNewRootFragmentInstance], which
     * itself is invoked when [selectRootFragment] is called
     */
    private val rootFragments = mutableListOf<BaseSingleFragment?>()

    /**
     * Extending activity is required to define at least one main (root) Fragment,
     * which will not be added to the back stack and will be selected using [selectRootFragment] method.
     *
     * If you have only one such fragment, simply ignore [positionIndex] as it will always be 0.
     */
    abstract fun getNewRootFragmentInstance(positionIndex: Int): BaseSingleFragment?

    /**
     * This function will be invoked whenever back stack count changes, indicating change
     * in navigation.
     * Note that [backStackCount] value 0 will mean we're at the root screen.
     */
    open fun onBackStackChanged(backStackCount: Int) {}

    /**
     * pop until the fragment with given [fragmentName] is found, or all the way back to root
     */
    fun navigateBackTo(fragmentName: String) {
        handleCloseAllDialogsAndSheets()
        supportFragmentManager.let {
            for (i in it.backStackEntryCount - 1 downTo 0) {
                if (it.getBackStackEntryAt(i).name == fragmentName) {
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
        navigateBackTo("")
    }

    /**
     * Navigate one step back, effectively triggering a back press.
     */
    fun navigateBack() {
        onBackPressed()
    }

    /**
     * Will select [rootFragments] on [positionIndex].
     * Calling this while secondary fragment in in front will first pop whole stack.
     *
     * @param positionIndex corresponding with fragment supplied with [getNewRootFragmentInstance]
     * @param popStack pop whole stack above selected main fragment
     */
    fun selectRootFragment(positionIndex: Int = 0, popStack: Boolean = true) {
        handleCloseAllDialogsAndSheets()

        while (rootFragments.lastIndex < positionIndex) {
            rootFragments.add(null)
        }

        if (rootFragments[positionIndex] == null) {
            rootFragments[positionIndex] = getNewRootFragmentInstance(positionIndex)?.also {
                it.fragmentType = FragmentType.ROOT
            }
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
     * @param openAsModal
     * @param ignoreIfAlreadyInStack if same type of fragment already exists in back stack, transaction will be ignored.
     */
    fun navigateTo(fragment: BaseSingleFragment,
                   openAsModal: Boolean = false,
                   ignoreIfAlreadyInStack: Boolean = false) {
        handleCloseAllDialogsAndSheets()

        if (ignoreIfAlreadyInStack) {
            for (fragmentInStack in supportFragmentManager.fragments) {
                if (fragment::class == fragmentInStack::class) {
                    return
                }
            }
        }

        fragment.fragmentType = if (openAsModal) FragmentType.MODAL else FragmentType.DEFAULT
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
            this.fragment.fragmentType = FragmentType.BOTTOM_SHEET
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
            this.fragment.fragmentType = FragmentType.DIALOG
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
                when (fragment.fragmentType) {
                    // root fragment
                    FragmentType.ROOT -> setCustomAnimations(
                        customAnimationSettings.animationRootEnter, customAnimationSettings.animationRootExit,
                        customAnimationSettings.animationRootPopEnter, customAnimationSettings.animationRootPopExit)
                    // modal
                    FragmentType.MODAL -> setCustomAnimations(
                        customAnimationSettings.animationModalEnter, customAnimationSettings.animationModalExit,
                        customAnimationSettings.animationModalPopEnter, customAnimationSettings.animationModalPopExit)
                    // default
                    else -> setCustomAnimations(
                        customAnimationSettings.animationDefaultEnter, customAnimationSettings.animationDefaultExit,
                        customAnimationSettings.animationDefaultPopEnter, customAnimationSettings.animationDefaultPopExit)
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

    private fun handleCloseAllDialogsAndSheets() {
        if (closeDialogsAndSheetsWhileNavigating) {
            closeCurrentlyOpenBottomSheet()
            closeCurrentlyOpenDialog()
        }
    }
}