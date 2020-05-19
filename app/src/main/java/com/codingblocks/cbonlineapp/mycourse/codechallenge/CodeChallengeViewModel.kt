package com.codingblocks.cbonlineapp.mycourse.codechallenge

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.util.*
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.CoeChallenge

class CodeChallengeViewModel(
    handle: SavedStateHandle,
    private val repo: CodeChallengeRepository) : BaseCBViewModel() {

    var sectionId by savedStateValue<String>(handle, SECTION_ID)
    var contentId by savedStateValue<String>(handle, CONTENT_ID)
    var contestId by savedStateValue<String>(handle, CONTEST_ID)
    var codeId by savedStateValue<String>(handle, CODE_ID)

    var content: MutableLiveData<CoeChallenge> = MutableLiveData()
    var downloadState: MutableLiveData<Boolean> = MutableLiveData()

    fun fetchCodeChallenge() {
        runIO {
            when (val response = codeId?.toInt()?.let { repo.fetchCodeChallenge(it, contestId ?: "") }) {
                is ResultWrapper.GenericError -> {
                    setError(response.error)
                    getSavedData()
                }
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful){
                        response.value.body().let { codeChallenge ->
                            Log.e("codeChallenge",codeChallenge.toString())
                            content.postValue(codeChallenge)
                        }
                        downloadState.postValue(repo.isDownloaded(codeId!!))
                    }
                    else {
                        setError(fetchError(response.value.code()))
                        getSavedData()
                    }
                }
            }
        }
    }

    fun getSavedData(){
        if (isDownloaded()){
            content.postValue(codeId?.let { repo.getOfflineContent(it) })
            downloadState.postValue(true)
        }
    }

    fun isDownloaded(): Boolean {
        return codeId?.let { repo.isDownloaded(it) }!!
    }

    fun saveCode(){
        runIO {
            codeId?.let { content.value?.let { it1 -> repo.saveCode(it, it1) } }
            downloadState.postValue(true)
        }
    }
}
