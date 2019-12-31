package com.codingblocks.cbonlineapp.mycourse.player

import androidx.lifecycle.MutableLiveData
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
import com.codingblocks.cbonlineapp.util.LIVE
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.Note
import com.codingblocks.onlineapi.models.RunAttempts
import java.util.concurrent.TimeUnit

class VideoPlayerViewModel(
    private val repo: VideoPlayerRepository,
    private val repoDoubts: DashboardDoubtsRepository
) : ViewModel() {
    var playWhenReady = false
    var currentOrientation: Int = 0
    var mOtp: String? = null
    var mPlaybackInfo: String? = null
    var attemptId: String = ""
    var sectionId: String = ""
    var videoId: String = ""
    var contentId: String = ""
    var getOtpProgress: MutableLiveData<Boolean> = MutableLiveData()
    val doubts by lazy {
        repoDoubts.getDoubtsByCourseRun(LIVE, attemptId)
    }
    val notes by lazy {
        repo.getNotes(attemptId)
    }
    val sectionContentTitle = MutableLiveData<Pair<String, String>>()

    init {
        runIO {
            sectionContentTitle.postValue(repo.getSectionTitle(sectionId, contentId))
        }
    }

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
            when (val response = repoDoubts.fetchDoubtsByCourseRun(attemptId)) {
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
            when (val response = repo.fetchCourseNotes(attemptId)) {
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
        val progressData: Data = if (noteId.isEmpty()) {
            workDataOf("NOTE" to noteModel?.serializeToJson())
        } else {
            workDataOf("NOTE_ID" to noteId)
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
                    if (response.value.isSuccessful)
                        fetchNotes()
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

}
