package com.codingblocks.cbonlineapp.course

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Project
import com.codingblocks.onlineapi.models.Sections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CourseViewModel(
    private val repo: CourseRepository
) : ViewModel() {
    lateinit var id: String
    var course = MutableLiveData<Course>()
    var suggestedCourses = MutableLiveData<List<Course>>()
    val findCourses = MutableLiveData<List<Course>>()
    val projects = MutableLiveData<List<Project>>()
    val sections = MutableLiveData<List<Sections>>()
    var errorLiveData = MutableLiveData<String>()

    var image: MutableLiveData<String> = MutableLiveData()
    var name: MutableLiveData<String> = MutableLiveData()
    var addedToCartProgress: MutableLiveData<Boolean> = MutableLiveData()
    var enrollTrialProgress: MutableLiveData<Boolean> = MutableLiveData()

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
        fetchRecommendedCourses()
    }

    fun fetchRecommendedCourses() {
        runIO {
            when (val response = repo.getSuggestedCourses()) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        suggestedCourses.postValue(body())
                    } else {
                        setError(fetchError(code()))
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
                projectIdList.forEach {
                    val projectRes = withContext(Dispatchers.IO) { repo.getProjects(it.id) }
                    projectRes.body()?.let { it1 -> list.add(it1) }
                }
                projects.postValue(list)
            }
        } else {
            projects.postValue(emptyList())
        }
    }

    fun fetchSections(sectionIdList: ArrayList<Sections>?) {
        val list = arrayListOf<Sections>()
        if (!sectionIdList.isNullOrEmpty()) {
            runIO {
                sectionIdList.take(5).forEach {
                    val sectionRes = withContext(Dispatchers.IO) { repo.getSection(it.id) }
                    sectionRes.body()?.let { it1 -> list.add(it1) }
                }
                sections.postValue(list)
            }
        }
    }

    fun fetchAllSections(sectionIdList: ArrayList<Sections>?) {
        val list = arrayListOf<Sections>()
        if (!sectionIdList.isNullOrEmpty()) {
            runIO {
                sectionIdList.forEach {
                    val sectionRes = withContext(Dispatchers.IO) { repo.getSection(it.id) }
                    sectionRes.body()?.let { it1 -> list.add(it1) }
                }
                sections.postValue(list)
            }
        }
    }

    private fun setError(error: String) {
        errorLiveData.postValue(error)
    }

//    fun getCart() {
//        Clients.api.getCart().enqueue(retrofitCallback { _, response ->
//            response?.body().let { json ->
//                json?.getAsJsonArray("cartItems")?.get(0)?.asJsonObject.let {
//                    image.value = it?.get("image_url")?.asString
//                    name.value = it?.get("productName")?.asString
//                    sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
//                }
//            }
//        })
//    }

    fun clearCart(id: String) {
        runIO {
            when (val response = repo.clearCart()) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    addToCart(id)
                }
            }
        }
    }

    fun enrollTrial(id: String) {
        runIO {
            when (val response = repo.enrollToTrial(id)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        enrollTrialProgress.postValue(true)
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }

    fun addToCart(id: String) {
        runIO {
            when (val response = repo.addToCart(id)) {
                is ResultWrapper.GenericError -> setError(response.error)
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        addedToCartProgress.postValue(true)
                    } else {
                        setError(fetchError(code()))
                    }
                }
            }
        }
    }
}
