package com.codingblocks.cbonlineapp.course

import androidx.lifecycle.MutableLiveData
import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.baseclasses.STATE
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.getMeta
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

    fun fetchAllCourses(offset: String = "0") {
        runIO {
            when (val response = repo.getAllCourses(offset)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (response.value.isSuccessful)
                        response.value.body()?.let {
                            val currentOffSet = getMeta(it.meta, "currentOffset").toString()
                            val nextOffSet = getMeta(it.meta, "nextOffset").toString()
                            if (currentOffSet != nextOffSet && nextOffSet != "null") {
                                fetchAllCourses(nextOffSet)
                                it.get()?.let { it1 -> allCourse.addAll(it1) }
                                if (isLoggedIn){
                                    checkIfWishlisted(allCourse)
                                }else{
                                    suggestedCourses.postValue(allCourse)
                                }
                            } else {
                                setError(fetchError(code()))
                            }
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

    fun addToCart(id: String) {
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

    fun checkIfWishlisted(allCourse: List<Course>?){
        runIO {
            allCourse?.forEach {courseSingle->
                when (val response = repo.checkIfWishlisted(courseSingle.id?:"")) {
                    is ResultWrapper.GenericError -> setError(response.error)
                    is ResultWrapper.Success -> with(response.value) {
                        if (isSuccessful) {
                            if (response.value.body()?.id!=null){
                                courseSingle.isWishlist = true
                                courseSingle.userWishlistId = response.value.body()?.id
                            }
                        } else {
                            setError(fetchError(code()))
                        }
                    }
                }
            }
            suggestedCourses.postValue(allCourse)
        }
    }

    fun checkIfCourseWishlisted(courseSingle: Course?){
        runIO {
            when (val response = repo.checkIfWishlisted(courseSingle?.id?:"")) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        if (response.value.body()?.id!=null){
                            courseSingle?.isWishlist = true
                            courseSingle?.userWishlistId = response.value.body()?.id
                        }else{
                            courseSingle?.isWishlist = false
                            courseSingle?.userWishlistId = response.value.body()?.id
                        }
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
                is ResultWrapper.GenericError -> {
                    setError(response.error)
                }
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        courseSingle.userWishlistId = response.value.body()?.id
                        courseSingle.isWishlist = true
                        wishlistUpdated.postValue(true)
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }

    fun removeFromWishlist(courseSingle: Course = course.value!!){
        runIO {
            when (val response = repo.removeFromWishlist(courseSingle.userWishlistId?:"")) {
                is ResultWrapper.GenericError -> {
                    setError(response.error)
                }
                is ResultWrapper.Success -> {
                    if (response.value.isSuccessful) {
                        repo.removeFromWishlist(courseSingle)
                        wishlistUpdated.postValue(true)
                    } else {
                        setError(fetchError(response.value.code()))
                    }
                }
            }
        }
    }
}
