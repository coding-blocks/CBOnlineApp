package com.codingblocks.cbonlineapp.dashboard.doubts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.util.ALL
import com.codingblocks.cbonlineapp.util.extensions.DoubleTrigger
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError

class DashboardDoubtsViewModel(private val repo: DashboardDoubtsRepository) : ViewModel() {

    private var listDoubtsResponse: LiveData<List<DoubtsModel>> = MutableLiveData()
    var doubts: MediatorLiveData<List<DoubtsModel>> = MediatorLiveData()
    var errorLiveData: MutableLiveData<String> = MutableLiveData()
    var nextOffSet: MutableLiveData<Int> = MutableLiveData(-1)
    var prevOffSet: MutableLiveData<Int> = MutableLiveData(-1)
    var barMessage: MutableLiveData<String> = MutableLiveData()
    var type: MutableLiveData<String> = MutableLiveData()
    var courseId: MutableLiveData<String> = MutableLiveData()
    val default = "44872"
    private val doubtsDao = repo.getDoubtsByCourseRun(type.value ?: ALL, default)

    init {
        listDoubtsResponse = Transformations.switchMap(DoubleTrigger(type, courseId)) {
            fetchDoubts()
            repo.getDoubtsByCourseRun(it.first, it.second ?: default)
        }

        doubts.addSource(listDoubtsResponse) {
            doubts.postValue(it)
        }
        doubts.addSource(doubtsDao) {
            if (it.isNotEmpty()) {
                doubts.postValue(it)
                doubts.removeSource(doubtsDao)
            }
        }
    }

    fun fetchDoubts() {
        runIO {
            when (val response = repo.fetchDoubtsByCourseRun(courseId.value ?: default)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            repo.insertDoubts(it)
                            if (it.isEmpty())
                                doubts.postValue(emptyList())
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

    fun resolveDoubt(doubt: DoubtsModel) {
        runIO {
            when (val response = repo.resolveDoubt(doubt)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        fetchDoubts()
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun getRunId() = repo.getRuns()

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
}
