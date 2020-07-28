package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.RunAttemptModel

/**
 * @author aggarwalpulkit596
 */
@Dao
interface RunAttemptDao : BaseDao<RunAttemptModel> {

    @Query("SElECT * FROM RunAttemptModel where attemptId = :id")
    fun getRunAttempt(id: String): LiveData<RunAttemptModel>

    @Query("SElECT * FROM RunAttemptModel")
    fun getRunAttempts(): LiveData<List<RunAttemptModel>>

    @Query("SElECT attemptId FROM RunAttemptModel where attemptId = :id")
    fun getAttemptId(id: String): String?

    @Query("UPDATE RunAttemptModel SET paused = :paused, pauseTimeLeft = :pausedTimeLeft,lastPausedLeft = :lastPausedLeft  WHERE attemptId =:id")
    suspend fun updatePause(id: String, paused: Boolean, pausedTimeLeft: String?, lastPausedLeft: String?)
}
