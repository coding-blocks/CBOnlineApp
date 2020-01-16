package com.codingblocks.cbonlineapp.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError

class LibraryViewModel(private val repo: LibraryRepository) : ViewModel() {
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

    private fun setError(error: String) {
        errorLiveData.postValue(error)
    }

    fun fetchBookmarks() = repo.getBookmarks(attemptId)
}
