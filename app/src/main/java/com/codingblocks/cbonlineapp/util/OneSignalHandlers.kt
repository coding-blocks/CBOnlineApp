package com.codingblocks.cbonlineapp.util

import android.content.Intent
import cn.campusapp.router.Router
import com.codingblocks.cbonlineapp.CBOnlineApp
import com.codingblocks.cbonlineapp.database.NotificationDao
import com.codingblocks.cbonlineapp.database.models.Notification
import com.onesignal.OSNotification
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal
import org.jetbrains.anko.doAsync
import org.koin.standalone.StandAloneContext

var position: Long = 0

private val notificationDao = StandAloneContext.getKoin().koinContext.get<NotificationDao>()

class NotificationOpenedHandler : OneSignal.NotificationOpenedHandler {


    override fun notificationOpened(result: OSNotificationOpenResult) {
        val url = result.notification.payload.launchURL

        Router.open("activity://course/$url")

        doAsync {
            notificationDao.updateseen(position)
        }
    }

}

class NotificationReceivedHandler : OneSignal.NotificationReceivedHandler {


    override fun notificationReceived(notification: OSNotification) {
        val data = notification.payload.additionalData
        val title = notification.payload.title
        val body = notification.payload.body
        val url = notification.payload.launchURL
        notificationDao.insertWithId(Notification(heading = title, body = body, url = url))
            .also {
                position = it

                val local = Intent()

                local.action = "com.codingblocks.notification"

                CBOnlineApp.mInstance.sendBroadcast(local)
            }
    }

}

