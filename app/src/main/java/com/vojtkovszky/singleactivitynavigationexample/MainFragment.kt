package com.vojtkovszky.singleactivitynavigationexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vojtkovszky.singleactivitynavigation.BaseSingleFragment
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseSingleFragment() {

    companion object {
        private const val PARAM_FRAGMENT_NAME = "MainFragment.PARAM_FRAGMENT_NAME"
        private const val PARAM_IS_ROOT = "MainFragment.PARAM_IS_MODAL"

        fun newInstance(title: String, isModal: Boolean = false, isRoot: Boolean = false) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(PARAM_FRAGMENT_NAME, title)
                    putBoolean(PARAM_IS_ROOT, isRoot)
                }
                this.isModal = isModal
            }
    }

    private var title: String? = null
    private var isRoot: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(PARAM_FRAGMENT_NAME)
            isRoot = it.getBoolean(PARAM_IS_ROOT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        super.onResume()

        if (!isInBottomSheet) {
            baseSingleActivity.supportActionBar?.title = title
        }

        buttonOpenRegular.setOnClickListener {
            navigateTo(newInstance("Regular"))
        }

        buttonOpenModal.setOnClickListener {
            navigateTo(newInstance("Modal", isModal = true))
        }

        buttonOpenBottomSheet.setOnClickListener {
            openBottomSheet(newInstance("Bottom Sheet"))
        }

        buttonOpenDialog.setOnClickListener {
            openDialog(newInstance("Dialog"))
        }

        buttonBackToRoot.setOnClickListener {
            navigateBackToRoot()
        }

        textStatus.text = when {
            isInBottomSheet -> "This is a bottom sheet"
            isInDialog -> "This is a dialog"
            isRoot -> "This is a root fragment"
            isModal -> "This is a modal fragment"
            else -> "This is a regular fragment"
        }
    }
}