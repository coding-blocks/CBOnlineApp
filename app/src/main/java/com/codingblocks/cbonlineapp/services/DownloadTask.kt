package com.codingblocks.cbonlineapp.services

import android.os.AsyncTask
import java.io.File

class DownloadTask(downloadListener: DownloadNotification) : AsyncTask<String, Int, Int>() {

    private var downloadListener: DownloadNotification? = null

    var lastDownloadProgress = 0
    private var currDownloadUrl = ""

    init {
        this.downloadListener = downloadListener
    }

    override fun doInBackground(vararg params: String): Int? {


        // Set the low priority priority for the Pause, Continue, and Cancel actions
        // of the main line are not blocked.
        Thread.currentThread().priority = Thread.NORM_PRIORITY - 2

        var downloadFileUrl = ""
        if (params != null && params.isNotEmpty()) {
            downloadFileUrl = params[0]
        }

        this.currDownloadUrl = downloadFileUrl


        // file created in the download folder
        //returns download status

        return FileUtil.downloadFileFromUrl(downloadFileUrl)
    }

    override fun onPostExecute(downloadStatue: Int?) {
        if (downloadStatue == DownloadUtil.DOWNLOAD_SUCCESS) {
            downloadListener?.onSuccess()
        } else if (downloadStatue == DownloadUtil.DOWNLOAD_FAILED) {
            downloadListener?.onFailed()
        }
    }

    /* Update download async task progress. */
    fun updateTaskProgress(newDownloadProgress: Int?) {
        lastDownloadProgress = newDownloadProgress!!
        downloadListener?.onUpdateDownloadProgress(newDownloadProgress)
    }
}