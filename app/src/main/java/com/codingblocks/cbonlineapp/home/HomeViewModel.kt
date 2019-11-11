package com.codingblocks.cbonlineapp.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import com.codingblocks.cbonlineapp.util.extensions.filterList
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.CarouselCards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HomeRepository
) : ViewModel() {
    var carouselCards: MutableLiveData<List<CarouselCards>> = MutableLiveData()
    var carouselError: MutableLiveData<String> = MutableLiveData()
    var progress: MutableLiveData<Boolean> = MutableLiveData()
    var courses: LiveData<List<CourseInstructorPair>> = MutableLiveData()
    var courseFilter = MutableLiveData<String>("")

    fun getAllCourses() = repository.getAllCourses()

    init {
        courses = Transformations.switchMap(courseFilter) { query ->
            repository.getRecommendedCourses().filterList {
                (it?.courseRun?.course?.title ?: "").contains(query, true)
            }
        }
    }

    fun fetchRecommendedCourses() {
        Clients.onlineV2JsonApi.getRecommendedCourses()
            .enqueue(retrofitCallback { _, response ->
                response?.let {
                    if (response.isSuccessful) {
                        it.body()?.let { courseList ->
                            courseList.forEach { course ->
                                viewModelScope.launch(Dispatchers.IO) {
                                    repository.insertCourse(course)
                                }
                            }
                        }
                    }
                    progress.value = false
                }
            })
    }

    fun fetchAllCourses() {
        Clients.onlineV2JsonApi.getAllCourses()
            .enqueue(retrofitCallback { _, response ->
                response?.let {
                    if (response.isSuccessful) {
                        it.body()?.let { courseList ->
                            courseList.forEach { course ->
                                course.run {
                                    viewModelScope.launch(Dispatchers.IO) {
                                        repository.insertCourse(course, false)
                                    }
                                }
                            }
                        }
                    }
                    progress.value = false
                }
            })
    }

    fun fetchCards() {
        Clients.onlineV2JsonApi.carouselCards.enqueue(retrofitCallback { error, response ->
            response?.body()?.let {
                carouselCards.value = it
            }
            error?.let {
                carouselError.postValue(it.message)
            }
        })
    }
}
