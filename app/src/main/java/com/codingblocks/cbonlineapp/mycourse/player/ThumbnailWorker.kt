package com.codingblocks.cbonlineapp.mycourse.player

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.database.PlayerDao
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.onlineapi.models.Thumbnail
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.koin.core.KoinComponent
import org.koin.core.inject

class ThumbnailWorker(context: Context, private val workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters), KoinComponent {

    override suspend fun doWork(): Result {
        val playerDao: PlayerDao by inject()
        val videoId = workerParameters.inputData.getString(VIDEO_ID)
        val contentId = workerParameters.inputData.getString(CONTENT_ID)
        val request = Request.Builder()
            .url("https://dev.vdocipher.com/api/meta/$videoId")
            .build()

        val response: Response = withContext(Dispatchers.IO) { OkHttpClient().newCall(request).execute() }

        if (response.isSuccessful) {
            val data = Gson().fromJson(response.body?.string(), Thumbnail::class.java)
            val thumbnail = data.posters.firstOrNull()?.url

            return if (thumbnail.isNullOrBlank()) return Result.failure()
            else {
                contentId?.let { playerDao.updateThumbnail(thumbnail, it) }
                Result.success()
            }
        } else {
            if (response.code in (500..599)) {
                // try again if there is a server error
                return Result.retry()
            }
            return Result.failure()
        }
    }
}
