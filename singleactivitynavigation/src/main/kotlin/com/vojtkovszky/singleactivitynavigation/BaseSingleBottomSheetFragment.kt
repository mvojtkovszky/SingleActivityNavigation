package com.vojtkovszky.singleactivitynavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BaseSingleBottomSheetFragment: BottomSheetDialogFragment() {

    lateinit var fragment: BaseSingleFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FrameLayout(requireActivity()).apply {
            id = R.id.bottomSheetFragment
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            childFragmentManager
                .beginTransaction()
                .replace(R.id.bottomSheetFragment, fragment, fragment::class.simpleName)
                .commitAllowingStateLoss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::fragment.isInitialized) {
            childFragmentManager
                    .beginTransaction()
                    .remove(fragment)
                    .commitAllowingStateLoss()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        check(::fragment.isInitialized) {
            "Fragment needs to be set before calling show"
        }
        super.show(manager, tag)
    }
}