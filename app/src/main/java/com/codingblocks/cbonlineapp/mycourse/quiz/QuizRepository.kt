package com.codingblocks.cbonlineapp.mycourse.quiz

import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.models.ContentQnaModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizRepository(private var contentDao: ContentDao) {

    suspend fun getContent(contentId: String): ContentQnaModel = withContext(Dispatchers.IO) { contentDao.getContent(contentId) }.contentQna

    suspend fun getQuizDetails(quizId: String) = safeApiCall { Clients.onlineV2JsonApi.getQuizById(quizId) }

    suspend fun getQuizAttempts(qnaId: String) = safeApiCall { Clients.onlineV2JsonApi.getQuizAttempt(qnaId) }

    suspend fun createQuizAttempt(quizAttempt: QuizAttempt) = safeApiCall { Clients.onlineV2JsonApi.createQuizAttempt(quizAttempt) }

    suspend fun fetchQuizAttempt(quizAttemptId: String) = safeApiCall { Clients.onlineV2JsonApi.getQuizAttemptById(quizAttemptId) }
}
