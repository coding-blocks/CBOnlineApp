package com.codingblocks.cbonlineapp.services

object DownloadUtil {
    const val TAG_DOWNLOAD = "TAG_DOWNLOAD"
    const val DOWNLOAD_SUCCESS = 1
    const val DOWNLOAD_FAILED = 2
    var downloadManager: DownloadTask? = null
}
