package com.codingblocks.cbonlineapp.settings

import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.ContentDao

class SettingsViewModel(
    private val contentDao: ContentDao
) : ViewModel() {
    suspend fun getDownloads() = contentDao.getDownloads("true")

    fun updateContent(section_id: String, lectureContentId: String, s: String) = "Nothing"
}
