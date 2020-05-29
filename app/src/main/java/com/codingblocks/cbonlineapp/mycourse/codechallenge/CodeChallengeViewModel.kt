package com.codingblocks.cbonlineapp.mycourse.codechallenge


import androidx.lifecycle.*
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.*
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.CodeChallenge
import kotlinx.coroutines.Dispatchers

class CodeChallengeViewModel(
    handle: SavedStateHandle,
    private val repo: CodeChallengeRepository) : BaseCBViewModel() {

    var sectionId by savedStateValue<String>(handle, SECTION_ID)
    var contentId by savedStateValue<String>(handle, CONTENT_ID)
    var contestId by savedStateValue<String>(handle, CONTEST_ID)
    var codeId by savedStateValue<String>(handle, CODE_ID)

    var downloadState: MutableLiveData<Boolean> = MutableLiveData()
    var codeChallenge: CodeChallenge? = null

    fun fetchCodeChallenge() = liveData(Dispatchers.IO) {
        when (val response = codeId?.toInt()?.let { repo.fetchCodeChallenge(it, contestId ?: "") }) {
            is ResultWrapper.GenericError -> {
                setError(response.error)
                if (codeId?.let { repo.isDownloaded(it) }!!){
                    downloadState.postValue(true)
                    emit(codeId?.let { repo.getOfflineContent(it) } )
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
                    if (codeId?.let { repo.isDownloaded(it) }!!){
                        downloadState.postValue(true)
                        emit(codeId?.let { repo.getOfflineContent(it) } )
                    }
                }
            }
        }
    }

    fun saveCode() {
        runIO {
            codeId?.let { codeId -> codeChallenge?.let {codeContent-> repo.saveCode(codeId, codeContent) } }
            downloadState.postValue(true)
        }
    }
}
