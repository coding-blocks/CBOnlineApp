package com.codingblocks.cbonlineapp.mycourse.quiz

import com.codingblocks.cbonlineapp.database.BookmarkDao
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.models.BookmarkModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Bookmark
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.safeApiCall

class QuizRepository(private var contentDao: ContentDao,
                     private val bookmarkDao: BookmarkDao) {

    fun getContent(contentId: String) = contentDao.getContentLive(contentId)

    suspend fun getQuizDetails(quizId: String) = safeApiCall { Clients.onlineV2JsonApi.getQuizById(quizId) }

    suspend fun getQuizAttempts(qnaId: String) = safeApiCall { Clients.onlineV2JsonApi.getQuizAttempt(qnaId) }

    suspend fun createQuizAttempt(quizAttempt: QuizAttempt) = safeApiCall { Clients.onlineV2JsonApi.createQuizAttempt(quizAttempt) }

    suspend fun fetchQuizAttempt(quizAttemptId: String) = safeApiCall { Clients.onlineV2JsonApi.getQuizAttemptById(quizAttemptId) }

    suspend fun submitQuiz(quizAttemptId: String) = safeApiCall { Clients.onlineV2JsonApi.submitQuizById(quizAttemptId) }

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
