package com.codingblocks.cbonlineapp.dashboard.doubts

import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import com.codingblocks.cbonlineapp.database.CommentsDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.DoubtsDao
import com.codingblocks.cbonlineapp.database.RunAttemptDao
import com.codingblocks.cbonlineapp.database.models.CommentModel
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.util.LIVE
import com.codingblocks.cbonlineapp.util.RESOLVED
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Comment
import com.codingblocks.onlineapi.models.Doubts
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.RunAttempts
import com.codingblocks.onlineapi.safeApiCall

class DashboardDoubtsRepository(
    private val doubtsDao: DoubtsDao,
    private val commentsDao: CommentsDao,
    private val runDao: CourseWithInstructorDao,
    private val runAttemptDao: RunAttemptDao
) {

    suspend fun fetchDoubtsByCourseRun(id: String) = safeApiCall { Clients.onlineV2JsonApi.getDoubtByAttemptId(id) }

    suspend fun fetchCommentsByDoubtId(id: String) = safeApiCall { Clients.onlineV2JsonApi.getCommentsById(id) }

    suspend fun insertDoubts(doubts: List<Doubts>) {
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

    suspend fun resolveDoubt(doubt: DoubtsModel) =
        safeApiCall {
            Clients.onlineV2JsonApi.resolveDoubt(doubt.dbtUid,
                Doubts(
                    id = doubt.dbtUid,
                    title = doubt.title,
                    body = doubt.body,
                    discourseTopicId = doubt.discourseTopicId,
                    runAttempt = RunAttempts(doubt.runAttemptId),
                    conversationId = doubt.conversationId,
                    content = LectureContent(doubt.contentId),
                    status = doubt.status,
                    createdAt = doubt.createdAt
                ))
        }

    suspend fun insertComments(comments: List<Comment>) {
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

    fun getDoubtsByCourseRun(type: String?, attemptId: String = ""): LiveData<List<DoubtsModel>> {
        return when (type) {
            LIVE -> doubtsDao.getLiveDoubts(attemptId)
            RESOLVED -> doubtsDao.getResolveDoubts(attemptId)
            else -> doubtsDao.getDoubts(attemptId).distinctUntilChanged()
        }
    }

    fun getDoubtById(id: String) = doubtsDao.getDoubtById(id)
    fun getCommentsById(id: String) = commentsDao.getComments(id)
    fun getRuns() = runDao.getActiveRuns(System.currentTimeMillis() / 1000).distinctUntilChanged()
    suspend fun createComment(comment: Comment) = safeApiCall { Clients.onlineV2JsonApi.createComment(comment) }
    suspend fun insertComment(it: Comment) {
        commentsDao.insert(CommentModel(
            it.id,
            it.body,
            it.doubt?.id ?: "",
            it.updatedAt,
            it.username
        ))
    }

    suspend fun updateDb(dbtUid: String) {
        doubtsDao.updateStatus(dbtUid, "RESOLVED")
    }

    fun getRunAttempt(id: String) = runAttemptDao.getRunAttempt(id)
}
