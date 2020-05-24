package com.codingblocks.cbonlineapp.mycourse.document

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.savedStateValue
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Bookmark
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.RunAttempts
import com.codingblocks.onlineapi.models.Sections

class PdfViewModel(handle: SavedStateHandle,
                    private val repo: PdfActivityRepository) : BaseCBViewModel() {

    var sectionId by savedStateValue<String>(handle, SECTION_ID)
    var contentId by savedStateValue<String>(handle, CONTENT_ID)
    var attempId by savedStateValue<String>(handle, RUN_ATTEMPT_ID)
    val bookmark by lazy {
        repo.getBookmark(contentId!!)
    }
    val offlineSnackbar = MutableLiveData<String>()

    fun markBookmark() {
        runIO {
            val bookmark = Bookmark(RunAttempts(attempId!!), LectureContent(contentId!!), Sections(sectionId!!))
            when (val response = repo.addBookmark(bookmark)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let { bookmark ->
                            offlineSnackbar.postValue(("Bookmark Added Successfully !"))
                            repo.updateBookmark(bookmark)
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun removeBookmark() {
        runIO {
            when (val response = bookmark.value?.bookmarkUid?.let { repo.removeBookmark(it) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.code() == 204) {
                        offlineSnackbar.postValue(("Removed Bookmark Successfully !"))
                        bookmark.value?.bookmarkUid?.let { repo.deleteBookmark(it) }
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }
}
