package com.vojtkovszky.singleactivitynavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A bottom sheet fragment acting as a container for our [BaseSingleFragment], created when
 * [BaseSingleActivity.openBottomSheet] is called.
 *
 * Only to be used internally as [BaseSingleActivity] will always know how to handle
 * child fragments by itself.
 */
internal class BaseSingleBottomSheetFragment: BottomSheetDialogFragment() {

    // Fragment is to be set before dialog is created and will be attached to dialog's
    // base view when the dialog is created. The reference will be removed immediately after.
    internal var fragment: BaseSingleFragment? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FrameLayout(requireActivity()).apply {
            id = R.id.bottomSheetFragment
            // Fragment transaction is not needed if restoring from instance state as fragment
            // will be recreated in container.
            if (savedInstanceState == null && fragment != null) {
                childFragmentManager
                        .beginTransaction()
                        .replace(R.id.bottomSheetFragment, fragment!!, fragment!!::class.simpleName)
                        .runOnCommit {
                            // At this point fragment reference is not needed anymore and it's
                            // important we remove it to avoid memory leaks.
                            fragment = null
                        }
                        .commitAllowingStateLoss()
            }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        require(fragment != null) {
            "Fragment needs to be set before calling show"
        }

        // only show if we can guarantee we won't get into IllegalStateException due to state loss commit
        if (!manager.isDestroyed && !manager.isStateSaved) {
            super.show(manager, tag)
        }
    }

    /**
     * Returns current child [BaseSingleFragment] attached to this fragment
     */
    internal fun getInnerFragment(): BaseSingleFragment? {
        return childFragmentManager.fragments.filterIsInstance<BaseSingleFragment>().lastOrNull()
    }
}