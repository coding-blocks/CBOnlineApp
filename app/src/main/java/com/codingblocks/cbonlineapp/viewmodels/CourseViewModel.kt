package com.codingblocks.cbonlineapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRunDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.InstructorDao
import com.codingblocks.cbonlineapp.database.models.Course
import com.codingblocks.cbonlineapp.database.models.CourseRun
import com.codingblocks.cbonlineapp.database.models.CourseWithInstructor
import com.codingblocks.cbonlineapp.database.models.Instructor
import com.codingblocks.cbonlineapp.extensions.greater
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients


class CourseViewModel(
    var courseWithInstructorDao: CourseWithInstructorDao,
    var courseDao: CourseDao,
    var runDao: CourseRunDao,
    var instructorDao: InstructorDao
) : ViewModel() {

    fun fetchRecommendedCourses() {
        Clients.onlineV2JsonApi.getRecommendedCourses()
            .enqueue(retrofitCallback { throwable, response ->
                response?.let {
                    if (response.isSuccessful) {
                        it.body()?.let { courseList ->
                            courseList.forEach { course ->
                                course.run {
                                    courseDao.insert(
                                        Course(
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
                                            updatedAt,
                                            categoryId
                                        )
                                    )
                                }
                                var list = course.runs?.filter {
                                    !it.enrollmentStart.greater() && it.enrollmentEnd.greater() && !it.unlisted
                                }?.sortedWith(compareBy { it.price })
                                if (list != null) {
                                    if(list.isEmpty()){
                                        list = course.runs?.sortedWith(compareBy { it.price })
                                    }
                                }
                                list?.get(0)?.run {
                                    runDao.insert(
                                        CourseRun(
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
                                            updatedAt,
                                            title = course.title,
                                            recommended = true
                                        )
                                    )
                                }



                                    course.instructors?.forEach {
                                        instructorDao.insert(
                                            Instructor(
                                                it.id,
                                                it.name,
                                                it.description ?: "",
                                                it.photo,
                                                it.updatedAt
                                            )
                                        )
                                        courseWithInstructorDao.insert(
                                            CourseWithInstructor(course.id, it.id)
                                        )
                                    }
                                }

                            }
                        }
                    }
                })
            }

    }

