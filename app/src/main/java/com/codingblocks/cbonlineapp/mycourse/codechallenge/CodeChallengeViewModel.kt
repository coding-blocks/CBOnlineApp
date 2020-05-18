package com.codingblocks.cbonlineapp.mycourse.codechallenge

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.database.CodeChallengeDao
import com.codingblocks.cbonlineapp.database.models.CodeChallengeModel
import com.codingblocks.cbonlineapp.util.*
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Code_Challenge
import com.codingblocks.onlineapi.models.detailsClass
import com.codingblocks.onlineapi.models.included

class CodeChallengeViewModel(
    handle: SavedStateHandle,
    private val repo: CodeChallengeRepository,
    val prefs: PreferenceHelper) : BaseCBViewModel() {

    var sectionId by savedStateValue<String>(handle, SECTION_ID)
    var contentId by savedStateValue<String>(handle, CONTENT_ID)
    var contestId by savedStateValue<String>(handle, CONTEST_ID)
    var codeId by savedStateValue<String>(handle, CODE_ID)

    var content: MutableLiveData<Code_Challenge> = MutableLiveData()
    var downloadState: MutableLiveData<Boolean> = MutableLiveData()

    fun fetchCodeChallenge() {
        runIO {
            when (val response = repo.fetchCodeChallenge(codeId!!.toInt(), contestId ?: "")) {
                is ResultWrapper.GenericError -> {
                    setError(response.error)
                    if (repo.isDownloaded(codeId!!)){
                        content.postValue(repo.getOfflineContent(codeId!!))
                        downloadState.postValue(true)
                    }
                }
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful){
                        response.value.body()?.let { codeChallenge ->
                            content.postValue(codeChallenge)
                        }
                        downloadState.postValue(repo.isDownloaded(codeId!!))
                    }
                    else {
                        setError(fetchError(response.value.code()))
                        if (repo.isDownloaded(codeId!!)){
                            content.postValue(repo.getOfflineContent(codeId!!))
                            downloadState.postValue(true)
                        }
                    }
                }
            }
        }
    }

    fun saveCode(){
        runIO {
            repo.saveCode(codeId!!,content.value!!)
            downloadState.postValue(true)
        }
    }
}
