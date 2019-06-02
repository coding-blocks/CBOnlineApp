package com.codingblocks.cbonlineapp.util

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import cn.campusapp.router.Router
import cn.campusapp.router.route.ActivityRoute
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
        val data = result.notification.payload.additionalData
        if (url.contains("course", true) ||
            url.contains("classroom", true)
        ) {
            Router.open("activity://course/$url")
        } else if (url.contains("player", true)) {
            val activityRoute = Router.getRoute("activity://course/$url") as ActivityRoute
            activityRoute
                .withParams(VIDEO_ID, data.optString(VIDEO_ID))
                .open()
        } else {
            val builder = CustomTabsIntent.Builder()
                .enableUrlBarHiding()
                .setToolbarColor(mInstance.resources.getColor(R.color.colorPrimaryDark))
                .setShowTitle(true)
                .setSecondaryToolbarColor(mInstance.resources.getColor(R.color.colorPrimary))
            val customTabsIntent = builder.build()
            customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
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

                val local = Intent()

                local.action = "com.codingblocks.notification"

                CBOnlineApp.mInstance.sendBroadcast(local)
            }
    }
}
