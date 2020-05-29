package com.codingblocks.cbonlineapp.database

import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.CodeChallengeModel

@Dao
interface CodeChallengeDao : BaseDao<CodeChallengeModel> {

    @Query("SElECT * FROM CodeChallengeModel where id = :id")
    suspend fun getCodeChallengeById(id: String): CodeChallengeModel
}
