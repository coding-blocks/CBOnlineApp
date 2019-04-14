package com.codingblocks.cbonlineapp.services

import android.os.Binder

class DownloadBinder : Binder() {
    var downloadTask: DownloadTask? = null
    var downloadNotification: DownloadNotification? = null
    private var downloadUrl: String? = ""

    init {
        if (downloadNotification == null) {
            downloadNotification = DownloadNotification()
        }
    }

    fun startDownload(downloadUrl: String, progress: Int, id: String, lectureContentId: String, title: String) {
        downloadTask = DownloadTask(downloadNotification!!)

        DownloadUtil.downloadManager = downloadTask

        downloadTask!!.execute(downloadUrl, id, lectureContentId)

        this.downloadUrl = downloadUrl
        val notification = downloadNotification?.getDownloadNotification(title, "Starting the service", progress)
        downloadNotification!!.downloadService?.startForeground(1, notification)
    }
}

