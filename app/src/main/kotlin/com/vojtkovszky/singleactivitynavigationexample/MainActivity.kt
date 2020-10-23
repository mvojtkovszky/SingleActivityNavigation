package com.vojtkovszky.singleactivitynavigationexample

import android.os.Bundle
import android.view.View
import com.vojtkovszky.singleactivitynavigation.BaseSingleActivity
import com.vojtkovszky.singleactivitynavigation.BaseSingleFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity() : BaseSingleActivity() {

    companion object {
        // positions for root fragments in bottom navigation
        private const val ROOT_FRAGMENT_POS_HOME = 0
        private const val ROOT_FRAGMENT_POS_DASHBOARD = 1
        private const val ROOT_FRAGMENT_POS_NOTIFICATIONS = 2
    }

    // define main fragments based on position
    override fun getNewRootFragmentInstance(positionIndex: Int): BaseSingleFragment? {
        return when (positionIndex) {
            // we'll use same fragment for all root positions
            ROOT_FRAGMENT_POS_HOME, ROOT_FRAGMENT_POS_DASHBOARD, ROOT_FRAGMENT_POS_NOTIFICATIONS ->
                MainFragment()
            else -> null
        }
    }

    // define containers where fragments will be held
    override val defaultFragmentContainerId: Int = R.id.fragmentContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // we'll be switching main fragments with out bottom navigation
        navigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> selectRootFragment(ROOT_FRAGMENT_POS_HOME)
                R.id.navigation_dashboard -> selectRootFragment(ROOT_FRAGMENT_POS_DASHBOARD)
                R.id.navigation_notifications -> selectRootFragment(ROOT_FRAGMENT_POS_NOTIFICATIONS)
                else -> return@setOnNavigationItemSelectedListener false
            }
            return@setOnNavigationItemSelectedListener true
        }

        // set animation behaviour
        customAnimationSettings.setCustomAnimationsRoot(
            R.anim.enter_fade_in, 0, 0, 0)
        customAnimationSettings.setCustomAnimationsModal(
            R.anim.enter_from_bottom, R.anim.exit_to_top_short,
            R.anim.enter_from_top_short, R.anim.exit_to_bottom)
        customAnimationSettings.setCustomAnimationsDefault(
            R.anim.enter_from_right, R.anim.exit_to_left_short,
            R.anim.enter_from_left_short, R.anim.exit_to_right)

        // use home fragment as default
        selectRootFragment(ROOT_FRAGMENT_POS_HOME)
    }

    override fun onBackStackChanged(backStackCount: Int) {
        // only make bottom bar visible if we're on root screen
        navigationView.visibility = if (backStackCount == 0) View.VISIBLE else View.GONE

        super.onBackStackChanged(backStackCount)
    }
}