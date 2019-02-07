package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class DoubtsDao : BaseDao<DoubtsModel> {

    @Query("SElECT * FROM DoubtsModel where runAttemptId = :ruid")
    abstract fun getDoubts(ruid: String): LiveData<List<DoubtsModel>>

}