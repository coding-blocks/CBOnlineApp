package com.codingblocks.cbonlineapp.course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.FeaturesDao
import com.codingblocks.cbonlineapp.database.models.InstructorModel
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.CourseFeatures
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CourseViewModel(
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val featuresDao: FeaturesDao

) : ViewModel() {
    private val repository = CourseRepository()

    var sheetBehavior: BottomSheetBehavior<*>? = null

    var image: MutableLiveData<String> = MutableLiveData()
    var name: MutableLiveData<String> = MutableLiveData()
    lateinit var instructors: MutableLiveData<List<InstructorModel>>
    lateinit var features: MutableLiveData<List<CourseFeatures>>

    var fetchedCourse: MutableLiveData<Course> = MutableLiveData()
    var addedToCartProgress: MutableLiveData<Boolean> = MutableLiveData()
    var clearCartProgress: MutableLiveData<Boolean> = MutableLiveData()
    var enrollTrialProgress: MutableLiveData<Boolean> = MutableLiveData()

    fun getInstructors(id: String): LiveData<List<InstructorModel>> {
        if (!::instructors.isInitialized) {
            instructors = MutableLiveData()
            viewModelScope.launch(Dispatchers.IO) {
                instructors.postValue(courseWithInstructorDao.getInstructors(id))
            }
        }
        return instructors
    }

    fun getCourseFeatures(id: String): LiveData<List<CourseFeatures>> {
        if (!::features.isInitialized) {
            features = MutableLiveData()
            viewModelScope.launch(Dispatchers.IO) {
                features.postValue(featuresDao.getFeatures(id))
            }
        }
        return features
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

    fun getCourse(courseId: String) {
        Clients.onlineV2JsonApi.courseById(courseId).enqueue(retrofitCallback { _, response ->
            if (response?.isSuccessful == true)
                fetchedCourse.value = response.body()
        })
    }

    fun getCourseRating(id: String) = liveData(Dispatchers.IO) {
        emit(repository.getRating(id))
    }

    suspend fun getCourseSection(id: String) = repository.getCourseSections(id)

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
}
