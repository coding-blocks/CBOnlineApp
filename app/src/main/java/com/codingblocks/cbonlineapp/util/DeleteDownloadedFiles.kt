package com.codingblocks.cbonlineapp.util

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.SectionWithContentsDao
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

class DeleteDownloadedFiles(context: Context, private val workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters), KoinComponent {

    override suspend fun doWork(): Result {

        val contentDao: ContentDao by inject()

        val contentId = workerParameters.inputData.getString(CONTENT_ID)

        val dir = File(applicationContext.getExternalFilesDir(Environment.getDataDirectory().absolutePath), contentId
            ?: "")
        MediaUtils.deleteRecursive(dir)
        contentDao.updateContentWithVideoId(contentId ?: "", 0)

        return Result.success()
    }

}
