package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.BookmarkModel
import com.codingblocks.cbonlineapp.database.models.ContentLecture
import com.codingblocks.cbonlineapp.database.models.PdfModel

/**
 * @author aggarwalpulkit596
 */
@Dao
interface LibraryDao {
    @Query(
        """
        SELECT c.bookmarkUid,c.runAttemptId,c.contentId,c.sectionId,c.createdAt,b.title as contentName ,s.name as sectionName, b.contentable  FROM  BookmarkModel c
        INNER JOIN ContentModel b ON b.ccid = c.contentId
 	   INNER JOIN SectionModel s ON s.csid = c.sectionId
       WHERE c.runAttemptId = :id ORDER BY c.createdAt DESC
    """
    )
    fun getBookmarks(id: String): LiveData<List<BookmarkModel>>

    @Query("SELECT documentPdfLink, documentName, attempt_id, title FROM ContentModel WHERE ccid = :contentId")
    suspend fun getPDF(contentId: String): PdfModel

    @Query(
        """
        SELECT c.lectureUid,c.lectureName,c.lectureDuration,c.lectureDuration,c.lectureId,c.lectureSectionId,c.lectureUpdatedAt,c.isDownloaded,c.date,c.ccid as lectureContentId FROM  ContentModel c
       WHERE c.attempt_id = :attemptId AND c.isDownloaded = 1
    """
    )
    fun getDownloads(attemptId: String): LiveData<List<ContentLecture>>
}
