package com.codingblocks.cbonlineapp.dashboard

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.AppPrefs
import com.codingblocks.cbonlineapp.dashboard.home.DashboardHomeRepository
import com.codingblocks.cbonlineapp.dashboard.mycourses.DashboardMyCoursesRepository
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.getMeta

class DashboardViewModel(private val homeRepo: DashboardHomeRepository,
                         private val myCourseRepo: DashboardMyCoursesRepository,
                         val prefs: AppPrefs) : ViewModel() {
    var courses: MediatorLiveData<List<CourseInstructorPair>> = MediatorLiveData()
    var courseFilter = MutableLiveData<String>()
    var errorLiveData: MutableLiveData<String> = MutableLiveData()
    var isAdmin: MutableLiveData<Boolean> = MutableLiveData()
    val topRun = homeRepo.getTopRun()
    private val runs = myCourseRepo.getMyRuns()
    private val coursesResponse = Transformations.switchMap(courseFilter) { query ->
        myCourseRepo.getMyRuns(query)
    }

    init {
        courses.addSource(coursesResponse) {
            courses.postValue(it)
        }

        courses.addSource(runs) {
            if (it.isNotEmpty()) {
                courses.postValue(it)
                courses.removeSource(runs)
            }
        }
        fetchUser()

    }


    private fun fetchUser() {
        runIO {
            when (val response = homeRepo.fetchUser()) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            homeRepo.insertUser(it)
                            if (it.roleId == 1 || it.roleId == 3) {
                                isAdmin.postValue(true)
                            }

                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun fetchMyCourses(offset: String = "0") {
        runIO {
            when (val response = myCourseRepo.fetchMyCourses(offset)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            myCourseRepo.insertCourses(it.get())
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
