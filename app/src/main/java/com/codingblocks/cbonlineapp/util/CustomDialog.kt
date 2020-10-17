package com.codingblocks.cbonlineapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.extensions.openChrome
import kotlinx.android.synthetic.main.custom_dialog.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.singleTop

object CustomDialog {
    fun showConfirmation(context: Context, type: String, callback: (state: Boolean) -> Unit = { }) {
        val confirmDialog = AlertDialog.Builder(context).create()
        val updateView = context.layoutInflater.inflate(R.layout.custom_dialog, null)
        when (type) {
            VERIFY -> {
                updateView.dialogPositiveBtn.text = context.getString(R.string.verify_title)
                updateView.dialogDescTv.text = context.getString(R.string.verify_desc)
            }
            TRIAL -> {
                updateView.dialogPositiveBtn.text = context.getString(R.string.explore_now)
                updateView.dialogDescTv.text = context.getString(R.string.enroll_desc)
            }
            EXIT -> {
                updateView.dialogPositiveBtn.text = context.getString(R.string.okay)
                updateView.dialogDescTv.text = context.getString(R.string.do_you_want_to_exit)
            }

            LEAVE -> {
                updateView.dialogPositiveBtn.text = context.getString(R.string.okay)
                updateView.dialogDescTv.text = context.getString(R.string.do_you_want_to_exit)
            }

            WIFI -> {
                updateView.dialogPositiveBtn.text = context.getString(R.string.enable)
                updateView.dialogDescTv.text =
                    context.getString(R.string.disabled_wifi_message)
            }
            UNAVAILABLE -> {
                updateView.dialogPositiveBtn.text = context.getString(R.string.Ok)
                updateView.dialogDescTv.text =
                    context.getString(R.string.unavailable)
            }
            EXPIRED -> {
                updateView.dialogPositiveBtn.text = context.getString(R.string.Ok)
                updateView.dialogDescTv.text =
                    context.getString(R.string.expired_popup_desc)
            }
            LOGOUT -> {
                updateView.dialogPositiveBtn.text = context.getString(R.string.yes)
                updateView.dialogNegativeBtn.text = context.getString(R.string.no)
                updateView.dialogDescTv.text = context.getString(R.string.logout_message)
            }
            RESET -> {
                updateView.dialogPositiveBtn.text = context.getString(R.string.yes)
                updateView.dialogNegativeBtn.text = context.getString(R.string.no)
                updateView.dialogDescTv.text = "You will lose all your course progress.\nAre you sure you want to reset ?"
            }
            QUIZ -> {
                updateView.dialogPositiveBtn.text = context.getString(R.string.yes)
                updateView.dialogNegativeBtn.text = context.getString(R.string.cancel)
                updateView.dialogDescTv.text = context.getString(R.string.submit_quiz_message)
            }
            FILE -> {
                updateView.apply {
                    dialogTitleTv.text = context.getString(R.string.clean_dialog_title)
                    dialogDescTv.text = context.getString(R.string.clean_dialog_description)
                    dialogPositiveBtn.text = context.getString(R.string.clean_dialog_okBtn)
                    dialogNegativeBtn.text = context.getString(R.string.clean_dialog_cancelBtn)
                }
            }
            UNAUTHORIZED -> {
                updateView.dialogPositiveBtn.text = context.getString(R.string.log_in)
                updateView.dialogDescTv.text =
                    context.getString(R.string.unauthorized_popup_desc)
            }
            LOGIN -> {
                updateView.dialogPositiveBtn.text = context.getString(R.string.log_in)
                updateView.dialogDescTv.text =
                    context.getString(R.string.login_popup_desc)
            }
        }
        updateView.dialogPositiveBtn.setOnClickListener {
            confirmDialog.dismiss()
            when (type) {
                TRIAL -> context.startActivity(context.intentFor<DashboardActivity>("courseRun" to "mycourses").singleTop())
                VERIFY -> {
                    context.openChrome("https://account.codingblocks.com/users/me")
                }
                EXIT -> {
                    (context as Activity).finish()
                }
                LEAVE -> {
                    (context as AppCompatActivity).supportFragmentManager.popBackStack()
                }
                WIFI -> {
                    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
                UNAUTHORIZED -> {
                    callback(true)
                }
                else -> {
                    callback(true)
                }
            }
        }
        updateView.dialogNegativeBtn.setOnClickListener {
            confirmDialog.dismiss()
            when (type) {
                FILE -> {
                    callback(false)
                }
                UNAUTHORIZED -> {
                    callback(false)
                }
            }
        }
        confirmDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        confirmDialog.setView(updateView)
        confirmDialog.setCancelable(false)
        try {
            confirmDialog.show()
        } catch (e: Exception) {
        }
    }
}
