package com.codingblocks.cbonlineapp.mycourse.quiz

import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.models.ContentQna
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizRepository(private var contentDao: ContentDao) {
    suspend fun getContent(contentId: String): ContentQna = withContext(Dispatchers.IO) { contentDao.getContent(contentId) }.contentQna
    suspend fun getQuizDetails(quizId: String) = safeApiCall { Clients.onlineV2JsonApi.getQuizById(quizId) }
    suspend fun getQuizAttempts(qnaId: String) = safeApiCall { Clients.onlineV2JsonApi.getQuizAttempt(qnaId) }
}
