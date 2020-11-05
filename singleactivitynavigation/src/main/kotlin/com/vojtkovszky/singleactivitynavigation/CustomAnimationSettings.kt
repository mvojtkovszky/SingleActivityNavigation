package com.vojtkovszky.singleactivitynavigation

import androidx.annotation.AnimRes
import java.io.Serializable

/**
 * Global custom animation settings.
 * Animation resources to be used when navigating between fragments based on their type.
 * By default, no animations are used.
 *
 * Mind that if [BaseSingleFragment] uses custom animations, it will override this behaviour,
 * but in general consistent use of transitions could be achieved by defining it here only.
 */
@Suppress("unused")
class CustomAnimationSettings : Serializable {
    @AnimRes var animationRootEnter = 0
    @AnimRes var animationRootExit = 0
    @AnimRes var animationRootPopEnter = 0
    @AnimRes var animationRootPopExit = 0
    @AnimRes var animationModalEnter = 0
    @AnimRes var animationModalExit = 0
    @AnimRes var animationModalPopEnter = 0
    @AnimRes var animationModalPopExit = 0
    @AnimRes var animationDefaultEnter = 0
    @AnimRes var animationDefaultExit = 0
    @AnimRes var animationDefaultPopEnter = 0
    @AnimRes var animationDefaultPopExit = 0

    /**
     * Set custom animation resources for root fragments transactions.
     */
    fun setCustomAnimationsRoot(@AnimRes enter: Int = 0,
                                @AnimRes exit: Int = 0,
                                @AnimRes popEnter: Int = 0,
                                @AnimRes popExit: Int = 0) {
        this.animationRootEnter = enter
        this.animationRootExit = exit
        this.animationRootPopEnter = popEnter
        this.animationRootPopExit = popExit
    }

    /**
     * Set custom animation resources for modal fragments transactions.
     */
    fun setCustomAnimationsModal(@AnimRes enter: Int = 0,
                                 @AnimRes exit: Int = 0,
                                 @AnimRes popEnter: Int = 0,
                                 @AnimRes popExit: Int = 0) {
        this.animationModalEnter = enter
        this.animationModalExit = exit
        this.animationModalPopEnter = popEnter
        this.animationModalPopExit = popExit
    }

    /**
     * Set custom animation resources for secondary fragments transactions.
     */
    fun setCustomAnimationsDefault(@AnimRes enter: Int = 0,
                                   @AnimRes exit: Int = 0,
                                   @AnimRes popEnter: Int = 0, 
                                   @AnimRes popExit: Int = 0) {
        this.animationDefaultEnter = enter
        this.animationDefaultExit = exit
        this.animationDefaultPopEnter = popEnter
        this.animationDefaultPopExit = popExit
    }
}