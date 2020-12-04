package com.vojtkovszky.singleactivitynavigation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment

@Suppress("unused")
abstract class BaseSingleActivity: AppCompatActivity() {

    companion object {
        private const val ARG_CUSTOM_ANIMATIONS = "BaseSingleActivity.ARG_CUSTOM_ANIMATIONS"
        private const val ARG_CLOSE_DIALOGS_WHILE_NAV = "BaseSingleActivity.ARG_CLOSE_DIALOGS_WHILE_NAV"
    }

    /**
     * An id of a container view where fragments fragments will be added to. It will be used as
     * a container for all fragments, unless you wan
     */
    abstract val defaultFragmentContainerId: Int

    /**
     * An id of a container view where root fragments will be added to. Can be same as
     * [defaultFragmentContainerId] unless you want to utilize master/detail view
     */
    open val rootFragmentContainerId: Int
        get() = defaultFragmentContainerId

    /**
     * A modifier allowing to set behaviour while navigating through fragments
     * Setting this to true (default) will mean that on every navigation change, any currently opened
     * dialogs and bottom sheet will be closed, while false will keep those open.
     * This value is retained through instance state restoration.
     */
    var closeDialogsAndSheetsWhileNavigating: Boolean = true

    /**
     * Define a default custom animation settings for fragment transaction animations.
     * If you want fragment to behave differently, simply override animation parameters from
     * [BaseSingleFragment].
     * This value is retained through instance state restoration.
     */
    var customAnimationSettings = CustomAnimationSettings()

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
     * Represents a latest [BaseSingleFragment] added to the fragment manager, regardless of its container.
     */
    @SuppressWarnings("WeakerAccess")
    fun getCurrentFragment() : BaseSingleFragment? {
        getCurrentBottomSheetFragment()?.let { bottomSheet ->
            bottomSheet.getInnerFragment()?.let { return@getCurrentFragment it }
        }
        getCurrentDialogFragment()?.let { dialogFragment ->
            dialogFragment.getInnerFragment()?.let { return@getCurrentFragment it }
        }
        return supportFragmentManager.fragments.filterIsInstance<BaseSingleFragment>().lastOrNull()
    }

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
                it.addFragmentTypeToBundle(FragmentType.ROOT)
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
     * @param openAsModal define if fragment should be [FragmentType.MODAL] instead of [FragmentType.DEFAULT].
     * This behaviour can also be defined by fragment itself by overriding [BaseSingleFragment.isModal].
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

        fragment.addFragmentTypeToBundle(
                if (openAsModal || fragment.isModal) FragmentType.MODAL
                else FragmentType.DEFAULT)

        commitTransaction(fragment, true)
    }

    /**
     * Open a given [fragment] in a bottom sheet
     */
    fun openBottomSheet(fragment: BaseSingleFragment) {
        closeCurrentlyOpenBottomSheet()
        with(BaseSingleBottomSheetFragment()) {
            this.fragment = fragment.also { it.addFragmentTypeToBundle(FragmentType.BOTTOM_SHEET) }
            this.show(supportFragmentManager, fragment::class.simpleName)
        }
    }

    /**
     * Open a given [fragment] in a dialog.
     * @param anchorView if provided, it will try to anchor dialog position to it, either above or below it.
     * @param useFullWidth by default, dialog tends to be very narrow, setting this to true will make
     * @param dialogStyle see [DialogFragment.setStyle]
     * @param dialogTheme see [DialogFragment.setStyle]
     * container width match window width
     */
    fun openDialog(fragment: BaseSingleFragment,
                   anchorView: View? = null,
                   useFullWidth: Boolean = true,
                   dialogStyle: Int = DialogFragment.STYLE_NORMAL,
                   dialogTheme: Int = 0) {
        closeCurrentlyOpenDialog()
        with(BaseSingleDialogFragment.newInstance(anchorView, useFullWidth)) {
            setStyle(dialogStyle, dialogTheme)
            this.fragment = fragment.also { it.addFragmentTypeToBundle(FragmentType.DIALOG) }
            this.show(supportFragmentManager, fragment::class.simpleName)
        }
    }

    /**
     * Closes (dismisses) [BaseSingleBottomSheetFragment] if open.
     */
    fun closeCurrentlyOpenBottomSheet() {
        dismissDialog(getCurrentBottomSheetFragment())
    }

    /**
     * Closes (dismisses) [BaseSingleDialogFragment] if open.
     */
    fun closeCurrentlyOpenDialog() {
        dismissDialog(getCurrentDialogFragment())
    }

    // use onCreate to keep track of back stack changed listener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.addOnBackStackChangedListener {
            onBackStackChanged(supportFragmentManager.backStackEntryCount)
        }
    }

    // back press handling
    override fun onBackPressed() {
        if (getCurrentFragment()?.overridesBackPress == true) {
            return
        }
        super.onBackPressed()
    }

    // handle storing reusable data to saved instance state
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ARG_CUSTOM_ANIMATIONS, customAnimationSettings)
        outState.putBoolean(ARG_CLOSE_DIALOGS_WHILE_NAV, closeDialogsAndSheetsWhileNavigating)
    }

    // handle retrieving reusable data from saved instance state
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        customAnimationSettings = (savedInstanceState.getSerializable(ARG_CUSTOM_ANIMATIONS)
                ?: CustomAnimationSettings()) as CustomAnimationSettings
        closeDialogsAndSheetsWhileNavigating = savedInstanceState.getBoolean(ARG_CLOSE_DIALOGS_WHILE_NAV, true)
    }

    // logic to commit transaction
    private fun commitTransaction(fragment: BaseSingleFragment,
                                  addToBackStack: Boolean) {
        // if fragment will be added to back stack, it need to get in front of the fragment replacing
        // it in order for animations to play nicely
        if (addToBackStack) {
            fragment.addTranslationZToBundle(supportFragmentManager.backStackEntryCount + 1f)
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

            // replace
            replace(if (fragment.fragmentType == FragmentType.ROOT) rootFragmentContainerId else defaultFragmentContainerId,
                fragment, fragment::class.simpleName)

            // and finally commit
            commitAllowingStateLoss()
        }
    }

    // Represents currently opened dialog fragment, or null if not opened
    private fun getCurrentDialogFragment(): BaseSingleDialogFragment? =
            supportFragmentManager.fragments.filterIsInstance<BaseSingleDialogFragment>().lastOrNull()

    // Represents currently opened bottom sheet fragment, or null if not opened
    private fun getCurrentBottomSheetFragment(): BaseSingleBottomSheetFragment? =
            supportFragmentManager.fragments.filterIsInstance<BaseSingleBottomSheetFragment>().lastOrNull()

    // logic to dismiss a dialog fragment
    private fun dismissDialog(dialogFragment: AppCompatDialogFragment?) {
        dialogFragment?.let {
            if (it.isResumed) {
                it.dismissAllowingStateLoss()
            }
        }
    }

    // close all dialogs and bottom sheets, but check for closeDialogsAndSheetsWhileNavigating
    private fun handleCloseAllDialogsAndSheets() {
        if (closeDialogsAndSheetsWhileNavigating) {
            closeCurrentlyOpenBottomSheet()
            closeCurrentlyOpenDialog()
        }
    }
}