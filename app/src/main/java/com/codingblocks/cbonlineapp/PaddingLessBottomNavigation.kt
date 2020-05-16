package com.codingblocks.cbonlineapp

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView

class PaddingLessBottomNavigation(context : Context, attributeSet: AttributeSet) : BottomNavigationView(context, attributeSet) {

    override fun onFinishInflate() {
        super.onFinishInflate()
        val menuView = getChildAt(0) as? ViewGroup
        menuView?.let {
            for (child in 0 until it.childCount) {
                val menuItem = it.getChildAt(child)
                val largeLabel =
                    menuItem?.findViewById<View>(com.google.android.material.R.id.largeLabel)
                largeLabel?.setPadding(0, 0, 0, 0)
            }
        }
    }
}
