package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.JobsModel

@Dao
abstract class JobsDao : BaseDao<JobsModel> {
    @Query("SElECT * FROM JobsModel ")
    abstract fun getAllJobs(): LiveData<List<JobsModel>>

    @Query("SElECT * FROM JobsModel where uid = :id")
    abstract fun getJobById(id: String): LiveData<JobsModel>
}
