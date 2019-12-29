package com.codingblocks.cbonlineapp.util.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager

/**
 * Created by pulkit-mac on 22/03/18.
 */

class ViewPagerCustomDuration : ViewPager {
    private var mScroller: FixedSpeedScroller? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    /*
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private fun init() {
        try {
            val viewpager = ViewPager::class.java
            val scroller = viewpager.getDeclaredField("mScroller")
            scroller.isAccessible = true
            mScroller = FixedSpeedScroller(context,
                DecelerateInterpolator())
            scroller.set(this, mScroller)
        } catch (ignored: Exception) {
        }
    }

    /*
     * Set the factor by which the duration will change
     */
    fun setScrollDuration(duration: Int) {
        mScroller!!.setScrollDuration(duration)
    }

    private inner class FixedSpeedScroller(context: Context, interpolator: Interpolator) : Scroller(context, interpolator) {

        private var mDuration = 800

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        fun setScrollDuration(duration: Int) {
            mDuration = duration
        }
    }
}
