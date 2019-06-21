package com.codingblocks.cbonlineapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.HomeActivity
import kotlinx.android.synthetic.main.custom_dialog.view.cancelBtn
import kotlinx.android.synthetic.main.custom_dialog.view.description
import kotlinx.android.synthetic.main.custom_dialog.view.okBtn
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.singleTop

object Components {
    fun showconfirmation(context: Context, type: String) {
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
        }
        updateView.okBtn.setOnClickListener {
            when (type) {
                "trial" -> context.startActivity(context.intentFor<HomeActivity>("course" to "mycourses").singleTop())
                "verify" -> {
                    openChrome(context, "https://account.codingblocks.com/users/me")
                }
                "exit" -> {
                    (context as Activity).finish()
                }
                "wifi" -> {
                    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
                "unavailable" -> {
                    confirmDialog.dismiss()
                }
            }
        }
        updateView.cancelBtn.setOnClickListener {
            confirmDialog.dismiss()
        }
        confirmDialog.window.setBackgroundDrawableResource(android.R.color.transparent)
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
