package com.codingblocks.cbonlineapp.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.models.DownloadData
import com.codingblocks.cbonlineapp.mycourse.content.player.VideoPlayerActivity
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.DOWNLOAD_CHANNEL_ID
import com.codingblocks.cbonlineapp.util.PreferenceHelper.Companion.getPrefs
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.TITLE
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.onlineapi.CBOnlineLib
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

class DownloadWorker(context: Context, private val workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters),
    KoinComponent,
    VdoDownloadManager.EventListener {

    private val notificationManager: NotificationManager by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val contentDao: ContentDao by inject()

    override suspend fun doWork(): Result {
        val contentId = workerParameters.inputData.getString(CONTENT_ID) ?: ""
        val attemptId = workerParameters.inputData.getString(RUN_ATTEMPT_ID) ?: ""
        val videoId = workerParameters.inputData.getString(VIDEO_ID) ?: ""
        val sectionId = workerParameters.inputData.getString(SECTION_ID) ?: ""
        val title = workerParameters.inputData.getString(TITLE) ?: ""
        if (downloadList.containsKey(videoId)) {
            return Result.success()
        }
        val downloadData = DownloadData(
            sectionId,
            videoId,
            attemptId,
            contentId,
            notificationId++,
            NotificationCompat.Builder(applicationContext, DOWNLOAD_CHANNEL_ID).apply {
                setSmallIcon(R.drawable.ic_file_download)
                setContentTitle(title)
                setOnlyAlertOnce(true)
                setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.ic_launcher))
                setContentText("Waiting to Download")
                setProgress(100, 0, false)
                color = ContextCompat.getColor(applicationContext, R.color.colorPrimaryDark)
                setOngoing(false) // THIS is the important line
                setAutoCancel(false)
            }
        )

        notificationManager.notify(downloadData.notificationId, downloadData.notificationBuilder.build())
        val response: Response<JsonObject>
        response = withContext(Dispatchers.IO) { CBOnlineLib.api.getOtp(downloadData.videoId, downloadData.sectionId, downloadData.attemptId, true) }
        if (response.isSuccessful) {
            response.body()?.let {
                downloadList[videoId] = (downloadData)
                val mOtp = it.get("otp").asString
                val mPlaybackInfo = it.get("playbackInfo").asString
                initializeDownload(mOtp, mPlaybackInfo, downloadData.videoId)
            }
            return Result.success()
        } else {
            for (data in downloadList) {
                notificationManager.cancel(data.value.notificationId)
            }
            if (response.code() in (500..599)) {
                // try again if there is a server error
                return Result.retry()
            }
            return Result.failure()
        }
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
                    var file = applicationContext.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
                    val directories =
                        applicationContext.getExternalFilesDirs(Environment.getDataDirectory().absolutePath)
                    if (getPrefs(applicationContext).SP_SD_CARD && directories.size > 1) {
                        file = directories[1]
                    } else {
                        getPrefs(applicationContext).SP_SD_CARD = false
                    }
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
                        vdoDownloadManager.addEventListener(this@DownloadWorker)
                    } catch (e: IllegalArgumentException) {
                    } catch (e: IllegalStateException) {
                    }
                }

                override fun onOptionsNotReceived(errDesc: ErrorDescription) {
                    // there was an error downloading the available options
                    Log.e("Service Error", "onOptionsNotReceived : $errDesc")
                }
            }
        )
    }

    private fun sendNotification(data: DownloadData, downloadPercent: Int) {
        data.notificationBuilder.setProgress(100, downloadPercent, false)
        data.notificationBuilder.setContentText("Downloaded $downloadPercent %")
        notificationManager.notify(data.notificationId, data.notificationBuilder.build())
    }

    private fun findDataWithId(videoId: String): DownloadData? {
        for (data in downloadList) {
            if (videoId == data.key)
                return data.value
        }
        return null
    }

    // VdoDownloadManager Events
    override fun onChanged(videoId: String?, downloadStatus: DownloadStatus) {
        if (videoId != null) {
            val data = findDataWithId(videoId)
            if (data != null)
                sendNotification(data, downloadStatus.downloadPercent)
        }
    }

    override fun onDeleted(videoId: String?) {
    }

    override fun onFailed(videoId: String?, downloadStatus: DownloadStatus?) {
        if (videoId != null) {
            val data = findDataWithId(videoId)
            if (data != null) {
                notificationManager.cancel(data.notificationId)
                data.notificationBuilder.setOngoing(false)
                data.notificationBuilder.setContentText("Download Failed.Retrying")
                GlobalScope.launch(Dispatchers.IO) {
                    contentDao.updateContent(data.contentId, 0)
                }
                retryDownload(data)
                notificationManager.notify(data.notificationId, data.notificationBuilder.build())
            }
        }
    }

    private fun retryDownload(downloadData: DownloadData) {
        GlobalScope.launch {
            val response: Response<JsonObject> = withContext(Dispatchers.IO) { CBOnlineLib.api.getOtp(downloadData.videoId, downloadData.sectionId, downloadData.attemptId, true) }
            if (response.isSuccessful) {
                response.body()?.let {
                    val mOtp = it.get("otp").asString
                    val mPlaybackInfo = it.get("playbackInfo").asString
                    initializeDownload(mOtp, mPlaybackInfo, downloadData.videoId)
                }
            }
        }
    }

    override fun onQueued(videoId: String?, downloadStatus: DownloadStatus?) {
    }

    override fun onCompleted(videoId: String?, downloadStatus: DownloadStatus?) {
        if (videoId != null) {
            val data = findDataWithId(videoId)
            if (data != null) {
                GlobalScope.launch(Dispatchers.IO) {
                    contentDao.updateContent(data.contentId, 1)
                }
                val intent = Intent(applicationContext, VideoPlayerActivity::class.java)
                intent.putExtra(CONTENT_ID, data.contentId)
                intent.putExtra(SECTION_ID, data.sectionId)

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val pendingIntent = PendingIntent.getActivity(
                    applicationContext, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT
                )
                notificationManager.cancel(data.notificationId)
                data.notificationBuilder.setProgress(0, 0, false)
                data.notificationBuilder.setContentText("File Downloaded")
                data.notificationBuilder.setContentIntent(pendingIntent)
                data.notificationBuilder.setOngoing(false)
                data.notificationBuilder.setAutoCancel(true)
                notificationManager.notify(data.notificationId, data.notificationBuilder.build())
            }
        }
    }

    companion object {
        @JvmStatic
        var notificationId = 0
        private val downloadList = hashMapOf<String, DownloadData>()
    }
}
