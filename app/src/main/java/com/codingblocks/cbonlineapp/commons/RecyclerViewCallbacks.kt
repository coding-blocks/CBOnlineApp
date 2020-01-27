package com.codingblocks.cbonlineapp.commons

interface NotificationClickListener {
    fun onClick(notificationID: Long, url: String, videoId: String)
}

interface SectionListClickListener {

    fun onClick(pos: Int, adapterPosition: Int)
}

interface OnCartItemClickListener {
    fun onItemClick(id: String, name: String)
}

interface DownloadStarter {
    fun startDownload(videoId: String, contentId: String, title: String, attemptId: String, sectionId: String)

    fun startSectionDownlod(sectionId: String)
}

interface OnItemClickListener {
    fun onItemClick(position: Int, id: String)
}
