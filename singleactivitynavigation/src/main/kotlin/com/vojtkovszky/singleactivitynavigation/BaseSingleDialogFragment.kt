package com.vojtkovszky.singleactivitynavigation

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager

class BaseSingleDialogFragment: AppCompatDialogFragment() {

    companion object {
        private const val ARG_ANCHOR_VIEW_HEIGHT = "BaseDialogFragment.ARG_ANCHOR_VIEW_HEIGHT"
        private const val ARG_ANCHOR_VIEW_X = "BaseDialogFragment.ARG_ANCHOR_VIEW_X"
        private const val ARG_ANCHOR_VIEW_Y = "BaseDialogFragment.ARG_ANCHOR_VIEW_Y"

        fun newInstance(anchorView: View? = null): BaseSingleDialogFragment {
            return BaseSingleDialogFragment().apply {
                if (anchorView != null) {
                    arguments = Bundle().apply {
                        putInt(ARG_ANCHOR_VIEW_HEIGHT, anchorView.height)
                        putFloat(ARG_ANCHOR_VIEW_X, anchorView.x)
                        putFloat(ARG_ANCHOR_VIEW_Y, anchorView.y)
                    }
                }
            }
        }
    }

    lateinit var fragment: BaseSingleFragment

    private var anchorHeight: Int = 0
    private var anchorX: Float? = null
    private var anchorY: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            anchorHeight = it.getInt(ARG_ANCHOR_VIEW_HEIGHT)
            anchorX = it.getFloat(ARG_ANCHOR_VIEW_X)
            anchorY = it.getFloat(ARG_ANCHOR_VIEW_Y)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FrameLayout(requireActivity()).apply {
            id = R.id.dialogFragment
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            childFragmentManager
                    .beginTransaction()
                    .replace(R.id.dialogFragment, fragment, fragment::class.simpleName)
                    .commitAllowingStateLoss()

            if (anchorHeight > 0) {
                dialog?.window?.let {
                    // if y is on the upper half of the screen, we'll anchor it above view
                    val aboveAnchor = it.attributes.y < it.attributes.height
                    it.setGravity(Gravity.TOP or Gravity.START)
                    it.attributes.x = anchorX!!.toInt()
                    it.attributes.y = anchorY!!.toInt() + (if (aboveAnchor) 0 - anchorHeight else anchorHeight)
                }
            }
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
        require(::fragment.isInitialized) {
            "Fragment or view needs to be set before calling show"
        }
        super.show(manager, tag)
    }
}