package com.codingblocks.cbonlineapp.util

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.SectionWithContentsDao
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

class DeleteDownloadedSection (context: Context, private val workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters), KoinComponent {

    lateinit var sectionId: String
    lateinit var attemptId: String
    private val sectionWithContentsDao: SectionWithContentsDao by inject()
    private val contentDao: ContentDao by inject()

    override suspend fun doWork(): Result {

        attemptId = workerParameters.inputData.getString(RUN_ATTEMPT_ID) ?: ""
        sectionId = workerParameters.inputData.getString(SECTION_ID) ?: ""

        val list = withContext(Dispatchers.IO) { sectionWithContentsDao.getVideoIdsWithSectionId(sectionId, attemptId) }

        return if (list.isNotEmpty()) deleteSection(list) else Result.failure()
    }

    private suspend fun deleteSection(list: List<SectionContentHolder.DownloadableContent>): Result {

        list.forEach { content->
            val contentId = content.contentId
            val dir = File(applicationContext.getExternalFilesDir(Environment.getDataDirectory().absolutePath), contentId)
            MediaUtils.deleteRecursive(dir)
            contentDao.updateContent(content.contentId, 0)
        }

        return Result.success()
    }
}
