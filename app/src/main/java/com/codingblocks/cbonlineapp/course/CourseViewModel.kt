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
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Project
import com.codingblocks.onlineapi.models.Sections
import com.codingblocks.onlineapi.models.Wishlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class CourseViewModel(
    private val repo: CourseRepository,
    val prefs: PreferenceHelper
) : BaseCBViewModel() {
    lateinit var id: String
    var course = MutableLiveData<Course>()
    var suggestedCourses = MutableLiveData<List<Course>>()
    val findCourses = MutableLiveData<List<Course>>()
    val projects = MutableLiveData<List<Project>>()
    val sections = MutableLiveData<List<Sections>>()
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
    private var courseLiveData: LiveData<PagedList<Course>>

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(9)
            .setEnablePlaceholders(true)
            .build()
        courseLiveData = initializedPagedListBuilder(config).build()
    }

    fun getCourses(): LiveData<PagedList<Course>> = courseLiveData

    private fun initializedPagedListBuilder(config: PagedList.Config):
        LivePagedListBuilder<String, Course> {

            val dataSourceFactory = object : DataSource.Factory<String, Course>() {
                override fun create(): DataSource<String, Course> {
                    return CourseDataSource(viewModelScope)
                }
            }
            return LivePagedListBuilder<String, Course>(dataSourceFactory, config)
        }

    fun changeWishlistStatus(id: String) {
        runIO {
            when (val response = repo.checkIfWishlisted(id)) {
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
        val wishlist = Wishlist(Course(id))
        runIO {
            when (val response = repo.addWishlist(wishlist)) {
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

    fun removeWishlist(courseSingle: String) {
        runIO {
            when (val response = repo.removeWishlist(courseSingle)) {
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
