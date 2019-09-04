package com.codingblocks.cbonlineapp.util

interface DownloadStarter {
    fun startDownload(
        videoId: String,
        sectionId: String,
        lectureContentId: String,
        title: String,
        attemptId: String,
        contentId: String
    )

    fun startSectionDownlod(sectionId: String)

    fun updateProgress(contentId: String)
}
