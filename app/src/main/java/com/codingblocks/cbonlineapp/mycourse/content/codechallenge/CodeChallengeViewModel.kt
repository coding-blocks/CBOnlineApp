package com.codingblocks.cbonlineapp.mycourse.content.codechallenge

import androidx.lifecycle.*
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.*
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.extensions.savedStateValue
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Bookmark
import com.codingblocks.onlineapi.models.CodeChallenge
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.RunAttempts
import com.codingblocks.onlineapi.models.Sections
import kotlinx.coroutines.Dispatchers

class CodeChallengeViewModel(
    handle: SavedStateHandle,
    private val repo: CodeChallengeRepository
) : BaseCBViewModel() {

    var sectionId by savedStateValue<String>(handle, SECTION_ID)
    var contentId by savedStateValue<String>(handle, CONTENT_ID)
    var contestId by savedStateValue<String>(handle, CONTEST_ID)
    var codeId by savedStateValue<String>(handle, CODE_ID)
    var attempId by savedStateValue<String>(handle, RUN_ATTEMPT_ID)

    var downloadState: MutableLiveData<Boolean> = MutableLiveData()
    var codeChallenge: CodeChallenge? = null
    val offlineSnackbar = MutableLiveData<String>()
    val bookmarkLiveData = MutableLiveData<Boolean>()

    val getBookmark by lazy {
        repo.getBookmark(contentId!!)
    }

    fun fetchCodeChallenge() = liveData(Dispatchers.IO) {
        val codeModel = contentId?.let { repo.getCodeId(it) }
        attempId = codeModel?.attempt_id
        codeId = codeModel?.codeUid
        contestId = codeModel?.codeContestId.toString()
        when (val response = codeId?.toInt()?.let { repo.fetchCodeChallenge(it, contestId ?: "") }) {
            is ResultWrapper.GenericError -> {
                setError(response.error)
                if (codeId?.let { repo.isDownloaded(it) }!!) {
                    downloadState.postValue(true)
                    emit(codeId?.let { repo.getOfflineContent(it) })
                }
            }
            is ResultWrapper.Success -> {
                if (response.value.isSuccessful) {
                    response.value.body()?.let {
                        emit(it)
                        codeChallenge = it
                    }
                    downloadState.postValue(repo.isDownloaded(codeId!!))
                } else {
                    setError(fetchError(response.value.code()))
                    if (codeId?.let { repo.isDownloaded(it) }!!) {
                        downloadState.postValue(true)
                        emit(codeId?.let { repo.getOfflineContent(it) })
                    }
                }
            }
        }
    }

    fun saveCode() {
        runIO {
            codeId?.let { codeId -> codeChallenge?.let { codeContent -> repo.saveCode(codeId, codeContent) } }
            downloadState.postValue(true)
        }
    }

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
                            bookmarkLiveData.postValue(true)
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun removeBookmark() {
        val uid = getBookmark.value?.bookmarkUid
        runIO {
            when (val response = uid?.let { repo.removeBookmark(it) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.code() == 204) {
                        offlineSnackbar.postValue(("Removed Bookmark Successfully !"))
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
