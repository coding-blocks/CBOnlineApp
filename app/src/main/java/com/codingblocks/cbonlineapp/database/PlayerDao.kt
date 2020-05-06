package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.PlayerState

@Dao
interface PlayerDao : BaseDao<PlayerState> {

    @Query("""
        SELECT ps.*,s.name as sectionName,c.title as contentTitle From PlayerState ps 
        INNER JOIN SectionModel s ON ps."sectionId" = s."csid"
	    INNER JOIN SectionWithContent sc ON sc."section_id" = s."csid"
	    INNER JOIN ContentModel c ON c."ccid" = sc."content_id"
	    WHERE s.attemptId = :attemptId AND s.csid = :sectionId AND c.ccid = :contentId
        ORDER BY ps.lastAccessedAt;
            """)
    fun getPromotedStories(attemptId: String, sectionId: String, contentId: String): LiveData<List<PlayerState>>
}
