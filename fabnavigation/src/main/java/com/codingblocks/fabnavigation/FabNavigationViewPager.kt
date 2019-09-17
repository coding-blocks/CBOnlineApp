package com.codingblocks.fabnavigation

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class FabNavigationViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    private var swipe: Boolean = false

    init {
        this.swipe = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.swipe) {
            super.onTouchEvent(event)
        } else false

    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.swipe) {
            super.onInterceptTouchEvent(event)
        } else false

    }

    /**
     * Enable or disable the swipe navigation
     * @param enabled
     */
    fun setPagingEnabled(enabled: Boolean) {
        this.swipe = enabled
    }
}
