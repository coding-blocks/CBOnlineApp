package com.codingblocks.cbonlineapp.util

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.models.ContentModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.util.*

class DeleteDownloadedFiles(context: Context, private val workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters), KoinComponent {

    val contentDao: ContentDao by inject()

    override suspend fun doWork(): Result {
        val list:List<ContentModel> = contentDao.getDownloads(true)

        list.forEach {item->
            val folderFile = File(applicationContext.getExternalFilesDir(Environment.getDataDirectory().absolutePath), "/${item.contentLecture.lectureId}")
            val fileDate = Date(folderFile.lastModified())
            val expiringDate = Date(fileDate.time + 15)

            //Difference is number of days between expiring date and today.
            val diff = (expiringDate.time - Date().time)/(24 * 60 * 60 * 1000)
            if (diff<=0){
                MediaUtils.deleteRecursive(folderFile)
                contentDao.updateContentWithVideoId(item.contentLecture.lectureId, 0)
            }
        }
        return Result.success()
    }
}
