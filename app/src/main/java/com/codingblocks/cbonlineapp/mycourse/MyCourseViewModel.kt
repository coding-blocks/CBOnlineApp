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
import com.codingblocks.cbonlineapp.database.models.CourseRunPair
import com.codingblocks.cbonlineapp.database.models.RunPerformance
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.PREMIUM
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.extensions.savedStateValue
import com.codingblocks.cbonlineapp.util.livedata.DoubleTrigger
import com.codingblocks.cbonlineapp.workers.ProgressWorker
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Leaderboard
import com.codingblocks.onlineapi.models.ResetRunAttempt
import com.codingblocks.onlineapi.models.SendFeedback
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.TimeUnit

class MyCourseViewModel(
    private val handle: SavedStateHandle,
    private val repo: MyCourseRepository,
    val prefs: PreferenceHelper
) : BaseCBViewModel() {

    var attemptId by savedStateValue<String>(handle, RUN_ATTEMPT_ID)
    var name by savedStateValue<String>(handle, COURSE_NAME)
    var runId by savedStateValue<String>(handle, RUN_ID)
    var premiumRun by savedStateValue<Boolean>(handle, PREMIUM)
    var courseId by savedStateValue<String>(handle, COURSE_ID)

    var progress: MutableLiveData<Boolean> = MutableLiveData()

    /** MutableLiveData Filters for [SectionContentHolder.SectionContentPair]. */
    var filters: MutableLiveData<String> = MutableLiveData()
    var complete: MutableLiveData<String> = MutableLiveData("")
    var content: LiveData<List<SectionContentHolder.SectionContentPair>> = MutableLiveData()

    init {
        getPerformance()
        content = Transformations.switchMap(DoubleTrigger(complete, filters)) {
            attemptId?.let {
                getStats()
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

    val performance: LiveData<RunPerformance>? by lazy {
        attemptId?.let { repo.getRunStats(it) }
    }

    val run: LiveData<CourseRunPair>? by lazy {
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

    fun getLeaderboard() = liveData(Dispatchers.IO) {
        when (val response = repo.fetchLeaderboard(runId!!)) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful) {
                    body()?.let {
                        emit(it)
                    }
                } else {
                    if (code() == 404)
                        emit(emptyList<Leaderboard>())
                    else
                        setError(fetchError(code()))
                }
            }
        }
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
        // Todo - Complete this
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

    private fun getPerformance() {
        runIO {
            val mRank = repo.getHackerBlocksPerformance().value
            when (val response = repo.getPerformance()) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        body()?.let { response ->
                            if (mRank?.currentOverallRank != response.currentOverallRank) {
                                repo.saveRank(response)
                            }
                        }
                    } else {
                        if (code() != 404)
                            setError(fetchError(code()))
                        else {
                            // No HB Report
                        }
                    }
                }
            }
        }
    }

    fun getHackerBlocksPerformance() = repo.getHackerBlocksPerformance()

    fun pauseCourse() = liveData {
        when (val response = repo.pauseCourse(attemptId)) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful) {
                    body()?.let { repo.updateRunAttempt(it) }
                    emit(true)
                } else {
                    errorLiveData.postValue("There was some error")
                }
            }
        }
    }

    fun unPauseCourse() = liveData {
        when (val response = repo.unPauseCourse(attemptId)) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful) {
                    body()?.let { repo.updateRunAttempt(it) }
                    emit(true)
                } else {
                    errorLiveData.postValue("There was some error")
                }
            }
        }
    }

    fun sendFeedback(feedback: SendFeedback) = liveData {
        when (val response = courseId?.let { repo.sendFeedback(it, feedback) }) {
            is ResultWrapper.GenericError -> {
                setError(response.error)
            }
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful) {
                    emit(true)
                } else {
                    errorLiveData.postValue("There was some error")
                }
            }
        }
    }

    fun getFeedback() = liveData {
        when (val response = courseId?.let { repo.getFeedback(it) }) {
            is ResultWrapper.GenericError -> {
                setError(response.error)
            }
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful) {
                    emit(body())
                } else {
                    errorLiveData.postValue("There was some error")
                }
            }
        }
    }

    fun getRunAttempt() = repo.getRunAttempt(attemptId!!)
}

//    fun fetchExtensions(productId: Int): MutableLiveData<List<ProductExtensionsItem>> {
//        CBOnlineLib.api.getExtensions(productId).enqueue(retrofitCallback { throwable, response ->
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
