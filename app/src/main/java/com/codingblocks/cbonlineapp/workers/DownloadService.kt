package com.codingblocks.cbonlineapp.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.database.models.DownloadData
import com.codingblocks.cbonlineapp.mycourse.content.player.VideoPlayerActivity
import com.codingblocks.cbonlineapp.util.*
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
import org.koin.android.ext.android.inject
import retrofit2.Response
import java.io.File

class DownloadService : Service(), VdoDownloadManager.EventListener {

    companion object {
        fun startService(
            context: Context,
            sectionId: String,
            attemptId: String,
            videoId: String,
            contentId: String,
            title: String
        ) {
            val startIntent = Intent(context, DownloadService::class.java)
            startIntent.putExtra(SECTION_ID, sectionId)
            startIntent.putExtra(RUN_ATTEMPT_ID, attemptId)
            startIntent.putExtra(CONTENT_ID, contentId)
            startIntent.putExtra(TITLE, title)
            startIntent.putExtra(VIDEO_ID, videoId)

            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, DownloadService::class.java)
            context.stopService(stopIntent)
        }

        const val NOTIFICATION_ID = 10
        const val ACTION_STOP = "ACTION_STOP_FOREGROUND_SERVICE"
        var notificationId = 1
        private val downloadList = hashMapOf<String, DownloadData>()
    }

    private val notificationManager by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private lateinit var notification: NotificationCompat.Builder

    private val contentDao: ContentDao by inject()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == ACTION_STOP) {
            stopServiceManually()
            notificationManager.cancel(NOTIFICATION_ID)
        } else {
            val contentId = intent.getStringExtra(CONTENT_ID) ?: ""
            val attemptId = intent.getStringExtra(RUN_ATTEMPT_ID) ?: ""
            val videoId = intent.getStringExtra(VIDEO_ID) ?: ""
            val sectionId = intent.getStringExtra(SECTION_ID) ?: ""
            val title = intent.getStringExtra(TITLE) ?: ""
            if (!downloadList.containsKey(videoId)) {
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
                        setContentText("Waiting to Download")
                        setProgress(100, 0, false)
                        setOngoing(true)
                    }
                )
                createNotification(downloadData)
            }
        }
        return START_NOT_STICKY
    }

    private fun createNotification(downloadData: DownloadData) {
        GlobalScope.launch {
            startDownload(downloadData)
        }
        val stopSelf = Intent(this, DownloadService::class.java)
        stopSelf.action = ACTION_STOP
        val pStopSelf = PendingIntent.getService(this, 0, stopSelf, /*Stop Service*/PendingIntent.FLAG_CANCEL_CURRENT)
        downloadData.notificationBuilder.addAction(R.drawable.ic_pause_white_24dp, "Cancel", pStopSelf)
        startForeground(downloadData.notificationId, downloadData.notificationBuilder.build())
    }

    private fun stopServiceManually() {
        stopForeground(true)
        stopSelf()
    }

    private suspend fun startDownload(downloadData: DownloadData) {
        val response: Response<JsonObject> = withContext(Dispatchers.IO) {
            CBOnlineLib.api.getOtp(downloadData.videoId, downloadData.sectionId, downloadData.attemptId, true)
        }
        if (response.isSuccessful) {
            response.body()?.let {
                downloadList[downloadData.videoId] = (downloadData)
                val mOtp = it.get("otp").asString
                val mPlaybackInfo = it.get("playbackInfo").asString
                initializeDownload(mOtp, mPlaybackInfo, downloadData.videoId)
            }
        }
    }

    private fun initializeDownload(mOtp: String, mPlaybackInfo: String, videoId: String) {
        val optionsDownloader = OptionsDownloader()
        optionsDownloader.downloadOptionsWithOtp(
            mOtp, mPlaybackInfo,
            object : OptionsDownloader.Callback {
                override fun onOptionsReceived(options: DownloadOptions) {
                    // we have received the available download options
                    val selectionIndices = intArrayOf(0, 1)
                    val downloadSelections = DownloadSelections(options, selectionIndices)
                    var file = applicationContext.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
                    val directories =
                        applicationContext.getExternalFilesDirs(Environment.getDataDirectory().absolutePath)
                    if (PreferenceHelper.getPrefs(applicationContext).SP_SD_CARD && directories.size > 1) {
                        file = directories[1]
                    } else {
                        PreferenceHelper.getPrefs(applicationContext).SP_SD_CARD = false
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
                        vdoDownloadManager.addEventListener(this@DownloadService)
                    } catch (e: IllegalArgumentException) {
                    } catch (e: IllegalStateException) {
                    }
                }

                override fun onOptionsNotReceived(errDesc: ErrorDescription) {
                    Log.e("Service Error", "onOptionsNotReceived : $errDesc")
                }
            }
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
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

    /** This function will be invoked when the progress of any download changes*/
    override fun onChanged(videoId: String, downloadStatus: DownloadStatus) {
        val data = findDataWithId(videoId)
        if (data != null)
            sendNotification(data, downloadStatus.downloadPercent)
    }

    override fun onDeleted(p0: String?) {
    }

    /**
     * This function will be invoked when the download fails
     * it will remove the files when may have been downloaded and got corrupted
     */
    override fun onFailed(videoId: String, p1: DownloadStatus?) {
        val data = findDataWithId(videoId)
        if (data != null) {
            notificationManager.cancel(data.notificationId)
            val folderFile = File(applicationContext.getExternalFilesDir(Environment.getDataDirectory().absolutePath), "/$videoId")
            FileUtils.deleteRecursive(folderFile)
        }
    }

    override fun onQueued(p0: String?, p1: DownloadStatus?) {
    }

    /**
     * Updates the [ContentModel] along with updating the notification.
     */
    override fun onCompleted(videoId: String, p1: DownloadStatus?) {
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
            downloadList.remove(videoId)
            if (downloadList.isEmpty()) {
                stopServiceManually()
            }
        }
    }
}
