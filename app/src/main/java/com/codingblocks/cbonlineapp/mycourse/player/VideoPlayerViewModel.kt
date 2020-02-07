package com.codingblocks.cbonlineapp.mycourse.player

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
import com.codingblocks.cbonlineapp.dashboard.doubts.DashboardDoubtsRepository
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.mycourse.player.notes.NotesWorker
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.LIVE
import com.codingblocks.cbonlineapp.util.ProgressWorker
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Bookmark
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.models.RunAttempts
import com.codingblocks.onlineapi.models.Sections
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class VideoPlayerViewModel(
    private val repo: VideoPlayerRepository,
    private val repoDoubts: DashboardDoubtsRepository
) : ViewModel() {
    var contentLength: Long = 0L
    var playWhenReady = false
    var currentOrientation: Int = 0
    var mOtp: String? = null
    var mPlaybackInfo: String? = null
    var attemptId = MutableLiveData<String>()
    var sectionId = ""
    var videoId: String = ""
    var contentId: String = ""
    var getOtpProgress: MutableLiveData<Boolean> = MutableLiveData()

    val doubts = Transformations.switchMap(attemptId) {
        fetchDoubts()
        repoDoubts.getDoubtsByCourseRun(LIVE, it)
    }
    val content by lazy {
        repo.getContent(contentId)
    }

    val notes = Transformations.switchMap(attemptId) {
        fetchNotes()
        repo.getNotes(it)
    }

    val bookmark by lazy {
        repo.getBookmark(contentId)
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

    fun fetchDoubts() {
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

    fun fetchNotes() {
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
            val bookmark = Bookmark(RunAttempts(attemptId.value
                ?: ""), LectureContent(contentId), Sections(sectionId ?: ""))
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

    private fun setError(error: String) {
//        errorLiveData.postValue(error)
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
            when (val response = repo.getOtp(videoId, attemptId.value ?: "", sectionId ?: "")) {
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
        val doubt = Doubts(null, title, body, RunAttempts(attemptId.value ?: ""), LectureContent(contentId))
        runIO {
            when (val response = repo.addDoubt(doubt)) {
                is ResultWrapper.GenericError -> setError(response.error)
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

    fun updateProgress() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val progressData: Data = workDataOf(CONTENT_ID to contentId, RUN_ATTEMPT_ID to attemptId.value)
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
