package com.codingblocks.cbonlineapp.mycourse.content.document

import com.codingblocks.cbonlineapp.database.BookmarkDao
import com.codingblocks.cbonlineapp.database.LibraryDao
import com.codingblocks.cbonlineapp.database.models.BookmarkModel
import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.models.Bookmark
import com.codingblocks.onlineapi.safeApiCall

class PdfActivityRepository(private val bookmarkDao: BookmarkDao, private val libraryDao: LibraryDao) {

    suspend fun removeBookmark(bookmarkUid: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.deleteBookmark(bookmarkUid) }

    fun getBookmark(contentId: String) = bookmarkDao.getBookmarkById(contentId)

    suspend fun updateBookmark(bookmark: Bookmark) {
        bookmarkDao.insert(
            BookmarkModel(
                bookmark.id ?: "",
                bookmark.runAttempt?.id ?: "",
                bookmark.content?.id ?: "",
                bookmark.section?.id ?: "",
                bookmark.createdAt ?: ""
            )
        )
    }

    suspend fun addBookmark(bookmark: Bookmark) = safeApiCall { CBOnlineLib.onlineV2JsonApi.addBookmark(bookmark) }

    fun deleteBookmark(id: String) = bookmarkDao.deleteBookmark(id)

    suspend fun getPdfBookmark(contentId: String) = libraryDao.getPDF(contentId)
}
