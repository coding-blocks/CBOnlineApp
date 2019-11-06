package com.codingblocks.cbonlineapp.util

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.ContentsId
import com.codingblocks.onlineapi.models.Progress
import com.codingblocks.onlineapi.models.RunAttemptsId
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response

class ProgressWorker(context: Context, private val workerParameters: WorkerParameters) : Worker(context, workerParameters), KoinComponent {

    override fun doWork(): Result {
        val contentDao: ContentDao by inject()
        val contentId = workerParameters.inputData.getString(CONTENT_ID)
        val attemptId = workerParameters.inputData.getString(RUN_ATTEMPT_ID)
        val progressId = workerParameters.inputData.getString(PROGRESS_ID)

        val progress = Progress()
        progress.status = "DONE"
        progress.runs = RunAttemptsId(attemptId)
        progress.content = ContentsId(contentId)
        val response: Response<Progress>
        if (progressId.isNullOrEmpty()) {
            response = Clients.onlineV2JsonApi.setProgress(progress).execute()
        } else {
            progress.id = progressId
            response = Clients.onlineV2JsonApi.updateProgress(progressId, progress).execute()
        }

        if (response.isSuccessful) {
            response.body()?.let {
                contentDao.updateProgress(it.contentId, it.runAttemptId, "DONE")
                contentDao.updateProgressID(it.contentId, it.runAttemptId, it.id)
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
