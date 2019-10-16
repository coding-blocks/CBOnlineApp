package com.codingblocks.cbonlineapp.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.FeaturesDao
import com.codingblocks.cbonlineapp.database.InstructorDao
import com.codingblocks.cbonlineapp.database.models.CourseFeatureModel
import com.codingblocks.cbonlineapp.database.models.CourseInstructorHolder
import com.codingblocks.cbonlineapp.database.models.CourseModel
import com.codingblocks.cbonlineapp.database.models.InstructorModel
import com.codingblocks.cbonlineapp.database.models.RunModel
import com.codingblocks.cbonlineapp.util.extensions.greater
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.CarouselCards
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeViewModel(
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao,
    private val instructorDao: InstructorDao,
    private val featuresDao: FeaturesDao
) : ViewModel() {
    var carouselCards: MutableLiveData<List<CarouselCards>> = MutableLiveData()
    var carouselError: MutableLiveData<String> = MutableLiveData()
    var progress: MutableLiveData<Boolean> = MutableLiveData()
    private val courses = MutableLiveData<List<CourseInstructorHolder.CourseInstructorPair>>()

    fun getAllCourses() = courseWithInstructorDao.getCourses()

    fun getRecommendedCourses(): LiveData<List<CourseInstructorHolder.CourseInstructorPair>> {
        ioMain({
            courseWithInstructorDao.getRecommendedCourses()
        }) {
            courses.value = it
        }
        return courses
    }

    fun fetchRecommendedCourses() {
        Clients.onlineV2JsonApi.getRecommendedCourses()
            .enqueue(retrofitCallback { _, response ->
                response?.let {
                    if (response.isSuccessful) {
                        it.body()?.let { courseList ->
                            courseList.forEach { course ->
                                course.run {
                                    courseDao.insertNew(
                                        CourseModel(
                                            id,
                                            title,
                                            subtitle,
                                            logo,
                                            summary,
                                            promoVideo,
                                            difficulty,
                                            reviewCount,
                                            rating,
                                            slug,
                                            coverImage,
                                            categoryId,
                                            faq
                                        )
                                    )
                                    coursefeatures?.forEach {
                                        featuresDao.insert(CourseFeatureModel(icon = it.icon, text = it.text, crCourseId = id))
                                    }
                                }
                                var list = course.runs?.filter { run ->
                                    !run.enrollmentStart.greater() && run.enrollmentEnd.greater() && !run.unlisted
                                }?.sortedWith(compareBy { run -> run.price })
                                if (list != null) {
                                    if (list.isEmpty()) {
                                        list =
                                            course.runs?.sortedWith(compareBy { run -> run.price })
                                    }
                                }
                                list?.get(0)?.run {
                                    try {
                                        runDao.insertNew(
                                            RunModel(
                                                id,
                                                null,
                                                name,
                                                description,
                                                enrollmentStart,
                                                enrollmentEnd,
                                                start,
                                                end,
                                                price,
                                                mrp ?: "",
                                                course.id,
                                                crUpdatedAt = updatedAt,
                                                recommended = true
                                            )
                                        )

                                        course.instructors?.forEach { instructor ->
                                            instructorDao.insertNew(
                                                InstructorModel(
                                                    instructor.id,
                                                    instructor.name,
                                                    instructor.description ?: "",
                                                    instructor.photo,
                                                    instructor.email,
                                                    instructor.sub
                                                )
                                            )
                                            courseWithInstructorDao.insert(
                                                CourseInstructorHolder.CourseWithInstructor(course.id, instructor.id)
                                            )
                                        }
                                    } catch (e: Exception) {
                                        progress.value = false
                                    }
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
                                    courseDao.insertNew(
                                        CourseModel(
                                            id,
                                            title,
                                            subtitle,
                                            logo,
                                            summary,
                                            promoVideo,
                                            difficulty,
                                            reviewCount,
                                            rating,
                                            slug,
                                            coverImage,
                                            categoryId,
                                            faq
                                        )
                                    )
                                }
                                var list = course.runs?.filter { runs ->
                                    !runs.enrollmentStart.greater() && runs.enrollmentEnd.greater() && !runs.unlisted
                                }?.sortedWith(compareBy { run -> run.price })
                                if (list != null) {
                                    if (list.isEmpty()) {
                                        list =
                                            course.runs?.sortedWith(compareBy { run -> run.price })
                                    }
                                }
                                list?.get(0)?.run {
                                    runDao.insertNew(
                                        RunModel(
                                            id,
                                            "",
                                            name,
                                            description,
                                            enrollmentStart,
                                            enrollmentEnd,
                                            start,
                                            end,
                                            price,
                                            mrp ?: "",
                                            course.id,
                                            crUpdatedAt = updatedAt
                                        )
                                    )
                                }

                                course.instructors?.forEach { instructor ->
                                    instructorDao.insertNew(
                                        InstructorModel(
                                            instructor.id,
                                            instructor.name,
                                            instructor.description ?: "",
                                            instructor.photo,
                                            instructor.email,
                                            instructor.sub
                                        )
                                    )
                                    courseWithInstructorDao.insert(
                                        CourseInstructorHolder.CourseWithInstructor(course.id, instructor.id)
                                    )
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

    fun <T : Any> ioMain(
        work: suspend (() -> T?),
        callback: ((T?) -> Unit)? = null
    ): Job =
        CoroutineScope(Dispatchers.Main).launch {
            val data = CoroutineScope(Dispatchers.IO).async {
                return@async work()
            }.await()

            callback?.let {
                it(data)
            }
        }
}
