package com.vojtkovszky.singleactivitynavigationexample

import android.os.Bundle
import android.view.View
import com.vojtkovszky.singleactivitynavigation.BaseSingleActivity
import com.vojtkovszky.singleactivitynavigation.BaseSingleFragment
import com.vojtkovszky.singleactivitynavigationexample.databinding.ActivityMainBinding

class MainActivity : BaseSingleActivity() {

    companion object {
        // positions for root fragments in bottom navigation
        private const val ROOT_FRAGMENT_POS_HOME = 0
        private const val ROOT_FRAGMENT_POS_DASHBOARD = 1
        private const val ROOT_FRAGMENT_POS_NOTIFICATIONS = 2
        // bundle keys for instance state
        private const val ARG_SELECTED_TAB_INDEX = "MainActivity.ARG_SELECTED_TAB_INDEX"
    }

    private var selectedTabIndex = ROOT_FRAGMENT_POS_HOME
    private var rootFragments = MutableList<BaseSingleFragment?>(3) { null }

    private lateinit var binding: ActivityMainBinding

    // define main fragments based on position
    override fun getDefaultFragmentContainerId(): Int {
        return R.id.fragmentContainer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // we'll be switching main fragments with out bottom navigation
        binding.navigationView.setOnNavigationItemSelectedListener {
            selectedTabIndex = when (it.itemId) {
                R.id.navigation_home -> ROOT_FRAGMENT_POS_HOME
                R.id.navigation_dashboard -> ROOT_FRAGMENT_POS_DASHBOARD
                R.id.navigation_notifications -> ROOT_FRAGMENT_POS_NOTIFICATIONS
                else -> return@setOnNavigationItemSelectedListener false
            }

            selectTab(selectedTabIndex)

            return@setOnNavigationItemSelectedListener true
        }

        // fresh start
        if (savedInstanceState == null) {
            // set animation behaviour
            customAnimationSettings.setCustomAnimationsRoot(
                    0, 0, 0, 0)
            customAnimationSettings.setCustomAnimationsModal(
                    R.anim.enter_from_bottom, R.anim.exit_to_top_short,
                    R.anim.enter_from_top_short, R.anim.exit_to_bottom)
            customAnimationSettings.setCustomAnimationsDefault(
                    R.anim.enter_from_right, R.anim.exit_to_left_short,
                    R.anim.enter_from_left_short, R.anim.exit_to_right)

            closeDialogsAndSheetsWhileNavigating = true

            // select fragment
            selectTab(selectedTabIndex)
        }
        // just restore bottom bar visibility
        else {
            handleNavigationViewVisibility(supportFragmentManager.backStackEntryCount)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        for (i in 0..rootFragments.lastIndex) {
            rootFragments[i] = null
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_SELECTED_TAB_INDEX, selectedTabIndex)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectedTabIndex = savedInstanceState.getInt(ARG_SELECTED_TAB_INDEX, ROOT_FRAGMENT_POS_HOME)
    }

    override fun onBackStackChanged(backStackCount: Int) {
        handleNavigationViewVisibility(backStackCount)
        super.onBackStackChanged(backStackCount)
    }

    private fun handleNavigationViewVisibility(backStackCount: Int) {
        // only make bottom bar visible if we're on root screen
        binding.navigationView.visibility = if (backStackCount == 0) View.VISIBLE else View.GONE
    }

    private fun selectTab(index: Int) {
        // setup if not set
        if (rootFragments[index] == null) {
            rootFragments[index] = when (index) {
                ROOT_FRAGMENT_POS_HOME -> MainFragment()
                ROOT_FRAGMENT_POS_DASHBOARD -> MainFragment()
                ROOT_FRAGMENT_POS_NOTIFICATIONS -> MainFragment()
                else -> null
            }
        }
        // select
        rootFragments[index]?.let {
            navigateToRoot(it)
        }
    }
}