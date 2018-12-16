package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class CourseRunDao : BaseDao<CourseRun> {

    @Query("SElECT * FROM CourseRun where crAttemptId = :courseId ")
    abstract fun getCourseRun(courseId: String): LiveData<CourseRun>

//
//    @Delete
//    fun delete(course: CourseRun)
//
//    @Update
//    fun update(course: CourseRun)
//
//
//    @Transaction
//    @Query("SELECT * FROM CourseRun")
//    fun getAllSections(): LiveData<AllCourseSection>
//
//    class AllCourseSection {
//        @Embedded
//        lateinit var course: CourseRun
//
//        @Relation(parentColumn = "id", entityColumn = "run_id")
//        lateinit var sections: List<CourseSection>
//
//    }
}