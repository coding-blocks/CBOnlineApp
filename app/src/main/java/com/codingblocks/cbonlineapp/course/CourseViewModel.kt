package com.codingblocks.cbonlineapp.course

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.extensions.runIO
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.fetchError
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Project
import com.codingblocks.onlineapi.models.Sections
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CourseViewModel(
    private val repo: CourseRepository
) : ViewModel() {
    var course = MutableLiveData<Course>()
    val projects = MutableLiveData<List<Project>>()
    val sections = MutableLiveData<List<Sections>>()
    var errorLiveData = MutableLiveData<String>()

    var sheetBehavior: BottomSheetBehavior<*>? = null

    var image: MutableLiveData<String> = MutableLiveData()
    var name: MutableLiveData<String> = MutableLiveData()
    lateinit var id: String
    var fetchedCourse: MutableLiveData<Course> = MutableLiveData()
    var addedToCartProgress: MutableLiveData<Boolean> = MutableLiveData()
    var clearCartProgress: MutableLiveData<Boolean> = MutableLiveData()
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
    }

    fun getProjects(projectIdList: ArrayList<Project>) {
//        repository.getProjects(projectIdList)
    }

    private fun setError(error: String) {
        errorLiveData.postValue(error)
    }


    fun getCart() {
        Clients.api.getCart().enqueue(retrofitCallback { _, response ->
            response?.body().let { json ->
                json?.getAsJsonArray("cartItems")?.get(0)?.asJsonObject.let {
                    image.value = it?.get("image_url")?.asString
                    name.value = it?.get("productName")?.asString

                    sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        })
    }

    fun clearCart() {
        Clients.api.clearCart().enqueue(retrofitCallback { _, response ->
            clearCartProgress.value = (response?.isSuccessful == true)
        })
    }
//    fun getCourseRating(id: String) = liveData(Dispatchers.IO) {
//        emit(repository.getRating(id))
//    }

//    suspend fun getCourseSection(id: String) = repository.getCourseSections(id)

    fun enrollTrial(id: String) {
        Clients.api.enrollTrial(id).enqueue(retrofitCallback { _, response ->
            enrollTrialProgress.value = (response?.isSuccessful == true)
        })
    }

    fun addToCart(id: String) {
        Clients.api.addToCart(id).enqueue(retrofitCallback { _, response ->
            addedToCartProgress.value = (response?.isSuccessful ?: false)
        })
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
}
