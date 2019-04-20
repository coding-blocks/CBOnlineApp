package com.codingblocks.cbonlineapp.extensions

import android.app.Activity
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.util.PreferenceHelper

@Keep
fun <A: Activity> A.getPrefs(): PreferenceHelper {
    return PreferenceHelper.getPrefs(this)
}

@Keep
fun <F: Fragment> F.getPrefs(): PreferenceHelper? {
    return context?.let { PreferenceHelper.getPrefs(it) }
}
