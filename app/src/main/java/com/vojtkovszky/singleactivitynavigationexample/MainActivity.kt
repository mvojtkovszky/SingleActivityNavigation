package com.vojtkovszky.singleactivitynavigationexample

import android.os.Bundle
import android.view.View
import com.vojtkovszky.singleactivitynavigation.BaseSingleActivity
import com.vojtkovszky.singleactivitynavigation.BaseSingleFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseSingleActivity() {

    // define main fragments based on position
    override fun getNewMainFragmentInstance(positionIndex: Int): BaseSingleFragment? {
        return when (positionIndex) {
            0 -> MainFragment.newInstance(getString(R.string.title_home), isRoot = true)
            1 -> MainFragment.newInstance(getString(R.string.title_dashboard), isRoot = true)
            2 -> MainFragment.newInstance(getString(R.string.title_notifications), isRoot = true)
            else -> null
        }
    }

    // define container where fragments will be held
    override val fragmentContainerId: Int = R.id.fragmentContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // we'll be switching main fragments with out bottom navigation
        navigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> selectMainFragment(0)
                R.id.navigation_dashboard -> selectMainFragment(1)
                R.id.navigation_notifications -> selectMainFragment(2)
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
        customAnimationSettings.setCustomAnimationsSecondary(
            R.anim.enter_from_right, R.anim.exit_to_left_short,
            R.anim.enter_from_left_short, R.anim.exit_to_right)

        // select default fragment on tab 1
        selectMainFragment(0)
    }

    override fun onBackStackChanged(backStackCount: Int) {
        // only make bottom bar visible if we're on root screen
        navigationView.visibility = if (backStackCount == 0) View.VISIBLE else View.GONE

        super.onBackStackChanged(backStackCount)
    }
}