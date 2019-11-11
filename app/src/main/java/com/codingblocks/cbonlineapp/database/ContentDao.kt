package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.codingblocks.cbonlineapp.database.models.ContentModel

@Dao
interface ContentDao : BaseDao<ContentModel> {

    @Query("SElECT * FROM ContentModel where attempt_id = :attempt_id AND ccid = :id")
    fun getContentWithId(attempt_id: String, id: String): ContentModel

    @Query("SElECT * FROM ContentModel where isDownloaded = :progress ORDER BY date")
    fun getDownloads(progress: String): List<ContentModel>

    @Query("UPDATE ContentModel SET isDownloaded = :status WHERE ccid = :contentId")
    suspend fun updateContent(contentId: String, status: Int)

    //    @Query("UPDATE ContentModel SET isDownloaded = :downloadprogress WHERE lectureId = :videoId AND section_id = :section")
//    abstract fun updateContentWithVideoId(section: String, videoId: String, downloadprogress: String)
    @Transaction
    suspend fun update(id: String, attemptId: String, progressId: String, status: String) {
        updateProgress(id, attemptId, status)
        updateProgressID(id, attemptId, progressId)
    }

    @Query("UPDATE ContentModel SET progressId = :progressId WHERE ccid = :id AND attempt_id = :attemptId")
    suspend fun updateProgressID(id: String, attemptId: String, progressId: String)

    @Query("UPDATE ContentModel SET progress = :status WHERE ccid = :id AND attempt_id = :attemptId")
    suspend fun updateProgress(id: String, attemptId: String, status: String)

    @Query("""
        SELECT c.* FROM ContentModel c
        INNER JOIN SectionModel s ON c."ccid" = sc."content_id"
        INNER JOIN SectionWithContent sc ON sc."section_id" = s."csid"
        WHERE s.csid =:sectionId AND c.attempt_id =:attemptId AND
        c.`order` = ((SELECT `order` FROM ContentModel where ccid = :uid) + 1 ) LIMIT 1
    """)
    fun getNextItem(sectionId: String, attemptId: String, uid: String): LiveData<ContentModel>
}
