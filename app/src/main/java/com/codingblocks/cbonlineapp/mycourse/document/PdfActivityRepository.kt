package com.codingblocks.cbonlineapp.mycourse.document

import com.codingblocks.cbonlineapp.database.BookmarkDao
import com.codingblocks.cbonlineapp.database.models.BookmarkModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Bookmark
import com.codingblocks.onlineapi.safeApiCall

class PdfActivityRepository(private val bookmarkDao: BookmarkDao) {

    suspend fun removeBookmark(bookmarkUid: String) = safeApiCall { Clients.onlineV2JsonApi.deleteBookmark(bookmarkUid) }

    fun getBookmark(contentId: String) = bookmarkDao.getBookmarkById(contentId)

    suspend fun updateBookmark(bookmark: Bookmark) {
        bookmarkDao.insert(BookmarkModel(bookmark.id ?: "",
            bookmark.runAttempt?.id ?: "",
            bookmark.content?.id ?: "",
            bookmark.section?.id ?: "",
            bookmark.createdAt ?: ""))
    }

    suspend fun markDoubt(bookmark: Bookmark) = safeApiCall { Clients.onlineV2JsonApi.addBookmark(bookmark) }

    fun deleteBookmark(id: String) = bookmarkDao.deleteBookmark(id)

}
