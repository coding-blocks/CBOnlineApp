package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.PlayerState

@Dao
interface PlayerDao : BaseDao<PlayerState> {

    @Query("""
  SELECT ps.*,s.name as sectionName,c.title as contentTitle,c.contentDuration From PlayerState ps 
        INNER JOIN SectionModel s ON ps."sectionId" = s."csid"
	    INNER JOIN ContentModel c ON c."ccid" = ps."contentId"
        ORDER BY ps.lastAccessedAt;
            """)
    fun getPromotedStories(): LiveData<List<PlayerState>>

    @Query("UPDATE PlayerState SET thumbnail = :thumbnail WHERE contentId = :contentId")
    suspend fun updateThumbnail(thumbnail: String, contentId: String)

    @Query("DELETE FROM PlayerState WHERE attemptId = :attemptId")
    fun deleteById(attemptId: String)
}
