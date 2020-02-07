package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.RunPerformance

/**
 * @author aggarwalpulkit596
 */
@Dao
interface RunPerformanceDao : BaseDao<RunPerformance> {

    @Query("SElECT * FROM RunPerformance where id = :id")
    fun getPerformance(id: String): LiveData<RunPerformance>
}
