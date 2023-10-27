package com.vojtkovszky.singleactivitynavigation

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.AnimRes
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

@Suppress("unused")
abstract class BaseSingleFragment: Fragment() {

    companion object {
        private const val ARG_FRAGMENT_TYPE_NAME = "BaseSingleFragment.ARG_FRAGMENT_TYPE_NAME"
        private const val ARG_TRANSLATION_Z = "BaseSingleFragment.ARG_TRANSLATION_Z"
    }

    /**
     * Represents type of this fragment instance. See [FragmentType] for more info.
     */
    var fragmentType: FragmentType = FragmentType.INVALID
        private set

    /**
     * Reference to activity holding this fragment
     */
    val baseSingleActivity: BaseSingleActivity?
        get() = activity as BaseSingleActivity?

    /**
     * In case fragment is always modal, this value will allow to override parameter set
     * in [BaseSingleActivity.navigateTo], so we don't have to set it every time we navigate to it.
     */
    open val isModal: Boolean
        get() = fragmentType == FragmentType.MODAL

    /**
     * If set to true, any navigation call from this fragment will check for fragment validity
     * first; meaning fragment has to be added, its activity not null, not destroyed, not finishing.
     * This ensures navigation methods are never invoked outside of fragment lifecycle.
     * false is default.
     */
    open val mustBeValidToInvokeNavigation: Boolean
        get() = false

    /**
     * Z translation of the fragment, allows fragments to overlap nicely when using animations.
     */
    private var translationZ: Float = 0f

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
     * Shortcut to [BaseSingleActivity.dismissOpenBottomSheet]
     */
    fun dismissOpenBottomSheet() {
        if (canProceedWithNavigation()) {
            baseSingleActivity?.dismissOpenBottomSheet()
        }
    }

    /**
     * Shortcut to [BaseSingleActivity.dismissOpenDialog]
     */
    fun dismissOpenDialog() {
        if (canProceedWithNavigation()) {
            baseSingleActivity?.dismissOpenDialog()
        }
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateBackTo]
     */
    fun navigateBackTo(fragmentName: String) {
        if (canProceedWithNavigation()) {
            baseSingleActivity?.navigateBackTo(fragmentName)
        }
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateBack]
     */
    fun navigateBack() {
        if (canProceedWithNavigation()) {
            baseSingleActivity?.navigateBack()
        }
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateBackToRoot]
     */
    fun navigateBackToRoot() {
        if (canProceedWithNavigation()) {
            baseSingleActivity?.navigateBackToRoot()
        }
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateTo]
     */
    fun navigateTo(fragment: BaseSingleFragment, openAsModal: Boolean = false,
                   ignoreIfAlreadyInStack: Boolean = false) {
        if (canProceedWithNavigation()) {
            baseSingleActivity?.navigateTo(fragment, openAsModal, ignoreIfAlreadyInStack)
        }
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateToBottomSheet]
     */
    fun navigateToBottomSheet(fragment: BaseSingleFragment) {
        if (canProceedWithNavigation()) {
            baseSingleActivity?.navigateToBottomSheet(fragment)
        }
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateToDialog]
     */
    fun navigateToDialog(fragment: BaseSingleFragment, anchorView: View? = null, useFullWidth: Boolean = true,
                         dialogStyle: Int = DialogFragment.STYLE_NORMAL, dialogTheme: Int = 0) {
        if (canProceedWithNavigation()) {
            baseSingleActivity?.navigateToDialog(fragment, anchorView, useFullWidth, dialogStyle, dialogTheme)
        }
    }

    /**
     * Shortcut to [BaseSingleActivity.navigateToRoot]
     */
    fun navigateToRoot(fragment: BaseSingleFragment, popStack: Boolean = true) {
        if (canProceedWithNavigation()) {
            baseSingleActivity?.navigateToRoot(fragment, popStack)
        }
    }
    // endregion shortcuts to baseSingleActivity methods

    // region add bundle
    /**
     * Add [FragmentType] to [Bundle] so value is kept in case underlying activity gets recreated.
     */
    internal fun addFragmentTypeToBundle(fragmentType: FragmentType) {
        // add it immediately so we can have access to it even before fragment is created
        this.fragmentType = fragmentType
        arguments = (arguments ?: Bundle()).apply {
            putString(ARG_FRAGMENT_TYPE_NAME, fragmentType.name)
        }
    }
    /**
     * Add [translationZ] to [Bundle] so value is kept in case underlying activity gets recreated.
     */
    internal fun addTranslationZToBundle(translationZ: Float) {
        arguments = (arguments ?: Bundle()).apply {
            putFloat(ARG_TRANSLATION_Z, translationZ)
        }
    }
    // endregion add bundle

    // region private methods
    private fun canProceedWithNavigation(): Boolean {
        return if (mustBeValidToInvokeNavigation) isValid() else true
    }

    private fun isValid(): Boolean {
        return baseSingleActivity != null && !baseSingleActivity!!.isDestroyed &&
                !baseSingleActivity!!.isFinishing && isAdded
    }
    // endregion private methods

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setTranslationZ(view, translationZ)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentType = FragmentType.values().firstOrNull {
            it.name == arguments?.getString(ARG_FRAGMENT_TYPE_NAME) } ?: FragmentType.INVALID
        translationZ = arguments?.getFloat(ARG_TRANSLATION_Z) ?: 0f

        // handle back press
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                this@BaseSingleFragment.handleOnBackPressed(this)
            }
        })
    }

    /**
     * This method is called on every back press. Override it if you want to handle back presses manually.
     * @param callback from a back pressed dispatcher for this fragment
     */
    open fun handleOnBackPressed(callback: OnBackPressedCallback) {
        // fix for bug in android 10 causing memory leak when about to exit
        // https://issuetracker.google.com/issues/139738913
        baseSingleActivity?.let {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q &&
                it.isTaskRoot &&
                (it.supportFragmentManager.primaryNavigationFragment?.childFragmentManager
                    ?.backStackEntryCount ?: 0) == 0 &&
                it.supportFragmentManager.backStackEntryCount == 0
            ) {
                it.finishAfterTransition()
                return@handleOnBackPressed
            }
        }

        // disable dispatcher callback, perform default back press and enable it again
        callback.isEnabled = false
        baseSingleActivity?.onBackPressedDispatcher?.onBackPressed()
        callback.isEnabled = true
    }
}