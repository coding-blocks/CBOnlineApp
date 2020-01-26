package com.codingblocks.cbonlineapp.dashboard.doubts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.util.ALL
import com.codingblocks.cbonlineapp.util.extensions.DoubleTrigger
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Comment
import com.codingblocks.onlineapi.models.Doubts

class DashboardDoubtsViewModel(private val repo: DashboardDoubtsRepository) : ViewModel() {

    var errorLiveData: MutableLiveData<String> = MutableLiveData()
    var barMessage: MutableLiveData<String> = MutableLiveData()
    var type: MutableLiveData<String> = MutableLiveData(ALL)
    var attemptId: MutableLiveData<String> = MutableLiveData()

    val doubts by lazy {
        Transformations.switchMap(DoubleTrigger(type, attemptId)) {
            fetchDoubts()
            repo.getDoubtsByCourseRun(it.first, it.second ?: "")
        }
    }

    fun fetchDoubts() {
        runIO {
            if (!attemptId.value.isNullOrEmpty())
                when (val response = repo.fetchDoubtsByCourseRun(attemptId.value ?: "")) {
                    is ResultWrapper.GenericError -> setError(response.error)
                    is ResultWrapper.Success -> {
                        if (response.value.isSuccessful)
                            response.value.body()?.let {
                                repo.insertDoubts(it)
                            }
                        else {
                            setError(fetchError(response.value.code()))
                        }
                    }
                }
        }
    }

    private fun setError(error: String) {
        errorLiveData.postValue(error)
    }

    fun resolveDoubt(doubt: DoubtsModel, saveToDb: Boolean = false) {
        runIO {
            when (val response = repo.resolveDoubt(doubt)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        if (saveToDb) {
                            repo.updateDb(doubt.dbtUid)
                        } else {
                            fetchDoubts()
                        }
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

//    fun getRunId() = repo.getRuns()

    fun getDoubt(doubtId: String): LiveData<DoubtsModel> {
        fetchComments(doubtId)
        return repo.getDoubtById(doubtId)
    }

    fun getComments(doubtId: String) = repo.getCommentsById(doubtId)

    private fun fetchComments(doubtId: String) {
        runIO {
            when (val response = repo.fetchCommentsByDoubtId(doubtId)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        response.value.body()?.let { repo.insertComments(it) }
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun createComment(body: String, doubtId: String, discourseTopicId: String) {
        runIO {
            val comment = Comment(body, discourseTopicId = discourseTopicId, doubt = Doubts(doubtId))
            when (val response = repo.createComment(comment)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        response.value.body()?.let { repo.insertComment(it) }
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun getRuns() = repo.getRuns()
}
