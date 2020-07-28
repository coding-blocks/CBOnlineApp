package com.codingblocks.cbonlineapp.database

import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.CodeChallengeModel
import com.codingblocks.cbonlineapp.database.models.CodeModel

@Dao
interface CodeChallengeDao : BaseDao<CodeChallengeModel> {

    @Query("SElECT * FROM CodeChallengeModel where id = :id")
    suspend fun getCodeChallengeById(id: String): CodeChallengeModel?

    @Query("SELECT codeUid, codeContestId, attempt_id FROM ContentModel WHERE ccid = :contentId")
    suspend fun getCodeChallenge(contentId: String): CodeModel
}
