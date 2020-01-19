package com.codingblocks.cbonlineapp.admin.doubts

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.codingblocks.cbonlineapp.CBOnlineApp
import com.codingblocks.cbonlineapp.CBOnlineApp.Companion.appContext
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.admin.AdminActivity
import com.codingblocks.cbonlineapp.util.ADMIN_CHANNEL_ID
import com.codingblocks.cbonlineapp.util.extensions.isotomillisecond
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DoubtReceiver : BroadcastReceiver() {
    private lateinit var notificationMgr: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val i = Intent(context, DoubtReceiver::class.java)

        val alarmIntent = PendingIntent.getBroadcast(
            context, 0, i, PendingIntent.FLAG_ONE_SHOT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + (60 * 1000 * 10),
                alarmIntent
            )
        }
        notificationMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        GlobalScope.launch {
            when (val response = safeApiCall(Dispatchers.IO) { Clients.onlineV2JsonApi.getLiveDoubts() }) {
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful)
                        if (!body()?.get().isNullOrEmpty()) {
                            body()?.get()?.get(0)?.apply {
                                Log.i("Notification Worker", "CurrentTime ${System.currentTimeMillis()} AckTime ${createdAt.isotomillisecond()}")
                                if ((System.currentTimeMillis() - createdAt.isotomillisecond()) < 900000) {
                                    showNotification(context)
                                }
                            }
                        }
                }
            }
        }
    }

    private fun showNotification(context: Context) {
        val intent = Intent(CBOnlineApp.appContext, AdminActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            appContext, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val notification = NotificationCompat.Builder(context, ADMIN_CHANNEL_ID).apply {
            setContentTitle("New Doubts to Resolve !!!!!")
            setSmallIcon(R.drawable.ic_conversation)
            setOnlyAlertOnce(true)
            setContentIntent(pendingIntent)
            setContentText("Students are waiting for your response.")
            setAutoCancel(true)
            setDefaults(NotificationCompat.DEFAULT_VIBRATE)
        }.build()

        notificationMgr.notify(1, notification)
    }
}
