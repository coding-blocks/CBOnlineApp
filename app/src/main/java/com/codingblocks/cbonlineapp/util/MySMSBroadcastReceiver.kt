package com.codingblocks.cbonlineapp.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

/**
 * @author aggarwalpulkit596
 */
/**
 * BroadcastReceiver to wait for SMS messages. This can be registered either
 * in the AndroidManifest or at runtime.  Should filter Intents on
 * SmsRetriever.SMS_RETRIEVED_ACTION.
 */
class MySMSBroadcastReceiver(val otpReceivedListener: OnSmsOTPReceivedListener) :
    BroadcastReceiver() {

    interface OnSmsOTPReceivedListener {
        fun onSmsOTPReceieved(otp: String)
        fun onSmsOTPTimeout()
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras!![SmsRetriever.EXTRA_STATUS] as Status?
            when (status!!.statusCode) {
                CommonStatusCodes.SUCCESS -> { // Get SMS message contents
                    val message: String? = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?

                    message?.let { msg ->
                        Regex("(\\d{6})").find(msg)?.groups
                            ?.takeIf { it.isNotEmpty() }
                            ?.get(0)?.value
                            ?.let { otp -> otpReceivedListener.onSmsOTPReceieved(otp) }
                            ?: otpReceivedListener.onSmsOTPReceieved("")
                    }
                }
                CommonStatusCodes.TIMEOUT -> otpReceivedListener.onSmsOTPTimeout()
            }
        }
    }

    companion object {
        fun register(activity: FragmentActivity, otpReceivedListener: OnSmsOTPReceivedListener) {
            val receiver = MySMSBroadcastReceiver(otpReceivedListener)
            val lifecycleObserver = object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
                fun unregister() {
                    try {
                        activity.unregisterReceiver(receiver)
                    } catch (e: Exception) {
                        // Handle Error
                    }
                }
            }

            if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {

                activity.registerReceiver(
                    receiver,
                    IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
                )

                activity.lifecycle.addObserver(lifecycleObserver)
            }
        }
    }
}
