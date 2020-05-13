package com.codingblocks.cbonlineapp.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.models.DownloadData
import com.codingblocks.cbonlineapp.mycourse.player.VideoPlayerActivity
import com.codingblocks.onlineapi.Clients
import com.google.gson.JsonObject
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.offline.DownloadOptions
import com.vdocipher.aegis.offline.DownloadRequest
import com.vdocipher.aegis.offline.DownloadSelections
import com.vdocipher.aegis.offline.DownloadStatus
import com.vdocipher.aegis.offline.OptionsDownloader
import com.vdocipher.aegis.offline.VdoDownloadManager
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import retrofit2.Response

class DownloadService : Service(), VdoDownloadManager.EventListener {

    private val downloadList = mutableListOf<DownloadData>()
    private var notificationId = 0

    private val contentDao: ContentDao by inject()

    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val downloadData = DownloadData(
                intent.getStringExtra(SECTION_ID),
                intent.getStringExtra(VIDEO_ID),
                intent.getStringExtra(RUN_ATTEMPT_ID),
                intent.getStringExtra(CONTENT_ID),
                notificationId++,
                NotificationCompat.Builder(this, DOWNLOAD_CHANNEL_ID).apply {
                    setSmallIcon(R.drawable.ic_file_download)
                    setContentTitle(intent.getStringExtra(TITLE))
                    setOnlyAlertOnce(true)
                    setLargeIcon(BitmapFactory.decodeResource(this@DownloadService.resources, R.mipmap.ic_launcher))
                    setContentText("Waiting to Download")
                    setProgress(100, 0, false)
                    color = resources.getColor(R.color.colorPrimaryDark)
                    setOngoing(true) // THIS is the important line
                    setAutoCancel(false)
                }
            )
            notificationManager.notify(downloadData.notificationId, downloadData.notificationBuilder.build())

            GlobalScope.launch {
                val response: Response<JsonObject> = withContext(Dispatchers.IO) { Clients.api.getOtp(downloadData.videoId, downloadData.sectionId, downloadData.attemptId, true) }
                if (response.isSuccessful) {
                    response.body()?.let {
                        downloadList.add(downloadData)
                        val mOtp = it.get("otp").asString
                        val mPlaybackInfo = it.get("playbackInfo").asString
                        initializeDownload(mOtp, mPlaybackInfo, downloadData.videoId)
                    }
                } else {
                    notificationManager.cancel(downloadData.notificationId)
                }
            }
        }
        return START_STICKY
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
                        this@DownloadService.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
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
                    // there was an error downloading the available options
                    Log.e("Service Error", "onOptionsNotReceived : $errDesc")
                }
            })
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        for (data in downloadList) {
            notificationManager.cancel(data.notificationId)
        }
        super.onDestroy()
    }

    private fun sendNotification(data: DownloadData, downloadPercent: Int) {
        data.notificationBuilder.setProgress(100, downloadPercent, false)
        data.notificationBuilder.setContentText("Downloaded $downloadPercent %")
        notificationManager.notify(data.notificationId, data.notificationBuilder.build())
    }

    private fun findDataWithId(videoId: String): DownloadData? {
        for (data in downloadList) {
            if (videoId == data.videoId)
                return data
        }
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        for (data in downloadList) {
            notificationManager.cancel(data.notificationId)
        }
        super.onTaskRemoved(rootIntent)
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
                data.notificationBuilder.setContentText("Download Failed")
                notificationManager.notify(data.notificationId, data.notificationBuilder.build())
            }
        }
    }

    override fun onQueued(videoId: String?, downloadStatus: DownloadStatus?) {
    }

    override fun onCompleted(videoId: String?, downloadStatus: DownloadStatus?) {
        if (videoId != null) {
            val data = findDataWithId(videoId)
            if (data != null) {
                GlobalScope.launch {
                    contentDao.updateContent(data.contentId, 1)
                }
                val intent = Intent(this, VideoPlayerActivity::class.java)
                intent.putExtra(VIDEO_ID, data.videoId)
                intent.putExtra(RUN_ATTEMPT_ID, data.attemptId)
                intent.putExtra(CONTENT_ID, data.contentId)
                intent.putExtra(DOWNLOADED, true)

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                val pendingIntent = PendingIntent.getActivity(
                    this, 0 /* Request code */, intent,
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
}
