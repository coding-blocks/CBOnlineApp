package com.codingblocks.cbonlineapp.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.course.CourseRepository
import com.codingblocks.cbonlineapp.dashboard.home.DashboardHomeRepository
import com.codingblocks.cbonlineapp.dashboard.mycourses.DashboardMyCoursesRepository
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.getMeta
import com.codingblocks.onlineapi.models.Course
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DashboardViewModel(
    private val homeRepo: DashboardHomeRepository,
    private val exploreRepo: CourseRepository,
    private val myCourseRepo: DashboardMyCoursesRepository
) : ViewModel() {
    var courseFilter = MutableLiveData<String>()
    var isAdmin: MutableLiveData<Boolean> = MutableLiveData()
    var isLoggedIn: MutableLiveData<Boolean> = MutableLiveData()
    var suggestedCourses = MutableLiveData<List<Course>>()
    var trendingCourses = MutableLiveData<List<Course>>()
    val added = MutableLiveData<Boolean>()

    val courses by lazy {
        Transformations.switchMap(courseFilter) { query ->
            myCourseRepo.getMyRuns(query)
        }
    }
    val attemptId = MutableLiveData<String>()
    val topRun by lazy {
        homeRepo.getTopRun()
    }

    val runPerformance = Transformations.switchMap(attemptId) { query ->
        homeRepo.getRunStats(query)
    }
    val allRuns by lazy {
        myCourseRepo.getMyRuns("")
    }
    var errorLiveData: MutableLiveData<String> = MutableLiveData()

    init {
        fetchUser()
    }

    fun fetchToken(grantCode: String) {
        runIO {
            when (val response = homeRepo.getToken(grantCode)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            val jwt = it.asJsonObject.get("jwt").asString
                            val rt = it.asJsonObject.get("refresh_token").asString
                            Clients.authJwt = jwt
                            Clients.refreshToken = rt
                        }
                }
            }
        }
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

    fun fetchRecommendedCourses(offset: Int, page: Int) {
        runIO {
            when (val response = exploreRepo.getSuggestedCourses(offset, page)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        if (offset == 0)
                            suggestedCourses.postValue(body())
                        else
                            trendingCourses.postValue(body())
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    fun fetchMyCourses(offset: String = "0") {
        runIO {
            when (val response = myCourseRepo.fetchMyCourses(offset)) {
                is ResultWrapper.GenericError -> {
                    added.postValue(true)
                    setError(response.error)
                }
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            withContext(Dispatchers.Default) {
                                myCourseRepo.insertCourses(it.get() ?: emptyList())
                            }
                            val currentOffSet = getMeta(it.meta, "currentOffset").toString()
                            val nextOffSet = getMeta(it.meta, "nextOffset").toString()
                            if (currentOffSet != nextOffSet && nextOffSet != "null") {
                                fetchMyCourses(nextOffSet)
                            } else {
                                added.postValue(true)
                            }
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun getStats(id: String) {
        attemptId.postValue(id)
        runIO {
            when (val response = homeRepo.getStats(id)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        body()?.let { response ->
                            homeRepo.saveStats(response, id)
                        }
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    private fun setError(error: String) {
        errorLiveData.postValue(error)
    }
}
