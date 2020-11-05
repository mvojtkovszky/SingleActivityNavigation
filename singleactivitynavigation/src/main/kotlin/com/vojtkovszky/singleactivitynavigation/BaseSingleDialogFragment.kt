package com.vojtkovszky.singleactivitynavigation

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager

/**
 * A dialog fragment acting as a container for our [BaseSingleFragment], created when
 * [BaseSingleActivity.openDialog] is called.
 *
 * Only to be used internally as [BaseSingleActivity] will always know how to handle
 * child fragments by itself.
 */
internal class BaseSingleDialogFragment: AppCompatDialogFragment() {

    companion object {
        private const val ARG_ANCHOR_VIEW_HEIGHT = "BaseDialogFragment.ARG_ANCHOR_VIEW_HEIGHT"
        private const val ARG_ANCHOR_VIEW_X = "BaseDialogFragment.ARG_ANCHOR_VIEW_X"
        private const val ARG_ANCHOR_VIEW_Y = "BaseDialogFragment.ARG_ANCHOR_VIEW_Y"
        private const val ARG_USE_FULL_WIDTH = "BaseDialogFragment.ARG_USE_FULL_WIDTH"

        fun newInstance(anchorView: View? = null, useFullWidth: Boolean = true): BaseSingleDialogFragment {
            return BaseSingleDialogFragment().apply {
                if (anchorView != null) {
                    arguments = Bundle().apply {
                        putInt(ARG_ANCHOR_VIEW_HEIGHT, anchorView.height)
                        putFloat(ARG_ANCHOR_VIEW_X, anchorView.x)
                        putFloat(ARG_ANCHOR_VIEW_Y, anchorView.y)
                        putBoolean(ARG_USE_FULL_WIDTH, useFullWidth)
                    }
                }
            }
        }
    }

    // fragment is to be set before dialog is created.
    // It will be attached to dialog's base view when the dialog is created.
    // Don't rely on this instance as it will be lost when/if underlying activity is recreated.
    // To retrieve fragment once attached, use getInnerFragment() instead
    internal lateinit var fragment: BaseSingleFragment

    private var anchorHeight: Int = 0
    private var anchorX: Float = 0f
    private var anchorY: Float = 0f
    private var useFullWidth: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            anchorHeight = it.getInt(ARG_ANCHOR_VIEW_HEIGHT)
            anchorX = it.getFloat(ARG_ANCHOR_VIEW_X)
            anchorY = it.getFloat(ARG_ANCHOR_VIEW_Y)
            useFullWidth = it.getBoolean(ARG_USE_FULL_WIDTH)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FrameLayout(requireActivity()).apply {
            id = R.id.dialogFragment
            // fragment transaction is not needed if restoring from instance state as fragment
            // will be recreated in container.
            if (savedInstanceState == null && ::fragment.isInitialized) {
                childFragmentManager
                        .beginTransaction()
                        .replace(R.id.dialogFragment, fragment, fragment::class.simpleName)
                        .commitAllowingStateLoss()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // fix dialog to not appear thin
        if (useFullWidth) {
            dialog?.window?.let { window ->
                WindowManager.LayoutParams().let { layoutParams ->
                    layoutParams.copyFrom(window.attributes)
                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                    window.attributes = layoutParams
                }
            }
        }

        // handle anchoring
        if (anchorHeight > 0) {
            dialog?.window?.let {
                // if y is on the upper half of the screen, we'll anchor it above view
                val aboveAnchor = it.attributes.y < it.attributes.height
                it.setGravity(Gravity.TOP or Gravity.START)
                it.attributes.x = anchorX.toInt()
                it.attributes.y = anchorY.toInt() + (if (aboveAnchor) 0 - anchorHeight else anchorHeight)
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
            "Fragment or view needs to be set before calling show"
        }
        super.show(manager, tag)
    }

    /**
     * Returns current child fragment attached to this fragment
     */
    internal fun getInnerFragment(): BaseSingleFragment? {
        return childFragmentManager.fragments.filterIsInstance<BaseSingleFragment>().lastOrNull()
    }
}