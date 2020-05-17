package com.codingblocks.cbonlineapp.mycourse.player

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.switchMap
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.dashboard.doubts.DashboardDoubtsRepository
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.mycourse.player.notes.NotesWorker
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.LIVE
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.ProgressWorker
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.extensions.serializeToJson
import com.codingblocks.cbonlineapp.util.savedStateValue
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Bookmark
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.models.RunAttempts
import com.codingblocks.onlineapi.models.Sections
import java.util.concurrent.TimeUnit
import org.json.JSONObject

const val VIDEO_POSITION = "videoPos"

class VideoPlayerViewModel(
    handle: SavedStateHandle,
    private val repo: VideoPlayerRepository,
    private val repoDoubts: DashboardDoubtsRepository,
    val prefs: PreferenceHelper
) : BaseCBViewModel() {
    var currentOrientation: Int = 0
    var sectionId by savedStateValue<String>(handle, SECTION_ID)
    var attemptId = MutableLiveData<String>()

    var currentVideoId = MutableLiveData<String>()
    var currentContentId by savedStateValue<String>(handle, CONTENT_ID)
    private var currentContentIdLive = handle.getLiveData<String>(CONTENT_ID)
    var position by savedStateValue<Long>(handle, VIDEO_POSITION)
    var currentContentProgress: String = "UNDONE"

    var mOtp: String? = null
    var mPlaybackInfo: String? = null
    var getOtpProgress: MutableLiveData<Boolean> = MutableLiveData()
    var isDownloaded = false

    val doubts = Transformations.distinctUntilChanged(attemptId).switchMap {
        fetchDoubts()
        repoDoubts.getDoubtsByCourseRun(LIVE, it)
    }

    val runAttempts = Transformations.distinctUntilChanged(attemptId).switchMap {
        repoDoubts.getRunAttempt(it)
    }

    val content = Transformations.distinctUntilChanged(currentContentIdLive).switchMap {
        repo.getContent(it)
    }

    val bookmark = Transformations.switchMap(currentContentIdLive) {
        repo.getBookmark(it)
    }

    val notes = Transformations.distinctUntilChanged(attemptId).switchMap {
        fetchNotes()
        repo.getNotes(it)
    }

    val contentList = Transformations.switchMap(attemptId) { attemptId ->
        sectionId?.let { sectionId -> repo.getContents(attemptId, sectionId) }
    }
    val offlineSnackbar = MutableLiveData<String>()

    fun resolveDoubt(doubt: DoubtsModel) {
        runIO {
            when (val response = repoDoubts.resolveDoubt(doubt)) {
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

    private fun fetchDoubts() {
        runIO {
            when (val response = repoDoubts.fetchDoubtsByCourseRun(attemptId.value ?: "")) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            repoDoubts.insertDoubts(it)
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    private fun fetchNotes() {
        runIO {
            when (val response = repo.fetchCourseNotes(attemptId.value ?: "")) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let { notes ->
                            repo.insertNotes(notes)
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun markBookmark() {
        runIO {
            val bookmark = Bookmark(RunAttempts(attemptId.value ?: ""), LectureContent(currentContentId ?: ""), Sections(sectionId?:""))
            when (val response = repo.markDoubt(bookmark)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let { bookmark ->
                            offlineSnackbar.postValue(("Bookmark Added Successfully !"))
                            repo.updateBookmark(bookmark)
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun deleteNote(noteId: String) {
        runIO {
            when (val response = repo.deleteNote(noteId)) {
                is ResultWrapper.GenericError ->
                    if (response.code in 100..103)
                        startWorkerRequest(noteId)
                    else {
                        setError(response.error)
                    }
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        repo.deleteNoteFromDb(noteId)
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    private fun startWorkerRequest(noteId: String = "", noteModel: Note? = null) {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val progressData: Data
        if (noteId.isEmpty()) {
            progressData = workDataOf("NOTE" to noteModel?.serializeToJson())
            offlineSnackbar.postValue("Note will be updated once you connect to Network")
        } else {
            progressData = workDataOf("NOTE_ID" to noteId)
            offlineSnackbar.postValue("Note will be Deleted once you connect to Network")
        }
        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<NotesWorker>()
                .setConstraints(constraints)
                .setInputData(progressData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance()
            .enqueue(request)
    }

    fun updateNote(note: NotesModel) {
        val newNote = Note(note.nttUid, note.duration, note.text, RunAttempts(note.runAttemptId), LectureContent(note.contentId))
        runIO {
            when (val response = repo.updateNote(newNote)) {
                is ResultWrapper.GenericError ->
                    if (response.code in 100..103)
                        startWorkerRequest(noteModel = newNote)
                    else {
                        setError(response.error)
                    }
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        repo.updateNoteInDb(newNote)
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun createNote(note: Note) {
        runIO {
            when (val response = repo.addNote(note)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        offlineSnackbar.postValue(("Note Created Successfully !"))
                        fetchNotes()
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun getOtp() {
        runIO {
            when (val response = repo.getOtp(currentVideoId.value ?: "", attemptId.value ?: "", sectionId
                ?: "")) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let { obj ->
                            mOtp = obj.get("otp")?.asString
                            mPlaybackInfo = obj.get("playbackInfo")?.asString
                            getOtpProgress.postValue(true)
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun removeBookmark() {
        runIO {
            when (val response = bookmark.value?.bookmarkUid?.let { repo.removeBookmark(it) }) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.code() == 204) {
                        offlineSnackbar.postValue(("Removed Bookmark Successfully !"))
                        bookmark.value?.bookmarkUid?.let { repo.deleteBookmark(it) }
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun createDoubt(title: String, body: String, function: (message: String) -> Unit) {
        val doubt = Doubts(null, title, body, RunAttempts(attemptId.value ?: ""), LectureContent(currentContentId
            ?: ""))
        runIO {
            when (val response = repo.addDoubt(doubt)) {
                is ResultWrapper.GenericError -> if (response.code in 100..103)
                    createDoubtOffline(doubtModel = doubt)
                else {
                    setError(response.error)
                }
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        offlineSnackbar.postValue(("Doubt Created Successfully !"))
                        function("")
                        fetchDoubts()
                    } else {
                        try {
                            val jObjError = JSONObject(response.value.errorBody()?.string())
                            val msg = (jObjError.getJSONArray("errors")[0] as JSONObject).getString("detail")
                            function(msg)
                        } catch (e: Exception) {
                        } finally {
                            setError(fetchError(response.value.code()))
                        }
                    }
                }
            }
        }
    }

    private fun createDoubtOffline(doubtModel: Doubts) {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val progressData = workDataOf("DOUBT" to doubtModel.serializeToJson())
        offlineSnackbar.postValue("Doubt will be posted once you connect to Network")
        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<NotesWorker>()
                .setConstraints(constraints)
                .setInputData(progressData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance()
            .enqueue(request)
    }

    fun updateProgress() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val progressData: Data = workDataOf(CONTENT_ID to currentContentId, RUN_ATTEMPT_ID to attemptId.value)
        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<ProgressWorker>()
                .setConstraints(constraints)
                .setInputData(progressData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance()
            .enqueue(request)
    }

    fun updateDownload(status: Int, lectureId: String) = runIO { repo.updateDownload(status, lectureId) }

    /**
     * Function to save player state if current lecture is incomplete
     */
    fun savePlayerState(currentTime: Long, thumbnail: Boolean) {
        runIO {
            attemptId.value?.let { repo.savePlayerState(it, sectionId!!, currentContentId!!, currentTime) }
            if (thumbnail) {
                val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                val thumbnailData: Data = workDataOf(VIDEO_ID to currentVideoId.value, CONTENT_ID to currentContentId)
                val request: OneTimeWorkRequest =
                    OneTimeWorkRequestBuilder<ThumbnailWorker>()
                        .setConstraints(constraints)
                        .setInputData(thumbnailData)
                        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                        .build()

                WorkManager.getInstance().enqueue(request)
            }
        }
    }

    fun deletePlayerState() {
        runIO {
            attemptId.value?.let { repo.deletePlayerState(it) }
        }
    }
}
