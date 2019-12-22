package com.codingblocks.cbonlineapp.mycourse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder
import com.codingblocks.cbonlineapp.util.SingleLiveEvent
import com.codingblocks.cbonlineapp.util.extensions.DoubleTrigger
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.ProductExtensionsItem
import com.codingblocks.onlineapi.models.ResetRunAttempt
import com.codingblocks.onlineapi.safeApiCall

class MyCourseViewModel(
    private val repo: MyCourseRepository
) : ViewModel() {

    var progress: MutableLiveData<Boolean> = MutableLiveData()
    var revoked: MutableLiveData<Boolean> = MutableLiveData()
    var expired: SingleLiveEvent<Boolean> = SingleLiveEvent()
    var attemptId: String = ""
    var runId: String = ""
    var courseId: String = ""
    private val mutablePopMessage = SingleLiveEvent<String>()
    private val extensions = MutableLiveData<List<ProductExtensionsItem>>()
    val popMessage: LiveData<String> = mutablePopMessage
    var resetProgress: MutableLiveData<Boolean> = MutableLiveData()
    var filters: MutableLiveData<String> = MutableLiveData()
    var complete: MutableLiveData<String> = MutableLiveData("")
    var content: LiveData<List<SectionContentHolder.SectionContentPair>> = MutableLiveData()
    var errorLiveData: MutableLiveData<String> = MutableLiveData()

    init {
        content = Transformations.switchMap(DoubleTrigger(complete, filters)) {
            repo.getSectionWithContent(attemptId)
        }
    }

    fun getInstructor() = repo.getInstructorWithCourseId(courseId)

    fun getResumeCourse() = repo.resumeCourse(attemptId)

    fun getRun() = repo.run(runId)

    fun updateHit(attemptId: String) = runIO {
        repo.updateHit(attemptId)
    }

    fun fetchSections() {
        runIO {
            when (val response = repo.fetchSections(attemptId)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let { runAttempt ->
                            repo.insertSections(runAttempt)
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

    fun resetProgress() {
        val resetCourse = ResetRunAttempt(attemptId)
        Clients.api.resetProgress(resetCourse).enqueue(retrofitCallback { _, response ->
            resetProgress.value = response?.isSuccessful ?: false
        })
    }

    fun requestApproval() {
        Clients.api.requestApproval(attemptId).enqueue(retrofitCallback { throwable, response ->
            response.let {
                if (it?.isSuccessful == true) {
                    mutablePopMessage.value = it.body()?.string()
                } else {
                    mutablePopMessage.value = it?.errorBody()?.string()
                }
            }
            throwable.let {
                mutablePopMessage.value = it?.message
            }
        })
    }

    fun fetchExtensions(productId: Int): MutableLiveData<List<ProductExtensionsItem>> {
        Clients.api.getExtensions(productId).enqueue(retrofitCallback { throwable, response ->
            response?.body().let { list ->
                if (response?.isSuccessful == true) {
                    extensions.postValue(list?.productExtensions)
                }
            }
            throwable.let {
                mutablePopMessage.value = it?.message
            }
        })
        return extensions
    }
}
