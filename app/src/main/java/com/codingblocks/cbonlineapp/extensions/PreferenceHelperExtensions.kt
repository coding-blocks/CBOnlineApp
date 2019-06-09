package com.codingblocks.cbonlineapp.extensions

import android.content.Context
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.util.PreferenceHelper

@Keep
fun <A : Context> A.getPrefs(): PreferenceHelper {
    return PreferenceHelper.getPrefs(this)
}

@Keep
fun <F : Fragment> F.getPrefs(): PreferenceHelper? {
    return context?.let { PreferenceHelper.getPrefs(it) }
}
