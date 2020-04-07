package com.codingblocks.cbonlineapp.util

import android.content.Intent
import android.util.Log
import cn.campusapp.router.Router
import com.codingblocks.cbonlineapp.CBOnlineApp
import com.codingblocks.cbonlineapp.CBOnlineApp.Companion.mInstance
import com.codingblocks.cbonlineapp.admin.AdminActivity
import com.codingblocks.cbonlineapp.database.NotificationDao
import com.codingblocks.cbonlineapp.database.models.Notification
import com.codingblocks.cbonlineapp.util.extensions.openChrome
import com.codingblocks.cbonlineapp.util.extensions.otherwise
import com.onesignal.OSNotification
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.koin.core.KoinComponent
import org.koin.core.context.GlobalContext
import org.koin.core.inject

var position: Long = 0

class NotificationOpenedHandler : OneSignal.NotificationOpenedHandler, KoinComponent {

    private val notificationDao : NotificationDao by inject()

    override fun notificationOpened(result: OSNotificationOpenResult) {
        val url = result.notification.payload.launchURL

        Router.open("activity://courseRun/$url").otherwise {
            if (url.contains("admin")) {
                with(mInstance) { startActivity(intentFor<AdminActivity>().newTask()) }
            }
            mInstance.openChrome(url, true)
        }
        doAsync {
            notificationDao.updateseen(position)
        }
    }
}

class NotificationReceivedHandler : OneSignal.NotificationReceivedHandler , KoinComponent {

    private val notificationDao : NotificationDao by inject()

    override fun notificationReceived(notification: OSNotification) {
        val data = notification.payload.additionalData
        val payload = notification.payload
        val title = notification.payload.title
        val body = notification.payload.body
        val url = notification.payload.launchURL
        val videoId = data.optString(VIDEO_ID) ?: ""
        notificationDao.insertWithId(
                Notification(
                    heading = title,
                    body = body,
                    url = url,
                    videoId = videoId
                )
            )
            .also {
                position = it

                Log.d("Koin" , "$position")

                val local = Intent()

                local.action = "com.codingblocks.notification"

                mInstance.sendBroadcast(local)
            }
    }
}
