package com.codingblocks.cbonlineapp.util

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.ContentsId
import com.codingblocks.onlineapi.models.Progress
import com.codingblocks.onlineapi.models.RunAttemptsId
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class ProgressWorker(context: Context, private val workerParameters: WorkerParameters) : Worker(context, workerParameters), KoinComponent {

    override fun doWork(): Result {
        val contentDao: ContentDao by inject()
        val contentId = workerParameters.inputData.getString(CONTENT_ID)
        val attemptId = workerParameters.inputData.getString(RUN_ATTEMPT_ID)
        val id = workerParameters.inputData.getString(ID)
        val progress = Progress()
        progress.status = "DONE"
        progress.runs = RunAttemptsId(attemptId)
        progress.content = ContentsId(contentId)
        val response = Clients.onlineV2JsonApi.setProgress(progress).execute()

        if (response.isSuccessful) {
            contentDao.updateProgress(contentId!!, attemptId!!, "DONE", response.body()?.id!!, id!!)
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
