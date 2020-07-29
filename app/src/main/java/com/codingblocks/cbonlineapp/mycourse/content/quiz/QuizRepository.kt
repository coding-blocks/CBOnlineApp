package com.codingblocks.cbonlineapp.mycourse.content.quiz

import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.models.QuizAttempt
import com.codingblocks.onlineapi.safeApiCall

class QuizRepository(private var contentDao: ContentDao) {

    fun getContent(contentId: String) = contentDao.getContentLive(contentId)

    suspend fun getQuizDetails(quizId: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.getQuizById(quizId) }

    suspend fun getQuizAttempts(qnaId: String) = safeApiCall { CBOnlineLib.onlineV2JsonApi.getQuizAttempt(qnaId) }

    suspend fun createQuizAttempt(quizAttempt: QuizAttempt) = safeApiCall {
        CBOnlineLib.onlineV2JsonApi.createQuizAttempt(quizAttempt)
    }

    suspend fun fetchQuizAttempt(quizAttemptId: String) = safeApiCall {
        CBOnlineLib.onlineV2JsonApi.getQuizAttemptById(quizAttemptId)
    }

    suspend fun submitQuiz(quizAttemptId: String) = safeApiCall {
        CBOnlineLib.onlineV2JsonApi.submitQuizById(quizAttemptId)
    }
}
