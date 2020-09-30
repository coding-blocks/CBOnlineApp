package com.codingblocks.cbonlineapp.mycourse.content.document

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.extensions.savedStateValue
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Bookmark
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.RunAttempts
import com.codingblocks.onlineapi.models.Sections

class PdfViewModel(
    handle: SavedStateHandle,
    private val repo: PdfActivityRepository
) : BaseCBViewModel() {

    var sectionId by savedStateValue<String>(handle, SECTION_ID)
    var contentId by savedStateValue<String>(handle, CONTENT_ID)
    var attempId by savedStateValue<String>(handle, RUN_ATTEMPT_ID)

    val bookmarkLiveData = MutableLiveData<Boolean>()
    val bookmark by lazy {
        repo.getBookmark(contentId!!)
    }
    val bookmarkSnackbar = MutableLiveData<String>()

    fun markBookmark() {
        runIO {
            val bookmark = Bookmark(RunAttempts(attempId!!), LectureContent(contentId!!), Sections(sectionId!!))
            when (val response = repo.addBookmark(bookmark)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let { bookmark ->
                            bookmarkSnackbar.postValue(("Bookmark Added Successfully !"))
                            repo.updateBookmark(bookmark)
                            bookmarkLiveData.postValue(true)
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun getPdf() = liveData {
        val pdfModel = repo.getPdfBookmark(contentId ?: "")
        attempId = pdfModel.attempt_id
        emit(pdfModel)
    }

    fun removeBookmark() {
        runIO {
            val uid = bookmark.value?.bookmarkUid
            when (val response = uid?.let { repo.removeBookmark(it) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.code() == 204) {
                        bookmarkSnackbar.postValue(("Removed Bookmark Successfully !"))
                        repo.deleteBookmark(uid)
                        bookmarkLiveData.postValue(false)
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }
}
