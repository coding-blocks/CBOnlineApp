package com.codingblocks.cbonlineapp.util

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.DownloadsDao
import com.codingblocks.cbonlineapp.database.models.DownloadModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

class DeleteDownloadedFiles(context: Context, private val workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters), KoinComponent {

    val contentDao: ContentDao by inject()
    val downloadsDao: DownloadsDao by inject()

    override suspend fun doWork(): Result {
        val list:List<DownloadModel> = downloadsDao.getAllDownloads()
        val today = DateUtils.getToday()

        list.forEach {item->
            if (item.date != today)
                return@forEach
            val file =
                applicationContext.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
            val folderFile = File(
                file,
                "/${item.videoId}"
            )
            MediaUtils.deleteRecursive(folderFile)
            contentDao.updateContentWithVideoId(item.videoId, 0)
        }
        return Result.success()
    }
}
