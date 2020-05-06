package com.codingblocks.cbonlineapp.util

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Environment
import android.widget.Toast

object Certificate  {

    fun downloadCertificateAndShow(context: Context, certificateUrl: String, fileName: String) {
        if (certificateUrl.isNullOrEmpty() || fileName.isNullOrEmpty()) {
            Toast.makeText(context, "Error fetching document", Toast.LENGTH_SHORT).show()
        } else {
            val uri = Uri.parse(certificateUrl)
            val request = DownloadManager.Request(uri)
            request.setMimeType("application/pdf")
            request.setTitle("$fileName.pdf")
            request.setDescription("Downloading attachment..")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        }
    }
}
