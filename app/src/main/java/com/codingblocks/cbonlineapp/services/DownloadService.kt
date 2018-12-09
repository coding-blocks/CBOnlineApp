package com.codingblocks.cbonlineapp.services

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.activities.MyCourseActivity
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Download
import okhttp3.ResponseBody
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DownloadService : IntentService("Download Service") {

    private var notificationBuilder: Notification.Builder? = null
    private var notificationManager: NotificationManager? = null
    private var totalFileSize: Int = 0


    override fun onHandleIntent(intent: Intent?) {

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationBuilder = Notification.Builder(this)
                //                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setAutoCancel(true)
        notificationManager!!.notify(0, notificationBuilder!!.build())

        initDownload()

    }

    private fun initDownload() {

        Clients.initiateDownload(url, "index.m3u8").enqueue(retrofitCallback { _, response ->
            response?.body()?.let { indexResponse ->
                downloadFile(indexResponse)
            }
        })

    }

    @Throws(IOException::class)
    private fun downloadFile(body: ResponseBody) {

        var count: Int
        val data = ByteArray(1024 * 4)
        val fileSize = body.contentLength()
        val bis = BufferedInputStream(body.byteStream(), 1024 * 8)
        val outputFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "file.zip")
        val output = FileOutputStream(outputFile)
        var total: Long = 0
        val startTime = System.currentTimeMillis()
        var timeCount = 1
        while ((count = bis.read(data)) != -1) {

            total += count.toLong()
            totalFileSize = (fileSize / Math.pow(1024.0, 2.0)).toInt()
            val current = Math.round(total / Math.pow(1024.0, 2.0)).toDouble()

            val progress = (total * 100 / fileSize).toInt()

            val currentTime = System.currentTimeMillis() - startTime

            val download = Download()
            download.totalFileSize = totalFileSize

            if (currentTime > 1000 * timeCount) {

                download.currentFileSize = current.toInt()
                download.progress = progress
                sendNotification(download)
                timeCount++
            }

            output.write(data, 0, count)
        }
        onDownloadComplete()
        output.flush()
        output.close()
        bis.close()

    }

    private fun sendNotification(download: Download) {

        sendIntent(download)
        notificationBuilder!!.setProgress(100, download.progress, false)
        notificationBuilder!!.setContentText(String.format("Downloaded (%d/%d) MB", download.currentFileSize, download.totalFileSize))
        notificationManager!!.notify(0, notificationBuilder!!.build())
    }


    private fun sendIntent(download: Download) {

        val intent = Intent(MyCourseActivity.MESSAGE_PROGRESS)
        intent.putExtra("download", download)
        BroadcastReceiver.getInstance(this@DownloadService).sendBroadcast(intent)
    }

    private fun onDownloadComplete() {

        val download = Download()
        download.progress = 100
        sendIntent(download)

        notificationManager!!.cancel(0)
        notificationBuilder!!.setProgress(0, 0, false)
        notificationBuilder!!.setContentText("File Downloaded")
        notificationManager!!.notify(0, notificationBuilder!!.build())

    }

    override fun onTaskRemoved(rootIntent: Intent) {
        notificationManager!!.cancel(0)
    }

}
