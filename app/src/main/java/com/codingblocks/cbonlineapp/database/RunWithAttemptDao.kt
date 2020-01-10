package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.codingblocks.cbonlineapp.database.models.RunWithAttempt

/**
 * @author aggarwalpulkit596
 */
@Dao
interface RunWithAttemptDao {

    @Transaction
    @Query("SELECT * FROM RunModel")
    fun getRunsAndAttempts(): LiveData<List<RunWithAttempt>>

    @Transaction
    @Query("SELECT * FROM RunModel where crUid = :id")
    fun getRunWithAttempt(id: String): LiveData<RunWithAttempt>
}
