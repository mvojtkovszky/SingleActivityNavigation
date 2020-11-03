package com.vojtkovszky.singleactivitynavigationexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vojtkovszky.singleactivitynavigation.BaseSingleFragment
import com.vojtkovszky.singleactivitynavigation.FragmentType
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : BaseSingleFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        super.onResume()

        // Set title. Determine type of current fragment with fragmentType parameter
        val title = getString(when(fragmentType) {
            FragmentType.BOTTOM_SHEET -> R.string.type_bottom_sheet
            FragmentType.DIALOG -> R.string.type_dialog
            FragmentType.ROOT -> R.string.type_root
            FragmentType.MODAL -> R.string.type_modal
            FragmentType.DEFAULT -> R.string.type_regular
            FragmentType.INVALID -> 0
        })

        // use it to set status text
        textStatus.text = getString(R.string.this_is_type_fragment, title)
        // and change title, but not needed in dialog
        if (!fragmentType.isDialogOrBottomSheet()) {
            baseSingleActivity?.supportActionBar?.title = title
        }

        // click listeners on buttons
        buttonOpenRegular.let {
            it.text = getString(R.string.open_type_fragment, getString(R.string.type_regular))
            it.setOnClickListener { navigateTo(MainFragment()) }
        }
        buttonOpenModal.let {
            it.text = getString(R.string.open_type_fragment, getString(R.string.type_modal))
            it.setOnClickListener { navigateTo(MainFragment(), openAsModal = true) }
        }
        buttonOpenBottomSheet.let {
            it.text = getString(R.string.open_type_fragment, getString(R.string.type_bottom_sheet))
            it.setOnClickListener { openBottomSheet(MainFragment()) }
        }
        buttonOpenDialog.let {
            it.text = getString(R.string.open_type_fragment, getString(R.string.type_dialog))
            it.setOnClickListener { openDialog(MainFragment())}
        }
        buttonBackToRoot.let {
            it.text = getString(R.string.back_to_root)
            it.setOnClickListener { navigateBackToRoot() }
        }
    }
}