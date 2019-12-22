package com.codingblocks.cbonlineapp.dashboard.mycourses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.getMeta

class DashboardMyCoursesViewModel(
    private val repo: DashboardMyCoursesRepository
) : ViewModel() {
    private var coursesResponse: LiveData<List<CourseInstructorPair>> = MutableLiveData()
    var courses: MediatorLiveData<List<CourseInstructorPair>> = MediatorLiveData()
    var courseFilter = MutableLiveData<String>()
    var errorLiveData: MutableLiveData<String> = MutableLiveData()
    var nextOffSet: MutableLiveData<Int> = MutableLiveData(-1)
    var prevOffSet: MutableLiveData<Int> = MutableLiveData(-1)

    init {
        coursesResponse = Transformations.switchMap(courseFilter) { query ->
            repo.getMyRuns(query)
        }
        courses.addSource(coursesResponse) {
            courses.postValue(it)
        }

        courses.addSource(repo.getMyRuns()) {
            if (it.isNotEmpty()) {
                courses.postValue(it)
                courses.removeSource(repo.getMyRuns())
            }
        }

    }

    fun fetchMyCourses(offset: String = "0") {
        runIO {
            when (val response = repo.fetchMyCourses(offset)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            repo.insertCourses(it.get())
                            val currentOffSet = getMeta(it.meta, "currentOffset").toString()
                            val nextOffSet = getMeta(it.meta, "nextOffset").toString()
                            if (currentOffSet != nextOffSet) {
                                fetchMyCourses(nextOffSet)
                                if (it.get().isEmpty())
                                    courses.postValue(emptyList())
                            }


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
