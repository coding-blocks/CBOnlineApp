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
            "verify" -> {
                updateView.dialogPositiveBtn.text = context.getString(R.string.verify_title)
                updateView.dialogDescTv.text = context.getString(R.string.verify_desc)
            }
            "trial" -> {
                updateView.dialogPositiveBtn.text = "Explore Now"
                updateView.dialogDescTv.text = context.getString(R.string.enroll_desc)
            }
            "exit" -> {
                updateView.dialogPositiveBtn.text = "Okay"
                updateView.dialogDescTv.text = "Do you want to exit?"
            }

            "leave" -> {
                updateView.dialogPositiveBtn.text = "Okay"
                updateView.dialogDescTv.text = "Do you want to exit?"
            }

            "wifi" -> {
                updateView.dialogPositiveBtn.text = "Enable"
                updateView.dialogDescTv.text =
                    "WIFI is disabled in your device. Would you like to enable it?"
            }
            "unavailable" -> {
                updateView.dialogPositiveBtn.text = "Ok"
                updateView.dialogDescTv.text =
                    context.getString(R.string.unavailable)
            }
            "expired" -> {
                updateView.dialogPositiveBtn.text = "Ok"
                updateView.dialogDescTv.text =
                    "This section is unavailable as your course has been expired.Please buy an extension to watch your videos"
            }
            "logout" -> {
                updateView.dialogPositiveBtn.text = "Yes"
                updateView.dialogNegativeBtn.text = "No"
                updateView.dialogDescTv.text = "Are you sure you want to logout?"
            }
            "reset" -> {
                updateView.dialogPositiveBtn.text = "Yes"
                updateView.dialogNegativeBtn.text = "No"
                updateView.dialogDescTv.text = "You will lose all your course progress.\nAre you sure you want to reset ?"
            }
            "quiz" -> {
                updateView.dialogPositiveBtn.text = "Yes"
                updateView.dialogNegativeBtn.text = "Cancel"
                updateView.dialogDescTv.text = "Are you sure to submit the quiz?"
            }
            "file" -> {
                updateView.apply {
                    dialogTitleTv.text = context.getString(R.string.clean_dialog_title)
                    dialogDescTv.text = context.getString(R.string.clean_dialog_description)
                    dialogPositiveBtn.text = context.getString(R.string.clean_dialog_okBtn)
                    dialogNegativeBtn.text = context.getString(R.string.clean_dialog_cancelBtn)
                }
            }
            UNAUTHORIZED -> {
                updateView.dialogPositiveBtn.text = "Log In"
                updateView.dialogDescTv.text =
                    "You have been logged out of this account.Please login again to Continue"
            }
            LOGIN->{
                updateView.dialogPositiveBtn.text = "Log In"
                updateView.dialogDescTv.text =
                    "Please login to use this feature."
            }
        }
        updateView.dialogPositiveBtn.setOnClickListener {
            confirmDialog.dismiss()
            when (type) {
                "trial" -> context.startActivity(context.intentFor<DashboardActivity>("courseRun" to "mycourses").singleTop())
                "verify" -> {
                    context.openChrome("https://account.codingblocks.com/users/me")
                }
                "exit" -> {
                    (context as Activity).finish()
                }
                "leave" -> {
                    (context as AppCompatActivity).supportFragmentManager.popBackStack()
                }
                "wifi" -> {
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
                "file" -> {
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
