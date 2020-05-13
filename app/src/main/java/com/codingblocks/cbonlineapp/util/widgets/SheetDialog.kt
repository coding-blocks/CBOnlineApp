package com.codingblocks.cbonlineapp.util.widgets

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.annotation.StyleRes
import com.google.android.material.bottomsheet.BottomSheetDialog

class SheetDialog(context: Context, @StyleRes theme: Int) : BottomSheetDialog(context, getThemeResId(context, theme)) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val v = window!!.decorView
            var flags = v.systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            v.systemUiVisibility = flags
        }
    }

    companion object {
        private fun getThemeResId(context: Context, themeId: Int): Int {
            var themeId = themeId
            if (themeId == 0) {
                val outValue = TypedValue()
                if (context.theme.resolveAttribute(com.google.android.material.R.attr.bottomSheetDialogTheme, outValue, true)) {
                    themeId = outValue.resourceId
                } else {
                    themeId = com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog
                }
            }
            return themeId
        }
    }
}
