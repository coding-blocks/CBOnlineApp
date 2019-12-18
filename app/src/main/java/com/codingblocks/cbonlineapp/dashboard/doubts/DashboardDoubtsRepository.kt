package com.codingblocks.cbonlineapp.dashboard.doubts

import com.codingblocks.cbonlineapp.database.DoubtsDao
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.safeApiCall
import java.util.*

class DashboardDoubtsRepository(private val doubtsDao: DoubtsDao) {

    suspend fun fetchDoubtsByCourseRun(id: String = "44872") = safeApiCall {
        Clients.onlineV2JsonApi.getDoubtByAttemptId(id)
    }


    suspend fun insertDoubts(doubts: ArrayList<Doubts>) {
        doubts.forEach {
            doubtsDao.insert(DoubtsModel(
                dbtUid = it.id,
                title = it.title,
                body = it.body,
                contentId = it.content?.id ?: "",
                status = it.status,
                runAttemptId = it.runAttempt?.id ?: "",
                discourseTopicId = it.discourseTopicId,
                conversationId = it.conversationId,
                createdAt = it.createdAt
            ))
        }
    }

    fun getDoubtsByCourseRun(id: String) = doubtsDao.getDoubts(id)
}
