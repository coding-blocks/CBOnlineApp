package com.codingblocks.cbonlineapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.RatingModel
import com.codingblocks.onlineapi.models.Sections
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Response

class CourseViewModel(
    private val courseWithInstructorDao: CourseWithInstructorDao
) : ViewModel() {
    var sheetBehavior: BottomSheetBehavior<*>? = null

    var image: MutableLiveData<String> = MutableLiveData()
    var name: MutableLiveData<String> = MutableLiveData()

    var fetchedCourse: MutableLiveData<Course> = MutableLiveData()
    var courseRating: MutableLiveData<RatingModel> = MutableLiveData()

    var addedToCartProgress: MutableLiveData<Boolean> = MutableLiveData()
    var clearCartProgress: MutableLiveData<Boolean> = MutableLiveData()
    var enrollTrialProgress: MutableLiveData<Boolean> = MutableLiveData()

    fun getInstructorWithCourseId(id: String) = courseWithInstructorDao.getInstructorWithCourseId(id)

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

    fun getCourseRating(id: String) {
        Clients.api.getCourseRating(id).enqueue(retrofitCallback { _, response ->
            if (response?.isSuccessful == true)
                courseRating.value = response.body()
        })
    }

    fun enrollTrial(id: String) {
        Clients.api.enrollTrial(id).enqueue(retrofitCallback { _, response ->
            enrollTrialProgress.value = (response?.isSuccessful == true)
        })
    }

    suspend fun getSectionsFromID(id: String): Response<Sections> {
        return Clients.onlineV2JsonApi.getSections(id).await()
    }

    fun addToCart(id: String) {
        Clients.api.addToCart(id).enqueue(retrofitCallback { _, response ->
            addedToCartProgress.value = (response?.isSuccessful ?: false)
        })
    }
}
