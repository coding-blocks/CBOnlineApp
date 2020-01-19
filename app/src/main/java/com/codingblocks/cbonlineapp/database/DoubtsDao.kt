package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.DoubtsModel

@Dao
abstract class DoubtsDao : BaseDao<DoubtsModel> {

    @Query("SElECT * FROM DoubtsModel where runAttemptId = :ruid")
    abstract fun getDoubts(ruid: String): LiveData<List<DoubtsModel>>

    @Query("SElECT * FROM DoubtsModel where status != 'RESOLVED' AND runAttemptId = :ruid")
    abstract fun getLiveDoubts(ruid: String): LiveData<List<DoubtsModel>>

    @Query("SElECT * FROM DoubtsModel where status = 'RESOLVED' AND runAttemptId = :ruid")
    abstract fun getResolveDoubts(ruid: String): LiveData<List<DoubtsModel>>

    @Query("UPDATE DoubtsModel SET status = :status where dbtUid = :uid")
    abstract suspend fun updateStatus(uid: String, status: String)

    @Query("SElECT * FROM DoubtsModel where dbtUid = :uid")
    abstract fun getDoubtById(uid: String): LiveData<DoubtsModel>
}
