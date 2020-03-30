package com.codingblocks.cbonlineapp.util

import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.SectionWithContentsDao
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder.DownloadableContent
import com.codingblocks.onlineapi.Clients
import com.google.gson.JsonObject
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.offline.DownloadOptions
import com.vdocipher.aegis.offline.DownloadRequest
import com.vdocipher.aegis.offline.DownloadSelections
import com.vdocipher.aegis.offline.DownloadStatus
import com.vdocipher.aegis.offline.OptionsDownloader
import com.vdocipher.aegis.offline.VdoDownloadManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response
import java.io.File

class SectionDownloadService(val context: Context, private val workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters),
    KoinComponent,
    VdoDownloadManager.EventListener {

    private val notificationManager by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private val contentDao: ContentDao by inject()
    private val sectionWithContentsDao: SectionWithContentsDao by inject()
    lateinit var sectionId: String
    lateinit var attemptId: String
    private var totalCount = 0
    private var completedCount = 0
    private lateinit var notification: NotificationCompat.Builder

    override suspend fun doWork(): Result {
        totalCount = 0
        completedCount = 0
        attemptId = workerParameters.inputData.getString(RUN_ATTEMPT_ID) ?: ""
        sectionId = workerParameters.inputData.getString(SECTION_ID) ?: ""

        val list = withContext(Dispatchers.IO) { sectionWithContentsDao.getVideoIdsWithSectionId(sectionId, attemptId) }

        return if (list.isNotEmpty()) downloadSection(list) else Result.failure()
    }

    private suspend fun downloadSection(list: List<DownloadableContent>): Result {
        totalCount = list.size
        notification = NotificationCompat.Builder(context, DOWNLOAD_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_file_download)
            setContentTitle("Downloading Section")
            setOnlyAlertOnce(true)
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            setContentText("0 out of $totalCount downloaded")
            setProgress(totalCount, 0, false)
            setOngoing(true)
            setAutoCancel(false)
        }
        notificationManager.notify(1, notification.build())
        val result = withContext(Dispatchers.IO) {
            startDownload(list)
            true
        }
        return if (result) Result.success() else Result.failure()
    }

    private suspend fun startDownload(list: List<DownloadableContent>) {
        list.forEach { content ->
            val response: Response<JsonObject> = withContext(Dispatchers.IO) {
                Clients.api.getOtp(content.videoId, content.sectionId, attemptId ?: "", true)
            }
            if (response.isSuccessful) {
                response.body()?.let {
                    downloadList[content.videoId] = (content)
                    val mOtp = it.get("otp").asString
                    val mPlaybackInfo = it.get("playbackInfo").asString
                    initializeDownload(mOtp, mPlaybackInfo, content.videoId)
                }
            }
        }
    }

    override fun onChanged(p0: String?, p1: DownloadStatus?) {
        notification.apply {
            setProgress(totalCount, completedCount, false)
            setContentText("$completedCount out of $totalCount downloaded(Current ${p1?.downloadPercent}% )")
        }
        notificationManager.notify(1, notification.build())
    }

    override fun onDeleted(p0: String?) {
    }

    override fun onFailed(p0: String?, p1: DownloadStatus?) {
    }

    override fun onQueued(p0: String?, p1: DownloadStatus?) {
    }

    override fun onCompleted(videoId: String, p1: DownloadStatus?) {
        completedCount++
        val data = findDataWithId(videoId)
        GlobalScope.launch(Dispatchers.IO) {
            contentDao.updateContent(data?.contentId ?: "", 1)
        }
        notification.apply {
            setProgress(totalCount, completedCount, false)
            setContentText("$completedCount out of $totalCount downloaded")
        }
        notificationManager.notify(1, notification.build())
        if (totalCount == completedCount) {
            notification.apply {
                setProgress(0, 0, false)
                setContentText("Section Downloaded")
                setOngoing(false)
                setAutoCancel(true)
            }
            notificationManager.notify(1, notification.build())
        }
    }

    private fun findDataWithId(videoId: String): DownloadableContent? {
        for (data in downloadList) {
            if (videoId == data.key)
                return data.value
        }
        return null
    }

    companion object {
        private val downloadList = hashMapOf<String, DownloadableContent>()
    }

    private fun initializeDownload(mOtp: String, mPlaybackInfo: String, videoId: String) {
        val optionsDownloader = OptionsDownloader()
        // assuming we have otp and playbackInfo
        optionsDownloader.downloadOptionsWithOtp(
            mOtp,
            mPlaybackInfo,
            object : OptionsDownloader.Callback {
                override fun onOptionsReceived(options: DownloadOptions) {
                    // we have received the available download options
                    val selectionIndices = intArrayOf(0, 1)
                    val downloadSelections = DownloadSelections(options, selectionIndices)
                    val file =
                        applicationContext.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
                    val folderFile = File(file, "/$videoId")
                    if (!folderFile.exists()) {
                        folderFile.mkdir()
                    }
                    val request =
                        DownloadRequest.Builder(downloadSelections, folderFile.absolutePath).build()
                    val vdoDownloadManager = VdoDownloadManager.getInstance(applicationContext)
                    // enqueue request to VdoDownloadManager for download
                    try {
                        vdoDownloadManager.enqueue(request)
                        vdoDownloadManager.addEventListener(this@SectionDownloadService)
                    } catch (e: IllegalArgumentException) {
                    } catch (e: IllegalStateException) {
                    }
                }

                override fun onOptionsNotReceived(errDesc: ErrorDescription) {
                    // there was an error downloading the available options
                    Log.e("Service Error", "onOptionsNotReceived : $errDesc")
                }
            })
    }
}
