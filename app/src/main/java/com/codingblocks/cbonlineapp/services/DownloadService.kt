package com.codingblocks.cbonlineapp.services

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.core.app.NotificationCompat
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.VideoPlayerActivity
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.DOWNLOADED
import com.codingblocks.cbonlineapp.util.LECTURE_CONTENT_ID
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.onlineapi.Clients
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.offline.DownloadOptions
import com.vdocipher.aegis.offline.DownloadRequest
import com.vdocipher.aegis.offline.DownloadSelections
import com.vdocipher.aegis.offline.DownloadStatus
import com.vdocipher.aegis.offline.OptionsDownloader
import com.vdocipher.aegis.offline.VdoDownloadManager
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.koin.android.ext.android.inject
import java.io.File

class DownloadService : IntentService("Download Service"), AnkoLogger,
    VdoDownloadManager.EventListener {

    lateinit var attemptId: String
    lateinit var contentId: String
    private lateinit var sectionId: String
    private lateinit var videoId: String
    lateinit var lectureContentId: String
    private lateinit var dataId: String

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private val notificationBuilder by lazy {
        NotificationCompat.Builder(this, MediaUtils.DOWNLOAD_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_file_download)
            .setContentTitle("Download")
            .setOnlyAlertOnce(true)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
            .setContentText("Downloading File")
            .setProgress(100, 0, false)
            .setColor(resources.getColor(R.color.colorPrimaryDark))
            .setOngoing(true) // THIS is the important line
            .setAutoCancel(false)
    }
    private val contentDao: ContentDao by inject()

    override fun onHandleIntent(intent: Intent) {
        val title = intent.getStringExtra("title")
        dataId = intent.getStringExtra("id")
        notificationBuilder.setContentTitle(title)
        notificationManager.notify(0, notificationBuilder.build())
        videoId = intent.getStringExtra(VIDEO_ID)
        attemptId = intent.getStringExtra(RUN_ATTEMPT_ID)
        sectionId = intent.getStringExtra(SECTION_ID)
        contentId = intent.getStringExtra(CONTENT_ID)
        lectureContentId = intent.getStringExtra(LECTURE_CONTENT_ID)

        Clients.api.getOtp(videoId, sectionId, attemptId, true)
            .enqueue(retrofitCallback { _, response ->
                response?.let { json ->
                    if (json.isSuccessful) {
                        json.body()?.let {
                            val mOtp = it.get("otp").asString
                            val mPlaybackInfo = it.get("playbackInfo").asString
                            initializeDownload(mOtp, mPlaybackInfo, videoId)
                        }
                    }
                }
            })
    }

    private fun initializeDownload(mOtp: String?, mPlaybackInfo: String?, videoId: String) {
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

    // function to update progress according to download progress
    private fun sendNotification(download: Int) {
        notificationBuilder.setProgress(100, download, false)
        notificationBuilder.setContentText("Downloaded $download %")
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun onDownloadComplete() {
        doAsync {
            contentDao.updateContent(dataId, lectureContentId, "true")
        }
        val intent = Intent(this, VideoPlayerActivity::class.java)
        intent.putExtra(VIDEO_ID, videoId)
        intent.putExtra(RUN_ATTEMPT_ID, attemptId)
        intent.putExtra(CONTENT_ID, contentId)
        intent.putExtra(DOWNLOADED, true)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        notificationManager.cancel(0)
        notificationBuilder.setProgress(0, 0, false)
        notificationBuilder.setContentText("File Downloaded")
        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.setOngoing(false)
        notificationBuilder.setAutoCancel(true)
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onChanged(p0: String?, downloadStatus: DownloadStatus?) {
        downloadStatus?.downloadPercent?.let { sendNotification(it) }
    }

    override fun onDeleted(p0: String?) {
    }

    override fun onFailed(p0: String?, p1: DownloadStatus?) {
        notificationManager.cancel(0)
        notificationBuilder.setOngoing(false)
        notificationBuilder.setContentText("Download Failed")
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onQueued(p0: String?, p1: DownloadStatus?) {
    }

    override fun onCompleted(p0: String?, p1: DownloadStatus?) {
        onDownloadComplete()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        notificationManager.cancel(0)
    }
}
