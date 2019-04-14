package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class CourseRunDao : BaseDao<CourseRun> {
    @Query("SELECT * FROM CourseRun where crAttemptId ==" + "'" + "'")
    abstract fun getAllRuns(): LiveData<List<CourseRun>>

    @Query("SELECT * FROM CourseRun where recommended == 1 AND crAttemptId ==" + "'" + "'")
    abstract fun getRecommendedRuns(): LiveData<List<CourseRun>>

    @Query("SELECT * FROM CourseRun where crAttemptId != " + "'" + "' ORDER BY hits DESC")
    abstract fun getMyRuns(): LiveData<List<CourseRun>>

    @Query("SELECT * FROM CourseRun where crUid = :runid")
    abstract fun getRunById(runid: String): CourseRun

    @Query("UPDATE CourseRun SET hits = hits+1 where crAttemptId = :attemptId")
    abstract fun updateHit(attemptId: String)

    @Query("SELECT * FROM CourseRun where crAttemptId = :attemptId")
    abstract fun getRunByAtemptId(attemptId: String): LiveData<CourseRun>
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
