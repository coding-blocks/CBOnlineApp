package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.BookmarkModel

/**
 * @author aggarwalpulkit596
 */
@Dao
interface LibraryDao {
    @Query("""
        SELECT c.bookmarkUid,c.runAttemptId,c.contentId,c.sectionId,c.createdAt,c.title as contentName ,s.name as sectionName FROM  ContentModel c
 	   INNER JOIN SectionModel s ON s.csid = c.sectionId
       WHERE c.runAttemptId = :id ORDER BY c.createdAt DESC
    """)
    fun getBookmarks(id: String): LiveData<List<BookmarkModel>>
}
