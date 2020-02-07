package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.BookmarkModel

/**
 * @author aggarwalpulkit596
 */
@Dao
interface BookmarkDao : BaseDao<BookmarkModel> {
    @Query(
        """
        SELECT b.*,c.title as contentName ,s.name as sectionName FROM  BookmarkModel b
 	   INNER JOIN ContentModel c ON c.ccid = b.contentId
  	   INNER JOIN SectionModel s ON s.csid = b.sectionId
       WHERE b.contentId = :uid ORDER BY b.createdAt DESC
        """
    )
    fun getBookmarkById(uid: String): LiveData<BookmarkModel>

    @Query("DELETE FROM BookmarkModel where bookmarkUid = :uid")
    fun deleteBookmark(uid: String)
}
