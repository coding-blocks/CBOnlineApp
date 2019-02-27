package com.codingblocks.cbonlineapp.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class DownloadService : Service() {

    private val downloadBinder = DownloadBinder()

    override fun onBind(intent: Intent): IBinder? {
        downloadBinder.downloadNotification?.downloadService = this
        return downloadBinder
    }
}

