package com.codingblocks.cbonlineapp.util.widgets

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.progress_dialog.view.*

/**
 * @author aggarwalpulkit596
 */
class ProgressDialog {
    companion object {
        fun progressDialog(context: Context, title: String = ""): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
            if (title.isNotEmpty())
                inflate.progressTitle.text = title
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))
            return dialog
        }
    }
}
