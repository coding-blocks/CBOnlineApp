package com.codingblocks.cbonlineapp.util

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

object Utils {
    fun setToolbar(
        activity: Activity,
        toolbar: MaterialToolbar,
        title: String = "",
        hasUpEnabled: Boolean = true,
        show: Boolean = true
    ) {
        if (activity is AppCompatActivity) {
            activity.setSupportActionBar(toolbar)
            if (show) {
                activity.supportActionBar?.title = title
                activity.supportActionBar?.setDisplayHomeAsUpEnabled(hasUpEnabled)
                activity.supportActionBar?.show()
            } else activity.supportActionBar?.hide()
        }
    }
}
