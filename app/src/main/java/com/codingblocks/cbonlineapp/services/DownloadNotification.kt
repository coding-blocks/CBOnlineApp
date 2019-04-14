package com.codingblocks.cbonlineapp.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.codingblocks.cbonlineapp.util.MediaUtils

class DownloadNotification {
    //        NotificationCompat.Builder(this, MediaUtils.DOWNLOAD_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_file_download)
//                .setContentTitle("Download")
//                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
//                .setContentText("Downloading File")
//                .setProgress(0, 0, true)
//                .setColor(resources.getColor(R.color.colorPrimaryDark))
//                .setOngoing(true) // THIS is the important line
//                .setAutoCancel(false)
    var downloadService: DownloadService? = null
        set(downloadService) {
            field = downloadService

            intent = Intent()
            pendingIntent = PendingIntent.getActivity(downloadService, 0, intent, 0)
            notifyBuilder = NotificationCompat.Builder(downloadService!!, MediaUtils.DOWNLOAD_CHANNEL_ID)
        }
    private var lastProgress = 0
    private var intent: Intent? = null
    private var pendingIntent: PendingIntent? = null
    private var notifyBuilder: NotificationCompat.Builder? = null
    fun onSuccess() {
        this.downloadService?.stopForeground(true)
        sendDownloadNotification("Downloaded successfully.", "finalized", -1)
    }

    fun onFailed() {
        this.downloadService?.stopForeground(true)
        sendDownloadNotification("Download failed.", "finalized", -1)
    }

    fun onUpdateDownloadProgress(progress: Int) {
        try {
            lastProgress = progress
            sendDownloadNotification("Downloading ...", "Starting the service", progress)

            Thread.sleep(200)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun sendDownloadNotification(title: String, text: String?, progress: Int) {
        val notification = getDownloadNotification(title, text, progress)
        val notificationManager = this.downloadService!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    fun getDownloadNotification(title: String, text: String?, progress: Int): Notification {
        if (progress == 0) {
            val bitmap = BitmapFactory.decodeResource(this.downloadService!!.resources, android.R.drawable.stat_sys_download)
            notifyBuilder?.setLargeIcon(bitmap)
            notifyBuilder?.setSmallIcon(android.R.mipmap.sym_def_app_icon)
            notifyBuilder?.setAutoCancel(true)
            notifyBuilder?.setContentIntent(pendingIntent)
            notifyBuilder?.setContentTitle(title)
            if (text != null) notifyBuilder?.setContentText(text)
            notifyBuilder?.priority = NotificationCompat.PRIORITY_HIGH
            notifyBuilder?.setOngoing(true)
        }

        if (progress in 1..99) {
            val stringBuffer = StringBuffer()
            stringBuffer.append("Downloading progress ")
            stringBuffer.append(progress)
            stringBuffer.append("%")
            notifyBuilder?.setOngoing(true)
            notifyBuilder?.setContentText("Downloading progress $progress%")
            notifyBuilder?.setProgress(100, progress, false)
        }
        if (progress == -1) {
            notifyBuilder?.setContentTitle(title)
            notifyBuilder?.setOngoing(false)
            if (text != null) notifyBuilder?.setContentText(text)
            notifyBuilder?.setProgress(0, 0, false)
        }

        return notifyBuilder!!.build()
    }
}
