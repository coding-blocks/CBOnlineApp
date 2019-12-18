package com.codingblocks.cbonlineapp.dashboard.doubts

import androidx.lifecycle.LiveData
import com.codingblocks.cbonlineapp.database.CommentsDao
import com.codingblocks.cbonlineapp.database.DoubtsDao
import com.codingblocks.cbonlineapp.database.models.CommentModel
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.util.LIVE
import com.codingblocks.cbonlineapp.util.RESOLVED
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Comment
import com.codingblocks.onlineapi.models.ContentsId
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.models.MyRunAttempts
import com.codingblocks.onlineapi.safeApiCall
import java.util.*

class DashboardDoubtsRepository(private val doubtsDao: DoubtsDao,
                                private val commentsDao: CommentsDao) {

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

    fun getDoubtsByCourseRun(type: String): LiveData<List<DoubtsModel>> {
        return when (type) {
            LIVE -> doubtsDao.getLiveDoubts("44872")
            RESOLVED -> doubtsDao.getResolveDoubts("44872")
            else -> doubtsDao.getDoubts("44872")
        }

    }

    suspend fun resolveDoubt(doubt: DoubtsModel) =
        safeApiCall {
            Clients.onlineV2JsonApi.resolveDoubt(doubt.dbtUid,
                Doubts(
                    id = doubt.dbtUid,
                    title = doubt.title,
                    body = doubt.body,
                    discourseTopicId = doubt.discourseTopicId,
                    runAttempt = MyRunAttempts(doubt.runAttemptId),
                    conversationId = doubt.conversationId,
                    content = ContentsId(doubt.contentId),
                    status = doubt.status
                ))
        }

    fun getDoubtById(id: String) = doubtsDao.getDoubtById(id)

    suspend fun fetchCommentsByDoubtId(id: String) = safeApiCall {
        Clients.onlineV2JsonApi.getCommentsById(id)
    }

    suspend fun insertComments(comments: ArrayList<Comment>) {
        comments.forEach {
            commentsDao.insert(CommentModel(
                it.id,
                it.body,
                it.doubt?.id ?: "",
                it.updatedAt,
                it.username
            ))
        }
    }

    fun getCommentsById(id: String) = commentsDao.getComments(id)

}
