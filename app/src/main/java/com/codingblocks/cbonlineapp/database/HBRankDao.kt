package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.HBRankModel

@Dao
interface HBRankDao :BaseDao<HBRankModel> {
    @Query("SELECT * FROM HBRankModel ORDER BY id DESC LIMIT 1")
    fun getRank():LiveData<HBRankModel>
}
