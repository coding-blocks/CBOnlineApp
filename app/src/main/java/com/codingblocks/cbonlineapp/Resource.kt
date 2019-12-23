package com.codingblocks.cbonlineapp

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

class Resources {

    fun getString(@StringRes resId: Int, vararg args: Any?) = context?.getString(resId, args)

    fun getColor(@ColorRes resId: Int) = Companion.context?.resources?.getColor(resId)

    companion object {
        fun getString(resources: Resources, @StringRes resId: Int) = context?.getString(resId)

        fun getString(@StringRes resId: Int, vararg args: Any?) = context?.getString(resId, args)

        fun getColor(@ColorRes resId: Int) = context?.resources?.getColor(resId)

        private val context by lazy {
            CBOnlineApp.appContext
        }
    }
}
