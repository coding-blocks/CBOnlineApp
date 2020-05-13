package com.codingblocks.cbonlineapp.util

import cn.campusapp.router.Router
import com.codingblocks.cbonlineapp.CBOnlineApp.Companion.mInstance
import com.codingblocks.cbonlineapp.admin.AdminActivity
import com.codingblocks.cbonlineapp.dashboard.DoubtCommentActivity
import com.codingblocks.cbonlineapp.database.DoubtsDao
import com.codingblocks.cbonlineapp.database.NotificationDao
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.util.extensions.openChrome
import com.codingblocks.cbonlineapp.util.extensions.otherwise
import com.codingblocks.onlineapi.models.Doubts
import com.google.gson.Gson
import com.onesignal.OSNotification
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.koin.core.KoinComponent
import org.koin.core.inject

var position: Long = 0

class NotificationOpenedHandler : OneSignal.NotificationOpenedHandler, KoinComponent {

    private val notificationDao: NotificationDao by inject()

    override fun notificationOpened(result: OSNotificationOpenResult) {
        val url = result.notification.payload.launchURL
        val data = result.notification.payload.additionalData
        val doubt = Gson().fromJson(data?.getString("doubt"), Doubts::class.java)

        Router.open("activity://courseRun/$url").otherwise {
            if (url.contains("admin")) {
                with(mInstance) { startActivity(intentFor<AdminActivity>().newTask()) }
            } else if (!doubt?.id.isNullOrEmpty()) {
                with(mInstance) {
                    startActivity(intentFor<DoubtCommentActivity>(DOUBT_ID to doubt.id
                    ).newTask())
                }
            } else
                mInstance.openChrome(url, true)
        }
//        doAsync {
//            notificationDao.updateseen(position)
//        }
    }
}

class NotificationReceivedHandler : OneSignal.NotificationReceivedHandler, KoinComponent {

    private val notificationDao: NotificationDao by inject()
    private val doubtsDao: DoubtsDao by inject()

    override fun notificationReceived(notification: OSNotification) {
        val data = notification.payload.additionalData
        val title = notification.payload.title
        val body = notification.payload.body
        val url = notification.payload.launchURL
        val videoId = data.optString(VIDEO_ID) ?: ""
        val doubt = Gson().fromJson(data?.getString("doubt"), Doubts::class.java)

        GlobalScope.launch {

            if (!doubt?.id.isNullOrEmpty()) {
                doubtsDao.insert(DoubtsModel(
                    dbtUid = doubt.id,
                    title = doubt.title,
                    body = doubt.body,
                    contentId = doubt.content?.id ?: "",
                    status = doubt.status,
                    runAttemptId = doubt.runAttempt?.id ?: "",
                    discourseTopicId = doubt.discourseTopicId,
                    conversationId = doubt.conversationId,
                    createdAt = doubt.createdAt
                ))
            }
//            notificationDao.insertWithId(
//                    Notification(
//                        heading = title,
//                        body = body,
//                        url = url,
//                        videoId = videoId
//                    )
//                )
//                .also {
//                    position = it
//
//                    val local = Intent()
//
//                    local.action = "com.codingblocks.notification"
//
//                    mInstance.sendBroadcast(local)
//                }
        }
    }
}
