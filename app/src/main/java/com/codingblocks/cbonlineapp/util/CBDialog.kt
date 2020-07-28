package com.codingblocks.cbonlineapp.util

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CBDialog(context: Context, type: DIALOG_TYPE?) : BaseCBDialogHelper() {

    //  dialog view
    override val dialogView: View by lazy {
        LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)
    }

    override val builder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(context).setView(dialogView)

    val title: TextView by lazy {
        dialogView.findViewById<TextView>(R.id.dialogTitleTv)
    }

    val desc: TextView by lazy {
        dialogView.findViewById<TextView>(R.id.dialogDescTv)
    }

    val negativeBtn: Button by lazy {
        dialogView.findViewById<Button>(R.id.dialogNegativeBtn)
    }

    //  close icon
    val positiveBtn: Button by lazy {
        dialogView.findViewById<Button>(R.id.dialogPositiveBtn)
    }

    fun negativeBtnClickListener(func: (() -> Unit)? = null) =
        with(negativeBtn) {
            setClickListenerToDialogButton(true, func)
        }

    fun positiveBtnClickListener(func: (() -> Unit)? = null) =
        with(positiveBtn) {
            setClickListenerToDialogButton(false, func)
        }

    //  view click listener as extension function
    private fun View.setClickListenerToDialogButton(dismiss: Boolean, func: (() -> Unit)?) =
        setOnClickListener {
            func?.invoke()
            if (dismiss)
                dialog?.dismiss()
        }

    fun setContent(type: DIALOG_TYPE?) {
        dialogView.context.run {
            when (type) {
                DIALOG_TYPE.UNAUTHORIZED -> {
                    positiveBtn.text = getString(R.string.log_in)
                    desc.text = getString(R.string.login_desc)
                }
                DIALOG_TYPE.PAUSED -> {
                    positiveBtn.text = "Yes, Un-Pause"
                }
                DIALOG_TYPE.PURCHASE -> {
                    cancelable = true
                    positiveBtn.text = getText(R.string.buy_now)
                    desc.text = getText(R.string.purchase)
                    negativeBtnClickListener { dialog?.dismiss() }
                }

                null -> TODO()
            }
        }
    }
}

/*
 * Confirm Dialog
 */
inline fun Activity.showConfirmDialog(type: DIALOG_TYPE?, func: CBDialog.() -> Unit = {}): AlertDialog =
    CBDialog(this, type).apply {
        setContent(type)
        func()
    }.create()

inline fun Fragment.showConfirmDialog(type: DIALOG_TYPE?, func: CBDialog.() -> Unit): AlertDialog =
    CBDialog(this.requireContext(), type).apply {
        setContent(type)
        func()
    }.create()

enum class DIALOG_TYPE {
    UNAUTHORIZED,
    PAUSED,
    PURCHASE
}
