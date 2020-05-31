package com.codingblocks.cbonlineapp.database

import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.DownloadModel

@Dao
interface DownloadsDao : BaseDao<DownloadModel> {

    @Query("SELECT * FROM DownloadModel")
    suspend fun getAllDownloads(): List<DownloadModel>

}
