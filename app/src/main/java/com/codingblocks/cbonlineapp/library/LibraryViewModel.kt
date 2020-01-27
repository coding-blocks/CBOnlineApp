package com.codingblocks.cbonlineapp.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.mycourse.MyCourseRepository
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LibraryViewModel(
    private val repo: LibraryRepository,
    private val courseRepo: MyCourseRepository
) : ViewModel() {
    var attemptId: String = ""
    var type: String = ""
    var name: String = ""
    var errorLiveData: MutableLiveData<String> = MutableLiveData()

    fun fetchNotes(): LiveData<List<NotesModel>> {
        val notes = repo.getNotes(attemptId)
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
        return notes
    }

    fun fetchSections() {
        runIO {
            if (withContext(Dispatchers.IO) { courseRepo.getSectionWithContentNonLive(attemptId) }.isEmpty())
                when (val response = courseRepo.fetchSections(attemptId)) {
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

    private fun setError(error: String) {
        errorLiveData.postValue(error)
    }

    fun fetchBookmarks() = repo.getBookmarks(attemptId)
    fun fetchDownloads() = repo.getDownloads(attemptId)
}
