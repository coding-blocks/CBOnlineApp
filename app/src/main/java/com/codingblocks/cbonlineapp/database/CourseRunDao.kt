package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class CourseRunDao : BaseDao<CourseRun> {

    @Query("SELECT * FROM CourseRun where crAttemptId ==" + "'"+"'")
    abstract fun getAllRuns(): LiveData<List<CourseRun>>

    @Query("SELECT * FROM CourseRun where crAttemptId != " + "'"+"'")
    abstract fun getMyRuns(): LiveData<List<CourseRun>>

    @Query("SELECT * FROM CourseRun where crUid = :runid")
    abstract fun getRunById(runid:String): CourseRun

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