package com.codingblocks.cbonlineapp

interface DownloadStarter {
    fun startDownload(url: String, id: String, lectureContentId: String, title: String)

}