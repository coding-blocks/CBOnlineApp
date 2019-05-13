package com.codingblocks.cbonlineapp

interface DownloadStarter {
    fun startDownload(videoId: String, id: String, lectureContentId: String, title: String, attemptId: String, contentId: String, sectionId: String)
}
