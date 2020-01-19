package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.RunModel

/**
 * @author aggarwalpulkit596
 */
@Dao
interface RunDao : BaseDao<RunModel> {

    @Query("SElECT crUid FROM RunModel where crUid = :id")
    fun getRunId(id: String): String?

    @Query("SElECT * FROM RunModel")
    fun getRunAttempts(): LiveData<List<RunModel>>
}
