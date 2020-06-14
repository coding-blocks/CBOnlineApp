package com.codingblocks.cbonlineapp.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.baseclasses.STATE
import com.codingblocks.cbonlineapp.course.adapter.CourseDataSource
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Project
import com.codingblocks.onlineapi.models.Sections
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.onlineapi.models.User
import com.codingblocks.onlineapi.models.Wishlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class CourseViewModel(
    private val repo: CourseRepository,
    val prefs: PreferenceHelper
) : BaseCBViewModel() {
    lateinit var id: String
    var isLoggedIn: Boolean = false
    var course = MutableLiveData<Course>()
    var suggestedCourses = MutableLiveData<List<Course>>()
    val findCourses = MutableLiveData<List<Course>>()
    val projects = MutableLiveData<List<Project>>()
    val sections = MutableLiveData<List<Sections>>()
    val wishlistUpdated = MutableLiveData<Boolean>()
    val snackbar = MutableLiveData<String>()
    private val allCourse = arrayListOf<Course>()

    var image: MutableLiveData<String> = MutableLiveData()
    var name: MutableLiveData<String> = MutableLiveData()
    var addedToCartProgress: MutableLiveData<STATE> = MutableLiveData()
    var enrollTrialProgress: MutableLiveData<STATE> = MutableLiveData()

    fun fetchCourse() {
        runIO {
            when (val response = repo.getCourse(id)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        if (isLoggedIn)
                            checkIfCourseWishlisted(body())
                        else
                            course.postValue(body())
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
        fetchAllCourses()
    }

    fun fetchAllCourses() {
        runIO {
            when (val response = repo.getAllCourses("0")) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            it.get()?.let { it1 -> allCourse.addAll(it1) }
                            suggestedCourses.postValue(allCourse)
                        }
                }
            }
        }
    }

    fun searchCourses(query: String) {
        runIO {
            when (val response = repo.findCourses(query)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        findCourses.postValue(body())
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    fun fetchProjects(projectIdList: ArrayList<Project>?) {
        val list = arrayListOf<Project>()
        if (!projectIdList.isNullOrEmpty()) {
            runIO {
                val projectList = projectIdList.map {
                    async(Dispatchers.IO) { repo.getProjects(it.id) } // runs in parallel in background thread
                }.awaitAll()
                projectList.forEach {
                    when (it) {
                        is ResultWrapper.GenericError -> setError(it.error)
                        is ResultWrapper.Success -> with(it.value) {
                            if (isSuccessful) {
                                body()?.let { it1 -> list.add(it1) }
                            } else {
                                setError(fetchError(code()))
                            }
                        }
                    }
                }.also {
                    projects.postValue(list)
                }
            }
        } else {
            projects.postValue(emptyList())
        }
    }

    // Todo - Improvise this
    fun fetchSections(sectionIdList: ArrayList<Sections>) {
        val list = arrayListOf<Sections>()
        if (!sectionIdList.isNullOrEmpty()) {
            runIO {
                val sectionList = sectionIdList.map {
                    async(Dispatchers.IO) { repo.getSection(it.id) } // runs in parallel in background thread
                }.awaitAll()
                sectionList.forEach {
                    when (it) {
                        is ResultWrapper.GenericError -> setError(it.error)
                        is ResultWrapper.Success -> with(it.value) {
                            if (isSuccessful) {
                                body()?.let { it1 -> list.add(it1) }
                            } else {
                                setError(fetchError(code()))
                            }
                        }
                    }
                }.also {
                    sections.postValue(list)
                }
            }
        }
    }

    fun clearCart(id: String) {
        addedToCartProgress.postValue(STATE.LOADING)
        runIO {
            when (val response = repo.clearCart()) {
                is ResultWrapper.GenericError -> {
                    enrollTrialProgress.postValue(STATE.ERROR)
                    addToCart(id)
                    setError(response.error)
                }
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        addToCart(id)
                    } else {
                        enrollTrialProgress.postValue(STATE.ERROR)
                        addToCart(id)
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    fun enrollTrial(id: String) {
        enrollTrialProgress.postValue(STATE.LOADING)
        runIO {
            when (val response = repo.enrollToTrial(id)) {

                is ResultWrapper.GenericError -> {
                    enrollTrialProgress.postValue(STATE.ERROR)
                    setError(response.error)
                }
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        enrollTrialProgress.postValue(STATE.SUCCESS)
                    } else {
                        enrollTrialProgress.postValue(STATE.ERROR)
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    private fun addToCart(id: String) {
        runIO {
            when (val response = repo.addToCart(id)) {
                is ResultWrapper.GenericError -> {
                    enrollTrialProgress.postValue(STATE.ERROR)
                    setError(response.error)
                }
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        addedToCartProgress.postValue(STATE.SUCCESS)
                    } else {
                        addedToCartProgress.postValue(STATE.ERROR)
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    /**
     * Paged Course List
     */
    private var courseLiveData  : LiveData<PagedList<Course>>

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(9)
            .setEnablePlaceholders(true)
            .build()
        courseLiveData  = initializedPagedListBuilder(config).build()
    }

    fun getCourses():LiveData<PagedList<Course>> = courseLiveData

    private fun initializedPagedListBuilder(config: PagedList.Config):
        LivePagedListBuilder<String, Course> {

        val dataSourceFactory = object : DataSource.Factory<String, Course>() {
            override fun create(): DataSource<String, Course> {
                return CourseDataSource(viewModelScope)
            }
        }
        return LivePagedListBuilder<String, Course>(dataSourceFactory, config)
    }

    fun changeWishlistStatus(courseSingle: Course? = course.value, mainCourse: Boolean = false){
        runIO {
            when (val response = repo.checkIfWishlisted(courseSingle?.id?:"")) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        val isWishlisted = response.value.body()?.id!=null
                        if (mainCourse){
                            wishlistUpdated.postValue(!isWishlisted)
                        }
                        if (isWishlisted){
                            removeFromWishlist(response.value.body()?.id)
                        }else{
                            courseSingle?.let { addToWishlist(it) }
                        }
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    fun checkIfCourseWishlisted(courseSingle: Course?){
        runIO {
            when (val response = repo.checkIfWishlisted(courseSingle?.id?:"")) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        wishlistUpdated.postValue(response.value.body()?.id!=null)
                        course.postValue(courseSingle)
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }

        }
    }

    fun addToWishlist(courseSingle: Course = course.value!!){
        val wishlist = Wishlist(courseSingle, User(prefs.SP_USER_ID))
        runIO {
            when (val response = repo.addToWishlist(wishlist)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        snackbar.postValue("${courseSingle.title} added to Wishlist")
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun removeFromWishlist(courseSingle: String?){
        runIO {
            when (val response = repo.removeFromWishlist(courseSingle?:"")) {
                is ResultWrapper.GenericError -> {
                    setError(response.error)
                }
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        snackbar.postValue("Course removed from Wishlist")
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }
}
