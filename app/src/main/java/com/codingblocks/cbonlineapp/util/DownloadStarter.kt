package com.codingblocks.cbonlineapp.util

interface DownloadStarter {
    fun startDownload(
        videoId: String,
        contentId: String,
        title: String,
        attemptId: String
    )

    fun startSectionDownlod(sectionId: String)

    fun updateProgress(contentId: String, progressId: String)
}
