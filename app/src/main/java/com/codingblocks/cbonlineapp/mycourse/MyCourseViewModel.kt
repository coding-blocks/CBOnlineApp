package com.codingblocks.cbonlineapp.mycourse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.ProgressWorker
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SingleLiveEvent
import com.codingblocks.cbonlineapp.util.extensions.DoubleTrigger
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.ProductExtensionsItem
import com.codingblocks.onlineapi.models.ResetRunAttempt
import java.util.concurrent.TimeUnit

class MyCourseViewModel(
    private val repo: MyCourseRepository
) : ViewModel() {

    var progress: MutableLiveData<Boolean> = MutableLiveData()
    var revoked: MutableLiveData<Boolean> = MutableLiveData()
    lateinit var runStartEnd: Pair<Long, Long>
    var attemptId: String = ""
    var runId: String = ""
    var courseId: String = ""
    var name: String = ""
    private val mutablePopMessage = SingleLiveEvent<String>()
    private val extensions = MutableLiveData<List<ProductExtensionsItem>>()
    val popMessage: LiveData<String> = mutablePopMessage
    var filters: MutableLiveData<String> = MutableLiveData()
    var complete: MutableLiveData<String> = MutableLiveData("")
    var content: LiveData<List<SectionContentHolder.SectionContentPair>> = MutableLiveData()
    var errorLiveData: MutableLiveData<String> = MutableLiveData()

    init {
        content = Transformations.switchMap(DoubleTrigger(complete, filters)) {
            repo.getSectionWithContent(attemptId)
        }
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

    fun getRun() = repo.getRunById(attemptId)

    fun getPerformance() = repo.getRunStats(attemptId)

    fun resetProgress(): MutableLiveData<Boolean> {
        val resetProgress = MutableLiveData<Boolean>()
        val resetCourse = ResetRunAttempt(attemptId)
        runIO {
            when (val response = repo.resetProgress(resetCourse)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        resetProgress.postValue(true)
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
        return resetProgress
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

    fun getStats(id: String = attemptId) {
        runIO {
            when (val response = repo.getStats(id)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        body()?.let { response ->
                            repo.saveStats(response, id)
                        }
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    fun getNextContent() = repo.getNextContent(attemptId)

    fun updateProgress(contentId: String) {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val progressData: Data = workDataOf(CONTENT_ID to contentId, RUN_ATTEMPT_ID to attemptId)
        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<ProgressWorker>()
                .setConstraints(constraints)
                .setInputData(progressData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance()
            .enqueue(request)
    }
}
