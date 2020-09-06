package com.codingblocks.cbonlineapp.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.work.BackoffPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.course.CourseRepository
import com.codingblocks.cbonlineapp.dashboard.doubts.DashboardDoubtsRepository
import com.codingblocks.cbonlineapp.dashboard.home.DashboardHomeRepository
import com.codingblocks.cbonlineapp.dashboard.mycourses.DashboardMyCoursesRepository
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import com.codingblocks.cbonlineapp.database.models.CourseRunPair
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.util.ALL
import com.codingblocks.cbonlineapp.util.DELETE_DOWNLOADED_VIDEO
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.cbonlineapp.util.extensions.savedStateValue
import com.codingblocks.cbonlineapp.util.livedata.DoubleTrigger
import com.codingblocks.cbonlineapp.util.livedata.getDistinct
import com.codingblocks.cbonlineapp.workers.DeleteDownloadsWorker
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.getMeta
import com.codingblocks.onlineapi.models.CareerTracks
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Player
import com.codingblocks.onlineapi.models.Wishlist
import com.google.common.util.concurrent.ListenableFuture
import com.onesignal.OneSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class DashboardViewModel(
    handle: SavedStateHandle,
    private val homeRepo: DashboardHomeRepository,
    private val exploreRepo: CourseRepository,
    private val myCourseRepo: DashboardMyCoursesRepository,
    private val repo: DashboardDoubtsRepository,
    val prefs: PreferenceHelper
) : BaseCBViewModel() {
    init {
        checkDownloadDataWM()
    }

    var isLoggedIn: Boolean? by savedStateValue(handle, LOGGED_IN)

    /**
     * Home Fragment
     */

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

    /** Function to execute [DeleteDownloadsWorker]*/
    private fun checkDownloadDataWM() {
        val wm = WorkManager.getInstance()

        // Will get if Auto delete downloaded video request is already started or not
        val future: ListenableFuture<List<WorkInfo>> = wm.getWorkInfosByTag(DELETE_DOWNLOADED_VIDEO)
        val list: List<WorkInfo> = future.get()

        // Request to delete video files which expired after 15 days
        val request: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<DeleteDownloadsWorker>(1, TimeUnit.DAYS)
                .addTag(DELETE_DOWNLOADED_VIDEO)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()

        // If found empty then it is not started or cancelled for whatever reason, will add request to start it
        if (list.isEmpty()) {
            wm.enqueue(request)
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
                else {
                    emitSource(MutableLiveData(null))
                }
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

    fun fetchBanner() = liveData {
        when (val response = homeRepo.fetchBanner()) {
            is ResultWrapper.GenericError -> setError(response.error)
            is ResultWrapper.Success -> with(response.value) {
                if (isSuccessful) {
                    emit(body()?.firstOrNull())
                } else {
                    setError(fetchError(code()))
                }
            }
        }
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

    val activePremiumRuns: LiveData<List<CourseInstructorPair>> by lazy {
        myCourseRepo.getPremiumActiveRuns()
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

    var snackbar = MutableLiveData<String>()
    var wishlistLiveData = MutableLiveData<List<Course>>()
    fun fetchWishList() {
        runIO {
            when (val response = homeRepo.fetchWishlist()) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        body()?.let {
                            val wishlist = it.get()
                            val courseList = ArrayList<Course>()
                            for (course in wishlist!!) {
                                courseList.add(course.course!!)
                            }
                            wishlistLiveData.postValue(courseList)
                        }
                    } else
                        setError(fetchError(code()))
                }
            }
        }
    }

    fun changeWishlistStatus(id: String) {
        runIO {
            when (val response = homeRepo.checkWishlisted(id)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        if (response.value.body()?.id != null) {
                            response.value.body()?.let { removeWishlist(it.id) }
                        } else {
                            addWishlist(id)
                        }
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    fun addWishlist(id: String) {
        val course = Wishlist(Course(id))
        runIO {
            when (val response = homeRepo.addWishlist(course)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        snackbar.postValue("Course added to Wishlist")
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun removeWishlist(id: String) {
        runIO {
            when (val response = homeRepo.removeWishlist(id)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        snackbar.postValue("Course removed from Wishlist")
                        fetchWishList()
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }
}
