package com.vojtkovszky.singleactivitynavigation

import androidx.annotation.AnimRes

class CustomAnimationSettings {
    @AnimRes var animationRootEnter = R.anim.enter_fade_in
    @AnimRes var animationRootExit = 0
    @AnimRes var animationRootPopEnter = 0
    @AnimRes var animationRootPopExit = R.anim.enter_fade_in
    @AnimRes var animationModalEnter = R.anim.enter_from_bottom
    @AnimRes var animationModalExit = R.anim.exit_to_top_short
    @AnimRes var animationModalPopEnter = R.anim.enter_from_top_short
    @AnimRes var animationModalPopExit = R.anim.exit_to_bottom
    @AnimRes var animationSecondaryEnter = R.anim.enter_from_right
    @AnimRes var animationSecondaryExit = R.anim.exit_to_left_short
    @AnimRes var animationSecondaryPopEnter = R.anim.enter_from_left_short
    @AnimRes var animationSecondaryPopExit = R.anim.exit_to_right

    fun setCustomAnimationsRoot(@AnimRes enter: Int = 0, @AnimRes exit: Int = 0,
                                @AnimRes popEnter: Int = 0, @AnimRes popExit: Int = 0) {
        this.animationRootEnter = enter
        this.animationRootExit = exit
        this.animationRootPopEnter = popEnter
        this.animationRootPopExit = popExit
    }

    fun setCustomAnimationsModal(@AnimRes enter: Int = 0, @AnimRes exit: Int = 0,
                                 @AnimRes popEnter: Int = 0, @AnimRes popExit: Int = 0) {
        this.animationModalEnter = enter
        this.animationModalExit = exit
        this.animationModalPopEnter = popEnter
        this.animationModalPopExit = popExit
    }

    fun setCustomAnimationsSecondary(@AnimRes enter: Int = 0, @AnimRes exit: Int = 0,
                                     @AnimRes popEnter: Int = 0, @AnimRes popExit: Int = 0) {
        this.animationSecondaryEnter = enter
        this.animationSecondaryExit = exit
        this.animationSecondaryPopEnter = popEnter
        this.animationSecondaryPopExit = popExit
    }
}