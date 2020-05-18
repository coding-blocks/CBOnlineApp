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
    private val codeDao: CodeChallengeDao,
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
                    if (isDownloaded()){
                        content.postValue(getOfflineContent())
                        downloadState.postValue(true)
                    }
                }
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful){
                        response.value.body()?.let { codeChallenge ->
                            content.postValue(codeChallenge)
                        }
                        downloadState.postValue(isDownloaded())
                    }
                    else {
                        setError(fetchError(response.value.code()))
                        if (isDownloaded()){
                            content.postValue(getOfflineContent())
                            downloadState.postValue(true)
                        }
                    }
                }
            }
        }
    }

    fun getOfflineContent(): Code_Challenge? {
        val model: CodeChallengeModel = codeDao.getCodeChallengeById(codeId!!)

        val challenge = Code_Challenge(
            model.title,
            included(
                model.difficulty,
                model.title,
                detailsClass(
                    model.constraints,
                    model.explanation,
                    model.input_format,
                    model.sample_input,
                    model.output_format,
                    model.sample_output,
                    model.description
                )
            )
        )
        return challenge
    }

    fun isDownloaded(): Boolean {
        return codeId?.let { codeDao.getCodeChallengeById(it) } != null
    }

    fun saveCode() {
        runIO {
            val newCode: CodeChallengeModel = codeId!!.let {
                CodeChallengeModel(
                    it,
                    content.value!!.name,
                    content.value!!.content!!.details!!.constraints,
                    content.value!!.content!!.details!!.explanation,
                    content.value!!.content!!.details!!.input_format,
                    content.value!!.content!!.details!!.sample_input,
                    content.value!!.content!!.details!!.output_format,
                    content.value!!.content!!.details!!.sample_output,
                    content.value!!.content!!.details!!.description
                )
            }


            val oldModel: CodeChallengeModel? = codeId?.let { codeDao.getCodeChallengeById(it) }
            if (oldModel != null && !oldModel.sameAndEqual(newCode)) {
                codeDao.update(newCode)
            } else {
                codeDao.insertNew(
                    newCode
                )
            }
            downloadState.postValue(true)
        }
    }
}
