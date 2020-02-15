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

    @Query("SElECT * FROM ContentModel where isDownloaded = :isDownloaded ORDER BY date")
    suspend fun getDownloads(isDownloaded: Boolean): List<ContentModel>

    @Transaction
    @Query("UPDATE ContentModel SET isDownloaded = :status WHERE ccid = :contentId")
    suspend fun updateContent(contentId: String, status: Int)

    @Transaction
    @Query("UPDATE ContentModel SET isDownloaded = :status WHERE lectureId = :videoId")
    suspend fun updateContentWithVideoId(videoId: String, status: Int)

    @Transaction
    suspend fun update(id: String, attemptId: String, progressId: String, status: String) {
        updateProgress(id, attemptId, status)
        updateProgressID(id, attemptId, progressId)
    }

    @Query("UPDATE ContentModel SET progressId = :progressId WHERE ccid = :id AND attempt_id = :attemptId")
    suspend fun updateProgressID(id: String, attemptId: String, progressId: String)

    @Query("UPDATE ContentModel SET progress = :status WHERE ccid = :id AND attempt_id = :attemptId")
    suspend fun updateProgress(id: String, attemptId: String, status: String)

    @Transaction
    @Query("SElECT * FROM ContentModel where  ccid = :id")
    fun getContent(id: String): ContentModel

    @Query("SElECT * FROM ContentModel where  ccid = :id")
    fun getContentLive(id: String): LiveData<ContentModel>

    @Query("SElECT progressId FROM ContentModel where  ccid = :id")
    suspend fun getProgressId(id: String): String
}
