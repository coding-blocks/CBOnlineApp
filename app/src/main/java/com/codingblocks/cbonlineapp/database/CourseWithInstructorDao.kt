package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query


@Dao
interface CourseWithInstructorDao {

//    @get:Query("SELECT * from Course")
//    val courseWithInstructors: LiveData<List<CourseWithInstructor>>
//
//    @Query("SELECT * from Course where crAttemptId = :courseId ")
//    fun getCourseWithInstructors(courseId: String): LiveData<CourseWithInstructor>

}