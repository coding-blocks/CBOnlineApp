package com.codingblocks.cbonlineapp.util

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import cn.campusapp.router.Router
import com.codingblocks.cbonlineapp.CBOnlineApp
import com.codingblocks.cbonlineapp.CBOnlineApp.Companion.mInstance
import com.codingblocks.cbonlineapp.R
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
        if (url.contains("course", true) ||
            url.contains("classroom", true) ||
            url.contains("player", true)
        )
            Router.open("activity://course/$url")
        else {
            val builder = CustomTabsIntent.Builder()
                .enableUrlBarHiding()
                .setToolbarColor(mInstance.resources.getColor(R.color.colorPrimaryDark))
                .setShowTitle(true)
                .setSecondaryToolbarColor(mInstance.resources.getColor(R.color.colorPrimary))
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(mInstance, Uri.parse(url))
        }
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

