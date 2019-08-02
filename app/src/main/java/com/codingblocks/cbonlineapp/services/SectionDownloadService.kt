package com.codingblocks.cbonlineapp.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.vdocipher.aegis.offline.DownloadStatus
import com.vdocipher.aegis.offline.VdoDownloadManager

class SectionDownloadService : Service(), VdoDownloadManager.EventListener {
    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onChanged(p0: String?, p1: DownloadStatus?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDeleted(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFailed(p0: String?, p1: DownloadStatus?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onQueued(p0: String?, p1: DownloadStatus?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCompleted(p0: String?, p1: DownloadStatus?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
