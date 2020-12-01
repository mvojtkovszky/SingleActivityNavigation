package com.vojtkovszky.singleactivitynavigationexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vojtkovszky.singleactivitynavigation.BaseSingleFragment
import com.vojtkovszky.singleactivitynavigation.FragmentType
import com.vojtkovszky.singleactivitynavigationexample.databinding.FragmentMainBinding

class MainFragment : BaseSingleFragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
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
        binding.textStatus.text = getString(R.string.this_is_type_fragment, title)
        binding.textStackSize.text = getString(R.string.stack_size_is,
                activity?.supportFragmentManager?.backStackEntryCount ?: -1)
        // and change title, but not needed in dialog
        if (!fragmentType.isDialogOrBottomSheet()) {
            baseSingleActivity?.supportActionBar?.title = title
        }

        // click listeners on buttons
        binding.buttonOpenRegular.let {
            it.text = getString(R.string.open_type_fragment, getString(R.string.type_regular))
            it.setOnClickListener { navigateTo(MainFragment()) }
        }
        binding.buttonOpenModal.let {
            it.text = getString(R.string.open_type_fragment, getString(R.string.type_modal))
            it.setOnClickListener { navigateTo(MainFragment(), openAsModal = true) }
        }
        binding.buttonOpenBottomSheet.let {
            it.text = getString(R.string.open_type_fragment, getString(R.string.type_bottom_sheet))
            it.setOnClickListener { openBottomSheet(MainFragment()) }
        }
        binding.buttonOpenDialog.let {
            it.text = getString(R.string.open_type_fragment, getString(R.string.type_dialog))
            it.setOnClickListener { openDialog(MainFragment())}
        }
        binding.buttonBackToRoot.let {
            it.text = getString(R.string.back_to_root)
            it.setOnClickListener { navigateBackToRoot() }
        }
    }
}