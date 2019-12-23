package com.codingblocks.cbonlineapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.custom_dialog.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.singleTop

object Components {
    fun showConfirmation(context: Context, type: String, callback: (state: Boolean) -> Unit = { status: Boolean -> }) {
        val confirmDialog = AlertDialog.Builder(context).create()
        val updateView = context.layoutInflater.inflate(R.layout.custom_dialog, null)
        when (type) {
            "verify" -> {
                updateView.okBtn.text = context.getString(R.string.verify_title)
                updateView.description.text = context.getString(R.string.verify_desc)
            }
            "trial" -> {
                updateView.okBtn.text = "Explore Now"
                updateView.description.text = context.getString(R.string.enroll_desc)
            }
            "exit" -> {
                updateView.okBtn.text = "Okay"
                updateView.description.text = "Do you want to exit?"
            }
            "wifi" -> {
                updateView.okBtn.text = "Enable"
                updateView.description.text =
                    "WIFI is disabled in your device. Would you like to enable it?"
            }
            "unavailable" -> {
                updateView.okBtn.text = "Ok"
                updateView.description.text =
                    "This section is unavailable on mobile, please view it on the browser instead!"
            }
            "expired" -> {
                updateView.okBtn.text = "Ok"
                updateView.description.text =
                    "This section is unavailable as your course has been expired.Please buy an extension to watch your videos"
            }
            "logout" -> {
                updateView.okBtn.text = "Yes"
                updateView.cancelBtn.text = "No"
                updateView.description.text = "Are you sure you want to logout?"
            }
            "reset" -> {
                updateView.okBtn.text = "Yes"
                updateView.cancelBtn.text = "No"
                updateView.description.text = "Are you sure you want to reset progress?"
            }
            "quiz" -> {
                updateView.okBtn.text = "Yes"
                updateView.cancelBtn.text = "Cancel"
                updateView.description.text = "Are you sure to submit the quiz?"
            }
            "file" -> {
                updateView.apply {
                    title.text = context.getString(R.string.clean_dialog_title)
                    description.text = context.getString(R.string.clean_dialog_description)
                    okBtn.text = context.getString(R.string.clean_dialog_okBtn)
                    cancelBtn.text = context.getString(R.string.clean_dialog_cancelBtn)
                }
            }
            UNAUTHORIZED -> {
                updateView.okBtn.text = "Log In"
                updateView.description.text =
                    "You have been logged out of this account.Please login again to Continue"
            }
        }
        updateView.okBtn.setOnClickListener {
            confirmDialog.dismiss()
            when (type) {
                "trial" -> context.startActivity(context.intentFor<DashboardActivity>("courseRun" to "mycourses").singleTop())
                "verify" -> {
                    openChrome(context, "https://account.codingblocks.com/users/me")
                }
                "exit" -> {
                    (context as Activity).finish()
                }
                "wifi" -> {
                    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
                UNAUTHORIZED -> {

                    openChrome(
                        context,
                        "${BuildConfig.OAUTH_URL}?redirect_uri=${BuildConfig.REDIRECT_URI}&response_type=code&client_id=${BuildConfig.CLIENT_ID}"
                    )
                }
                else -> {
                    callback(true)
                }
            }
        }
        updateView.cancelBtn.setOnClickListener {
            confirmDialog.dismiss()
            when (type) {
                UNAUTHORIZED -> {
                    callback(false)
                }
            }
        }
        confirmDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        confirmDialog.setView(updateView)
        confirmDialog.setCancelable(false)
        confirmDialog.show()
    }


    fun openChrome(context: Context, url: String, newTask: Boolean = false) {
        val builder = CustomTabsIntent.Builder()
            .enableUrlBarHiding()
            .setToolbarColor(context.resources.getColor(R.color.colorPrimaryDark))
            .setShowTitle(true)
            .setSecondaryToolbarColor(context.resources.getColor(R.color.colorPrimary))
        val customTabsIntent = builder.build()
        if (newTask) {
            customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}
