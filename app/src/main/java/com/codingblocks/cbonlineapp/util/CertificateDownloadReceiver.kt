package com.codingblocks.cbonlineapp.util

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

class CertificateDownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
            val downloadId = intent.getLongExtra(
                DownloadManager.EXTRA_DOWNLOAD_ID, 0
            )
            openDownloadedFile(context, downloadId)
        }
    }

    private fun openDownloadedFile(
        context: Context,
        downloadId: Long
    ) {
        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        val c: Cursor = downloadManager.query(query)
        if (c.moveToFirst()) {
            val downloadStatus: Int = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            val downloadPath: String =
                c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
            if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                var downloadUri = Uri.parse(downloadPath)
                if (downloadUri != null) {
                    if (ContentResolver.SCHEME_FILE == downloadUri.scheme) {
                        val file = File(downloadUri.path)
                        downloadUri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
                    }
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(downloadUri, "application/pdf")
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "Unable to Open File", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        c.close()
    }
}
