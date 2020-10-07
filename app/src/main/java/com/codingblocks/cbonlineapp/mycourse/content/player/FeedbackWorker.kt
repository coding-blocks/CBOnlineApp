package com.codingblocks.cbonlineapp.mycourse.content.player

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.models.ContentProgress
import com.codingblocks.onlineapi.models.LectureContent
import com.codingblocks.onlineapi.models.RunAttempts
import com.codingblocks.onlineapi.models.VideoFeedback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response

class FeedbackWorker(context: Context, private val workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters), KoinComponent {

    override suspend fun doWork(): Result {
        val contentDao: ContentDao by inject()
        val contentId = workerParameters.inputData.getString(CONTENT_ID)
        val attemptId = workerParameters.inputData.getString(RUN_ATTEMPT_ID)
        val rating = workerParameters.inputData.getFloat("rating", 0.0f)
        val reason = workerParameters.inputData.getString("reason")
        var progressId: String? =
            withContext(Dispatchers.IO) { contentDao.getProgressId(contentId ?: "") }
        if (progressId.isNullOrEmpty()) {
            progressId = null
        }
        val progress = ContentProgress(
            "ACTIVE", RunAttempts(attemptId ?: ""),
            LectureContent(
                contentId
                    ?: ""
            ),
            progressId,
            feedback = VideoFeedback(rating, reason ?: "")
        )
        val response: Response<ContentProgress> = if (progressId.isNullOrEmpty()) {
            CBOnlineLib.onlineV2JsonApi.setProgress(progress)
        } else {
            CBOnlineLib.onlineV2JsonApi.updateProgress(progressId, progress)
        }
        if (response.isSuccessful) {
            response.body()?.let {
                contentDao.update(it.contentId ?: "", it.runAttemptId ?: "", it.id ?: "", "ACTIVE")
            }
            return Result.success()
        } else {
            if (response.code() in (500..599)) {
                // try again if there is a server error
                return Result.retry()
            }
            return Result.failure()
        }
    }
}
