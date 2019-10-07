package com.codingblocks.cbonlineapp.home.mycourses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Config
import androidx.paging.toLiveData
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.InstructorDao
import com.codingblocks.cbonlineapp.database.models.CourseInstructorHolder
import com.codingblocks.cbonlineapp.database.models.CourseModel
import com.codingblocks.cbonlineapp.database.models.InstructorModel
import com.codingblocks.cbonlineapp.database.models.RunModel
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.crashlytics.android.Crashlytics

class MyCoursesViewModel(
    private val courseWithInstructorDao: CourseWithInstructorDao,
    private val courseDao: CourseDao,
    private val runDao: CourseRunDao,
    private val instructorDao: InstructorDao
) : ViewModel() {
    var message: MutableLiveData<String> = MutableLiveData()
    var progress: MutableLiveData<Boolean> = MutableLiveData()

    // Todo : Fix page list
    fun getMyRuns() = courseWithInstructorDao.getMyRuns().toLiveData(Config(5, enablePlaceholders = false))

    fun fetchMyCourses(refresh: Boolean = false) {
        progress.postValue(true)
        Clients.onlineV2JsonApi.getMyCourses()
            .enqueue(retrofitCallback { error, res ->
                res?.let { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { courseList ->
                            courseList.forEach { courseRun ->
                                courseRun.runAttempts?.get(0)?.id?.let { runId ->
                                    Clients.api.getMyCourseProgress(runId)
                                        .enqueue(retrofitCallback { _, progressRes ->
                                            progressRes?.body().let {
                                                var progress = 0.0
                                                var completedContents = 0.0
                                                var totalContents = 0.0
                                                try {
                                                    progress = it?.get("percent") as Double
                                                    completedContents = it["completedContents"] as Double
                                                    totalContents = it["totalContents"] as Double
                                                } catch (e: Exception) {
                                                    Crashlytics.logException(e)
                                                }

                                                val newCourse = courseRun.course?.run {
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
                                                        categoryId
                                                    )
                                                }
                                                courseRun.run {
                                                    val newRun = RunModel(
                                                        id,
                                                        runAttempts?.get(0)?.id ?: "",
                                                        name,
                                                        description,
                                                        enrollmentStart,
                                                        enrollmentEnd,
                                                        start,
                                                        end,
                                                        price,
                                                        mrp ?: "",
                                                        course?.id ?: "",
                                                        updatedAt,
                                                        progress,
                                                        runAttempts?.get(0)?.premium
                                                            ?: false,
                                                        whatsappLink,
                                                        runAttempts?.get(0)?.end
                                                            ?: "",
                                                        totalContents.toInt(),
                                                        completedContents.toInt(),
                                                        runAttempts?.get(0)?.certificateApproved
                                                            ?: false,
                                                        completionThreshold,
                                                        productId
                                                    )
                                                    val oldRun = runDao.getRunById(
                                                        runAttempts?.get(0)?.id ?: ""
                                                    )
                                                    if (oldRun == null) {
                                                        newCourse?.let { it1 ->
                                                            courseDao.insertNew(
                                                                it1
                                                            )
                                                        }
                                                        runDao.insertNew(newRun)
                                                    } else if (oldRun.progress != progress || refresh) {
                                                        newRun.hits = oldRun.hits
                                                        newCourse?.let { it1 ->
                                                            courseDao.update(
                                                                it1
                                                            )
                                                        }
                                                        runDao.update(newRun)
                                                    }
                                                }

                                                courseRun.course?.instructors?.forEach { instructorId ->
                                                    Clients.onlineV2JsonApi.instructorsById(
                                                        instructorId.id
                                                    ).enqueue(
                                                        retrofitCallback { _, instructorResponse ->
                                                            instructorResponse?.let {
                                                                if (instructorResponse.isSuccessful) {
                                                                    instructorResponse.body()
                                                                        ?.run {
                                                                            instructorDao.insertNew(
                                                                                InstructorModel(
                                                                                    id,
                                                                                    name,
                                                                                    description
                                                                                        ?: "",
                                                                                    photo,
                                                                                    email,
                                                                                    sub
                                                                                )
                                                                            )
                                                                            courseWithInstructorDao.insert(
                                                                                CourseInstructorHolder.CourseWithInstructor(
                                                                                    courseRun.course?.id
                                                                                        ?: "",
                                                                                    id
                                                                                )
                                                                            )
                                                                        }
                                                                }
                                                            }
                                                        })
                                                }
                                            }
                                        })
                                }
                            }
                        }
                        progress.value = false
                    }
                }
                error?.let {
                    message.postValue(it.localizedMessage)
                    progress.value = false

                }
            })
    }

    fun getTopRun() = courseWithInstructorDao.getTopRun()
}
