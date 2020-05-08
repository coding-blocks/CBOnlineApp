package com.codingblocks.cbonlineapp.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.mycourse.MyCourseRepository
import com.codingblocks.cbonlineapp.mycourse.player.notes.NotesWorker
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.TYPE
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.extensions.serializeToJson
import com.codingblocks.cbonlineapp.util.savedStateValue
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Note
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LibraryViewModel(
    private val handle: SavedStateHandle,
    private val repo: LibraryRepository,
    private val courseRepo: MyCourseRepository
) : BaseCBViewModel() {
    var attemptId: String? by savedStateValue(handle, RUN_ATTEMPT_ID)
    var type: String? by savedStateValue(handle, TYPE)
    var name: String? by savedStateValue(handle, COURSE_NAME)

    fun fetchNotes(): LiveData<List<NotesModel>> {
        val notes = repo.getNotes(attemptId!!)
        runIO {
            when (val response = repo.fetchCourseNotes(attemptId!!)) {
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
        return notes
    }

    fun fetchSections() {
        runIO {
            if (withContext(Dispatchers.IO) { courseRepo.getSectionWithContentNonLive(attemptId!!) }.isEmpty())
                when (val response = courseRepo.fetchSections(attemptId!!)) {
                    is ResultWrapper.GenericError -> setError(response.error)
                    is ResultWrapper.Success -> {
                        if (response.value.isSuccessful)
                            response.value.body()?.let { runAttempt ->
                                courseRepo.insertSections(runAttempt)
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
//            offlineSnackbar.postValue("Note will be updated once you connect to Network")
        } else {
            progressData = workDataOf("NOTE_ID" to noteId)
//            offlineSnackbar.postValue("Note will be Deleted once you connect to Network")
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

    fun fetchBookmarks() = repo.getBookmarks(attemptId!!)
    fun fetchDownloads() = repo.getDownloads(attemptId!!)
    fun updateDownload(status: Int, lectureId: String) = runIO { repo.updateDownload(status, lectureId) }

    fun removeBookmark(id: String) {
        runIO {
            when (val response = repo.removeBookmark(id)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.code() == 204) {
                        repo.deleteBookmark(id)
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }
}
