package com.vojtkovszky.singleactivitynavigation

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.vojtkovszky.singleactivitynavigation.util.serializable

@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class BaseSingleActivity: AppCompatActivity() {

    companion object {
        private const val ARG_CUSTOM_ANIMATIONS = "BaseSingleActivity.ARG_CUSTOM_ANIMATIONS"
        private const val ARG_CLOSE_DIALOGS_WHILE_NAV = "BaseSingleActivity.ARG_CLOSE_DIALOGS_WHILE_NAV"
    }

    // region Public properties
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
    // endregion

    // region Abstract and Open methods
    /**
     * Provide an id of a container view where fragments will be added to.
     * It will be used as a container for all fragments, unless you want to utilize master/detail
     * view - in that case override [getRootFragmentContainerId] as well
     */
    @IdRes
    abstract fun getDefaultFragmentContainerId(): Int

    /**
     * An id of a container view where root fragments will be added to.
     * By default it's the same as [getDefaultFragmentContainerId], but override it if you want to
     * utilize master/detail view
     */
    @IdRes
    open fun getRootFragmentContainerId(): Int {
        return getDefaultFragmentContainerId()
    }

    /**
     * This function will be invoked whenever back stack count changes, indicating change
     * in navigation.
     * Note that [backStackCount] value 0 will mean we're at the root screen.
     */
    open fun onBackStackChanged(backStackCount: Int) {}
    // endregion

    // region Public methods
    /**
     * Dismisses [BaseSingleBottomSheetFragment] if showing
     * (initialized by calling [navigateToBottomSheet]).
     */
    fun dismissOpenBottomSheet() {
        dismissDialog(getCurrentBottomSheetFragment())
    }

    /**
     * Dismisses [BaseSingleDialogFragment] if showing
     * (initialized by calling [navigateToDialog]).
     */
    fun dismissOpenDialog() {
        dismissDialog(getCurrentDialogFragment())
    }

    /**
     * Represents a latest [BaseSingleFragment] added to the fragment manager, regardless of its container.
     */
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
     * Navigate one step back.
     */
    fun navigateBack() {
        @Suppress("DEPRECATION")
        onBackPressed()
    }

    /**
     * pop until the fragment with given [fragmentName] is found, or all the way back to root
     * if no match found
     */
    fun navigateBackTo(fragmentName: String) {
        handleCloseAllDialogsAndSheets()
        supportFragmentManager.let {
            for (i in it.backStackEntryCount - 1 downTo 0) {
                if (it.getBackStackEntryAt(i).name == fragmentName) {
                    return
                }

                // popping back stack not allowed after onSaveInstanceState, will cause IllegalStateException
                if (!it.isStateSaved) {
                    it.popBackStack()
                }
            }
        }
    }

    /**
     * Pop stack all the way back to root fragment.
     * Root fragment is the fragment used when called [navigateToRoot]
     */
    fun navigateBackToRoot() {
        navigateBackTo("")
    }

    /**
     * Navigate to a given fragment which will be added to the top of back stack.
     *
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
     * Open given fragment as root.
     * Root fragment is not added to back stack and will be the root of navigation. When pressing back,
     * it will be the last fragment before activity is destroyed. It's also the only fragment visible
     * after whole back stack is popped or [navigateBackToRoot] is called.
     *
     * @param fragment fragment to show as root
     * @param popStack pop whole stack above selected root fragment
     */
    fun navigateToRoot(fragment: BaseSingleFragment, popStack: Boolean = true) {
        handleCloseAllDialogsAndSheets()

        if (popStack) {
            navigateBackToRoot()
        }

        fragment.addFragmentTypeToBundle(FragmentType.ROOT)

        commitTransaction(fragment, false)
    }

    /**
     * Open a given [fragment] in a bottom sheet
     */
    fun navigateToBottomSheet(fragment: BaseSingleFragment) {
        dismissOpenBottomSheet()
        with(BaseSingleBottomSheetFragment()) {
            this.fragment = fragment.also { it.addFragmentTypeToBundle(FragmentType.BOTTOM_SHEET) }
            this.show(supportFragmentManager, fragment::class.simpleName)
        }
    }

    /**
     * Open a given [fragment] in a dialog
     *
     * @param anchorView if provided, it will try to anchor dialog position to it, either above or below it.
     * @param useFullWidth by default, dialog tends to be very narrow, setting this to true will make
     * container width match window width
     * @param dialogStyle see [DialogFragment.setStyle]
     * @param dialogTheme see [DialogFragment.setStyle]
     */
    fun navigateToDialog(fragment: BaseSingleFragment,
                         anchorView: View? = null,
                         useFullWidth: Boolean = true,
                         dialogStyle: Int = DialogFragment.STYLE_NORMAL,
                         dialogTheme: Int = 0) {
        dismissOpenDialog()
        with(BaseSingleDialogFragment.newInstance(anchorView, useFullWidth)) {
            setStyle(dialogStyle, dialogTheme)
            this.fragment = fragment.also { it.addFragmentTypeToBundle(FragmentType.DIALOG) }
            this.show(supportFragmentManager, fragment::class.simpleName)
        }
    }
    // endregion

    // region Fragment overrides
    // use onCreate to keep track of back stack changed listener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.addOnBackStackChangedListener {
            onBackStackChanged(supportFragmentManager.backStackEntryCount)
        }
    }

    // back press handling
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (getCurrentFragment()?.overridesBackPress == true) {
            return
        }

        // fix for bug in android 10 causing memory leak when about to exit
        // https://issuetracker.google.com/issues/139738913
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q &&
            isTaskRoot &&
            (supportFragmentManager.primaryNavigationFragment?.childFragmentManager
                ?.backStackEntryCount ?: 0) == 0 &&
            supportFragmentManager.backStackEntryCount == 0
        ) {
            finishAfterTransition()
            return
        }

        @Suppress("DEPRECATION")
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
        customAnimationSettings = savedInstanceState.serializable(ARG_CUSTOM_ANIMATIONS) ?: CustomAnimationSettings()
        closeDialogsAndSheetsWhileNavigating = savedInstanceState.getBoolean(ARG_CLOSE_DIALOGS_WHILE_NAV, true)
    }
    // endregion

    // region Private methods
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

            // back stack add always, except on root fragments
            if (addToBackStack) {
                addToBackStack(fragment::class.simpleName)
            }

            // replace
            val container =
                if (fragment.fragmentType == FragmentType.ROOT) getRootFragmentContainerId()
                else getDefaultFragmentContainerId()
            replace(container, fragment, fragment::class.simpleName)

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
            if (it.isResumed && !it.isStateSaved) {
                it.dismissAllowingStateLoss()
            }
        }
    }

    // close all dialogs and bottom sheets, but check for closeDialogsAndSheetsWhileNavigating
    private fun handleCloseAllDialogsAndSheets() {
        if (closeDialogsAndSheetsWhileNavigating) {
            dismissOpenBottomSheet()
            dismissOpenDialog()
        }
    }
    // endregion
}