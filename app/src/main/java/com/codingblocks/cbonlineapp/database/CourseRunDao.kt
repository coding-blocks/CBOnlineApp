package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.RunModel

@Dao
abstract class CourseRunDao : BaseDao<RunModel> {

    @Query("SELECT * FROM RunModel where crAttemptId != " + "'" + "' ORDER BY hits DESC limit 2")
    abstract fun getTopRun(): LiveData<List<RunModel>>

    @Query("SELECT * FROM RunModel where crAttemptId = :attemptId")
    abstract suspend fun getRunById(attemptId: String): RunModel

    @Query("UPDATE RunModel SET hits = hits+1 where crAttemptId = :attemptId")
    abstract suspend fun updateHit(attemptId: String)

    @Query("SELECT * FROM RunModel where crAttemptId = :attemptId")
    abstract fun getRunByAtemptId(attemptId: String): LiveData<RunModel>

    @Query("SELECT * FROM RunModel where crCourseId IN (:courses)")
    abstract fun getJobCourses(courses: ArrayList<String>): List<RunModel>

    @Query("SELECT crUid FROM RunModel where crUid = :runId")
    abstract fun getRun(runId: String): String
}
