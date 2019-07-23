package com.codingblocks.cbonlineapp.database

import androidx.core.app.NotificationCompat

data class DownloadData(val dataId: String, val videoId: String, val attemptId: String, val sectionId: String, val contentId: String, val lectureContentId: String, val notificationId: Int, val notificationBuilder: NotificationCompat.Builder)
