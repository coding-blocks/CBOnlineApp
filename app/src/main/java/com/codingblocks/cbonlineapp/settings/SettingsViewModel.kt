package com.codingblocks.cbonlineapp.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.models.ContentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val contentDao: ContentDao
) : ViewModel() {

    suspend fun getDownloads(): List<ContentModel> =
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            contentDao.getDownloads(true)
        }

    fun updateContent(section_id: String, lectureContentId: String, s: String) = "Nothing"
}
