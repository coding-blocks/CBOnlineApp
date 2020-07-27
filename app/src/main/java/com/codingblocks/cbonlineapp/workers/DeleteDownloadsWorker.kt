package com.codingblocks.cbonlineapp.workers

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.util.FileUtils
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.util.*

/**
 * Worker class to Auto-Delete downloaded files after 15 days.
 */
class DeleteDownloadsWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters), KoinComponent {

    val contentDao: ContentDao by inject()

    override suspend fun doWork(): Result {
        val list: List<ContentModel> = contentDao.getDownloads(true)

        list.forEach { item ->
            val folderFile = File(applicationContext.getExternalFilesDir(Environment.getDataDirectory().absolutePath), "/${item.contentLecture.lectureId}")
            val fileDate = Date(folderFile.lastModified())
            val calendar = Calendar.getInstance()
            calendar.time = Date(fileDate.time)
            calendar.add(Calendar.DATE, 14)
            val expiringDate = calendar.time
            val diff = (expiringDate.time - Date().time) / (24 * 60 * 60 * 1000)
            if (diff <= 0) {
                FileUtils.deleteRecursive(folderFile)
                contentDao.updateContentWithVideoId(item.contentLecture.lectureId, 0)
            }
        }
        return Result.success()
    }
}
