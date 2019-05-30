package com.codingblocks.cbonlineapp.viewmodels

import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.ContentDao

class SettingsViewModel(
    private val contentDao: ContentDao
) : ViewModel() {
    fun getDownloads() = contentDao.getDownloads("true")

    fun updateContent(section_id: String, lectureContentId: String, s: String) = contentDao.updateContent(section_id, lectureContentId, s)
}
