package com.codingblocks.cbonlineapp.mycourse

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.ProgressWorker
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.util.extensions.DoubleTrigger
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.savedStateValue
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.ResetRunAttempt
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers

class MyCourseViewModel(
    private val handle: SavedStateHandle,
    private val repo: MyCourseRepository
) : BaseCBViewModel() {

    var attemptId by savedStateValue<String>(handle, RUN_ATTEMPT_ID)
    var name by savedStateValue<String>(handle, COURSE_NAME)
    var runId by savedStateValue<String>(handle, RUN_ID)

    var progress: MutableLiveData<Boolean> = MutableLiveData()

    /** MutableLiveData Filters for [SectionContentHolder.SectionContentPair]. */
    var filters: MutableLiveData<String> = MutableLiveData()
    var complete: MutableLiveData<String> = MutableLiveData("")
    var content: LiveData<List<SectionContentHolder.SectionContentPair>> = MutableLiveData()

    init {
        getStats()
        content = Transformations.switchMap(DoubleTrigger(complete, filters)) {
            attemptId?.let {
                repo.getSectionWithContent(it)
            }
        }
    }

    var runStartEnd: Pair<Long, Long>
        get() {
            return handle["runStartEnd"] ?: Pair(0L, 0L)
        }
        set(value) {
            handle.set("runStartEnd", value)
        }

    val performance by lazy {
        attemptId?.let { repo.getRunStats(it) }
    }

    val run by lazy {
        attemptId?.let { repo.getRunById(it) }
    }

    fun fetchSections(refresh: Boolean = false) {
        runIO {
            when (val response = attemptId?.let { repo.fetchSections(it) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let { runAttempt ->
                            repo.insertSections(runAttempt, refresh)
                            progress.postValue(false)
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun getStats() {
        runIO {
            when (val response = attemptId?.let { repo.getStats(it) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        body()?.let { response ->
                            attemptId?.let { repo.saveStats(response, it) }
                        }
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    val resetProgress = liveData(Dispatchers.IO) {
        when (val response = attemptId?.let { ResetRunAttempt(it) }?.let { repo.resetProgress(it) }) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> {
                if (response.value.isSuccessful)
                    emit(true)
                else {
                    setError(fetchError(response.value.code()))
                }
            }
        }
    }

    val nextContent by lazy {
        attemptId?.let { repo.getNextContent(it) }
    }

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

    fun addToCart() = liveData(Dispatchers.IO) {
        when (val response = runId?.let { repo.addToCart(it) }) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful) {
                    emit(true)
                } else {
                    setError(fetchError(code()))
                }
            }
        }
    }

    fun requestMentorApproval() {
        runIO {
            when (val response = attemptId?.let { repo.requestApproval(it) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (!isSuccessful) setError(fetchError(code()))
                }
            }
        }
    }

    fun updateRunAttempt() {
        runIO {
            when (val response = attemptId?.let { repo.fetchSections(it) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (!isSuccessful) setError(fetchError(code()))
                }
            }
        }
    }

    fun requestGoodies(name: String, address: String, postal: String, mobile: String?) {
        runIO {
            when (val response = attemptId?.let { repo.fetchSections(it) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (!isSuccessful) setError(fetchError(code()))
                }
            }
        }
    }

    fun downloadCertificateAndShow(context: Context, certificateUrl: String, fileName: String) {
        if (certificateUrl.isNullOrEmpty() || fileName.isNullOrEmpty()) {
            Toast.makeText(context, "Error fetching document", Toast.LENGTH_SHORT).show()
        } else {
            val uri = Uri.parse(certificateUrl)
            val request = DownloadManager.Request(uri)
            request.setMimeType("application/pdf")
            request.setTitle("$fileName.pdf")
            request.setDescription("Downloading attachment..")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        }
    }
}

//    fun requestApproval() {
//        Clients.api.requestApproval(attemptId).enqueue(retrofitCallback { throwable, response ->
//            response.let {
//                if (it?.isSuccessful == true) {
//                    mutablePopMessage.value = it.body()?.string()
//                } else {
//                    mutablePopMessage.value = it?.errorBody()?.string()
//                }
//            }
//            throwable.let {
//                mutablePopMessage.value = it?.message
//            }
//        })
//    }
//
//    fun fetchExtensions(productId: Int): MutableLiveData<List<ProductExtensionsItem>> {
//        Clients.api.getExtensions(productId).enqueue(retrofitCallback { throwable, response ->
//            response?.body().let { list ->
//                if (response?.isSuccessful == true) {
//                    extensions.postValue(list?.productExtensions)
//                }
//            }
//            throwable.let {
//                mutablePopMessage.value = it?.message
//            }
//        })
//        return extensions
//    }
