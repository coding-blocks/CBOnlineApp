package com.codingblocks.cbonlineapp.util

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity

object Utils {
    fun setToolbar(
        activity: Activity?,
        title: String = "",
        hasUpEnabled: Boolean = true,
        show: Boolean = true
    ) {
        if (activity is AppCompatActivity) {
            if (show) {
                activity.supportActionBar?.title = title
                activity.supportActionBar?.setDisplayHomeAsUpEnabled(hasUpEnabled)
                activity.supportActionBar?.show()
            } else activity.supportActionBar?.hide()
        }
    }
}
