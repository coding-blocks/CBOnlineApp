package com.codingblocks.cbonlineapp.settings

import androidx.lifecycle.viewModelScope
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.models.ContentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val contentDao: ContentDao
) : BaseCBViewModel() {

    suspend fun getDownloads(): List<ContentModel> =
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            contentDao.getDownloads(true)
        }

    fun updateContent(section_id: String, lectureContentId: String, s: String) = "Nothing"
}
