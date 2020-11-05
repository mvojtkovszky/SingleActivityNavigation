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
 */
internal class BaseSingleBottomSheetFragment: BottomSheetDialogFragment() {

    // fragment is to be set before dialog is created.
    // It will be attached to dialog's base view when the dialog is created.
    internal lateinit var fragment: BaseSingleFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FrameLayout(requireActivity()).apply {
            id = R.id.bottomSheetFragment
            // fragment transaction is not needed if restoring from instance state as fragment
            // will be recreated in container.
            if (savedInstanceState == null && ::fragment.isInitialized) {
                childFragmentManager
                        .beginTransaction()
                        .replace(R.id.bottomSheetFragment, fragment, fragment::class.simpleName)
                        .commitAllowingStateLoss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // remove fragment from view when dialog's view is getting destroyed
        if (::fragment.isInitialized) {
            childFragmentManager
                    .beginTransaction()
                    .remove(fragment)
                    .commitAllowingStateLoss()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        require(::fragment.isInitialized) {
            "Fragment needs to be set before calling show"
        }
        super.show(manager, tag)
    }
}