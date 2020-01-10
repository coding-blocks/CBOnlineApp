package com.codingblocks.cbonlineapp.library

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError

class LibraryViewModel(private val repo: LibraryRepository) : ViewModel() {
    var attemptId: String = ""
    val notes by lazy {
        repo.getNotes(attemptId)
    }
    var errorLiveData: MutableLiveData<String> = MutableLiveData()

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
        errorLiveData.postValue(error)
    }
}
