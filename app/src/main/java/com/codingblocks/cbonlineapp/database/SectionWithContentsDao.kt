package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder.DownloadableContent
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder.NextContent
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder.SectionContentPair
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder.SectionWithContent
import com.codingblocks.cbonlineapp.database.models.SectionModel

@Dao
interface SectionWithContentsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(join: SectionWithContent)

    @Query("""
        SELECT c.ccid as contentId,s.csid as sectionId,c.lectureId as videoId FROM  SectionModel s
	    INNER JOIN SectionWithContent sc ON sc."section_id" = s."csid"
	    INNER JOIN ContentModel c ON c."ccid" = sc."content_id"
	    WHERE s.attemptId = :attemptId AND s.csid = :sectionId AND c.contentable = "lecture"AND isDownloaded = 0 
        ORDER BY c.`order`;
            """)
    suspend fun getVideoIdsWithSectionId(sectionId: String, attemptId: String): List<DownloadableContent>

    @Query("""
        SELECT c.ccid as contentId,s.csid as sectionId,c.contentable FROM  SectionModel s
	    INNER JOIN SectionWithContent sc ON sc."section_id" = s."csid"
	    INNER JOIN ContentModel c ON c."ccid" = sc."content_id"
	    WHERE s.attemptId = :attemptId AND progress != "DONE" AND (c.contentable = "lecture" OR c.contentable = "video")
        ORDER BY s."sectionOrder", sc."order" LIMIT 1;
        """)
    fun resumeCourse(attemptId: String): LiveData<NextContent>

    //
    @Query("""
        SELECT s.* FROM SectionModel s,ContentModel c 
	    WHERE s.attemptId = :attemptId AND s.csid = :sectionId  AND (c.contentable = "lecture" OR c.contentable = "video")
        ORDER BY s."sectionOrder", s."sectionOrder" LIMIT 1
        """)
    fun getNextContent(attemptId: String, sectionId: String): LiveData<SectionContentPair>

    @Query("""
        SELECT s.* FROM SectionModel s
	    WHERE s.attemptId = :attemptId
        ORDER BY s."sectionOrder"
        """)
    fun getSectionWithContent(attemptId: String): LiveData<List<SectionContentPair>>

    @RawQuery(observedEntities = [SectionModel::class, ContentModel::class])
    fun getSectionWithContentComputed(query: SupportSQLiteQuery): LiveData<List<SectionContentPair>>

    @Query("""
        SELECT s.* FROM SectionModel s
	    WHERE s.attemptId = :attemptId
        ORDER BY s."sectionOrder"
        """)
    suspend fun getSectionWithContentNonLive(attemptId: String): List<SectionContentPair>
}
