package com.codingblocks.cbonlineapp.dashboard.mycourses

import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError

class DashboardMyCoursesViewModel(
    private val repo: DashboardMyCoursesRepository
) : ViewModel() {
    init {
        fetchMyCourses()
    }

    fun fetchMyCourses() {
        runIO {
            runIO {
                when (val response = repo.fetchMyCourses()) {
                    is ResultWrapper.GenericError -> setError(response.error)
                    is ResultWrapper.Success -> {
                        if (response.value.isSuccessful)
                            response.value.body()?.let {
                                repo.insertCourses(it.get())
                            }
                        else {
                            setError(fetchError(response.value.code()))
                        }
                    }
                }
            }
        }
    }


    private fun setError(error: String) {
//        errorLiveData.postValue(error)
    }
}
