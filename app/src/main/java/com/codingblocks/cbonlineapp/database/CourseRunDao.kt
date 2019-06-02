package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.CourseRun

@Dao
abstract class CourseRunDao : BaseDao<CourseRun> {

    @Query("SELECT * FROM CourseRun where crAttemptId ==" + "'" + "'")
    abstract fun getAllRuns(): LiveData<List<CourseRun>>

    @Query("SELECT * FROM CourseRun where recommended == 1 AND crAttemptId ==" + "'" + "'")
    abstract fun getRecommendedRuns(): LiveData<List<CourseRun>>

    @Query("SELECT * FROM CourseRun where crAttemptId != " + "'" + "' ORDER BY hits DESC")
    abstract fun getMyRuns(): LiveData<List<CourseRun>>

    @Query("SELECT * FROM CourseRun where crAttemptId != " + "'" + "' ORDER BY hits DESC limit 2")
    abstract fun getTopRun(): LiveData<List<CourseRun>>

    @Query("SELECT * FROM CourseRun where crAttemptId = :attemptId")
    abstract fun getRunById(attemptId: String): CourseRun

    @Query("SELECT * FROM CourseRun where crUid = :runId")
    abstract fun getRunByRunId(runId: String): CourseRun

    @Query("UPDATE CourseRun SET hits = hits+1 where crAttemptId = :attemptId")
    abstract fun updateHit(attemptId: String)

    @Query("SELECT * FROM CourseRun where crAttemptId = :attemptId")
    abstract fun getRunByAtemptId(attemptId: String): LiveData<CourseRun>
}
