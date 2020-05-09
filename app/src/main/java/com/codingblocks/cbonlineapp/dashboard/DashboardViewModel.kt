package com.codingblocks.cbonlineapp.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.course.CourseRepository
import com.codingblocks.cbonlineapp.dashboard.doubts.DashboardDoubtsRepository
import com.codingblocks.cbonlineapp.dashboard.home.DashboardHomeRepository
import com.codingblocks.cbonlineapp.dashboard.mycourses.DashboardMyCoursesRepository
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import com.codingblocks.cbonlineapp.database.models.CourseRunPair
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.util.ALL
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.DoubleTrigger
import com.codingblocks.cbonlineapp.util.extensions.getDistinct
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.savedStateValue
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.getMeta
import com.codingblocks.onlineapi.models.CareerTracks
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Player
import com.onesignal.OneSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class DashboardViewModel(
    handle: SavedStateHandle,
    private val homeRepo: DashboardHomeRepository,
    private val exploreRepo: CourseRepository,
    private val myCourseRepo: DashboardMyCoursesRepository,
    private val repo: DashboardDoubtsRepository,
    val prefs: PreferenceHelper
) : BaseCBViewModel() {
    var isLoggedIn: Boolean? by savedStateValue(handle, LOGGED_IN)

    init {
        Clients.refreshToken = homeRepo.prefs.SP_JWT_REFRESH_TOKEN
        Clients.authJwt = homeRepo.prefs.SP_JWT_TOKEN_KEY
    }

    /**
     * Home Fragment
     */
    fun fetchToken(grantCode: String) {
        runIO {
            when (val response = homeRepo.getToken(grantCode)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            val jwt = it.asJsonObject.get("jwt").asString
                            val rt = it.asJsonObject.get("refresh_token").asString
                            homeRepo.prefs.SP_JWT_TOKEN_KEY = jwt
                            homeRepo.prefs.SP_JWT_REFRESH_TOKEN = rt
                            Clients.authJwt = jwt
                            Clients.refreshToken = rt
                        }
                }
            }
        }
    }

    fun refreshToken() {
        runIO {
            when (val response = homeRepo.refreshToken()) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            val jwt = it.asJsonObject.get("jwt").asString
                            val rt = it.asJsonObject.get("refresh_token").asString
                            homeRepo.prefs.SP_JWT_TOKEN_KEY = jwt
                            homeRepo.prefs.SP_JWT_REFRESH_TOKEN = rt
                            Clients.authJwt = jwt
                            Clients.refreshToken = rt
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun fetchUser() = liveData(Dispatchers.IO) {
        when (val response = homeRepo.fetchUser()) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> {
                if (response.value.isSuccessful)
                    response.value.body()?.let {
                        homeRepo.insertUser(it)
                        delay(2000)
                        emit(it)
                        if (!prefs.SP_PUSH_NOTIFICATIONS)
                            setPlayerId()
                    }
                else {
                    if (response.value.code() == 401)
                        if (prefs.SP_JWT_REFRESH_TOKEN.isNotEmpty()) {
                            refreshToken()
                        } else {
                            setError(fetchError(response.value.code()))
                        }
                }
            }
        }
    }

    private fun setPlayerId() {
        runIO {
            OneSignal.getPermissionSubscriptionState().subscriptionStatus.userId?.let {
                when (val response = homeRepo.updatePlayerId(Player(playerId = it))) {
                    is ResultWrapper.GenericError -> setError(response.error)
                    is ResultWrapper.Success -> with(response.value) {
                        if (isSuccessful) {
                            homeRepo.prefs.SP_PUSH_NOTIFICATIONS = true
                            OneSignal.setExternalUserId(prefs.SP_ONEAUTH_ID)
                        } else {
                            setError(fetchError(code()))
                        }
                    }
                }
            }
            /**
             * Send error to crashlytics if no playerid found for Onesignal
             * */
        }
    }

    var suggestedCourses = MutableLiveData<List<Course>>()
    var trendingCourses = MutableLiveData<List<Course>>()
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

    var tracks = MutableLiveData<List<CareerTracks>>()
    fun fetchTracks() {
        runIO {
            when (val response = exploreRepo.getTracks()) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        tracks.postValue(body())
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    /**
     * My Course Fragment
     */
    val allRuns: LiveData<List<CourseInstructorPair>> by lazy {
        myCourseRepo.getActiveRuns()
    }
    val purchasedRuns: LiveData<List<CourseInstructorPair>> by lazy {
        myCourseRepo.getPurchasedRuns()
    }
    var courseFilter = MutableLiveData<String>()
    val courses: LiveData<List<CourseInstructorPair>> by lazy {
        Transformations.distinctUntilChanged(courseFilter).switchMap { query ->
            myCourseRepo.getMyRuns(query).getDistinct()
        }
    }

    fun fetchMyCourses(offset: String = "0") {
        runIO {
            when (val response = myCourseRepo.fetchMyCourses(offset)) {
                is ResultWrapper.GenericError -> {
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
                            }
                        }
                    else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    /**
     * Home Fragment Top Course Run and Stats
     */

    fun fetchTopRunWithStats() = liveData<CourseRunPair>(Dispatchers.IO) {
        when (val response = homeRepo.fetchLastAccessedRun()) {
            is ResultWrapper.GenericError -> {
                if (response.code in 101..103)
                    emitSource(homeRepo.getTopRun())
                setError(response.error)
            }
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful) {
                    myCourseRepo.insertCourses(listOf(body()!!))
                    emitSource(homeRepo.getTopRunById(body()!!.runAttempts!!.first().id))
                } else {
                    emitSource(MutableLiveData(null))
                    setError(fetchError(code()))
                }
            }
        }
    }

    fun getStats(id: String) {
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

    fun fetchRecentlyPlayed() = liveData(Dispatchers.IO) {
        emitSource(homeRepo.getRecentlyPlayed())
    }

    /**
     * Doubt Variables and functions
     */

    var type: MutableLiveData<String> = MutableLiveData(ALL)
    val attemptId = MutableLiveData<String>()

    val doubts: LiveData<List<DoubtsModel>> by lazy {
        Transformations.distinctUntilChanged(DoubleTrigger(type, attemptId)).switchMap {
            fetchDoubts()
            repo.getDoubtsByCourseRun(it.first, it.second ?: "")
        }
    }

    private fun fetchDoubts() {
        runIO {
            if (!attemptId.value.isNullOrEmpty())
                when (val response = repo.fetchDoubtsByCourseRun(attemptId.value ?: "")) {
                    is ResultWrapper.GenericError -> setError(response.error)
                    is ResultWrapper.Success -> {
                        if (response.value.isSuccessful)
                            response.value.body()?.let {
                                repo.insertDoubts(it)
                            }
                        else {
                            setError(fetchError(response.value.code()))
                        }
                    }
                }
        }
    }

    fun resolveDoubt(doubt: DoubtsModel, saveToDb: Boolean = false) {
        runIO {
            when (val response = repo.resolveDoubt(doubt)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        if (saveToDb) {
                            repo.updateDb(doubt.dbtUid)
                        } else {
                            fetchDoubts()
                        }
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun getPerformance(attemptId: String) = homeRepo.getRunStats(attemptId)
}
