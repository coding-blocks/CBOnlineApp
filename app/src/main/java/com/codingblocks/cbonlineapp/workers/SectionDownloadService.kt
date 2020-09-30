package com.codingblocks.cbonlineapp.workers

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
import androidx.core.content.ContextCompat
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.SectionWithContentsDao
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder
import com.codingblocks.cbonlineapp.util.*
import com.codingblocks.onlineapi.CBOnlineLib
import com.google.gson.JsonObject
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.offline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import retrofit2.Response
import java.io.File

/**
 * A Foreground Service to download files
 */

class SectionDownloadService : Service(), VdoDownloadManager.EventListener {

    companion object {
        fun startService(context: Context, sectionId: String, attemptId: String) {
            val startIntent = Intent(context, SectionDownloadService::class.java)
            startIntent.putExtra(SECTION_ID, sectionId)
            startIntent.putExtra(RUN_ATTEMPT_ID, attemptId)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, SectionDownloadService::class.java)
            context.stopService(stopIntent)
        }

        const val NOTIFICATION_ID = 10
        const val ACTION_STOP = "ACTION_STOP_FOREGROUND_SERVICE"
        private val downloadList = hashMapOf<String, SectionContentHolder.DownloadableContent>()
    }

    private val notificationManager by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private val vdoDownloadManager by lazy {
        VdoDownloadManager.getInstance(applicationContext)
    }

    private lateinit var notification: NotificationCompat.Builder

    private val contentDao: ContentDao by inject()
    private val sectionWithContentsDao: SectionWithContentsDao by inject()

    var sectionId: String? = null
    var attemptId: String? = null
    var sectionName: String? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == ACTION_STOP) {
            stopServiceManually()
            notificationManager.cancel(NOTIFICATION_ID)
        } else {
            downloadList.clear()
            attemptId = intent.getStringExtra(RUN_ATTEMPT_ID)
            sectionId = intent.getStringExtra(SECTION_ID)
            GlobalScope.launch {
                createNotification(
                    withContext(Dispatchers.IO) {
                        sectionWithContentsDao.getVideoIdsWithSectionId(sectionId!!, attemptId!!)
                    }
                )
            }
        }
        return START_NOT_STICKY
    }

    private suspend fun createNotification(sectionList: List<SectionContentHolder.DownloadableContent>) {
        if (sectionList.isNotEmpty()) {
            sectionName = sectionList.first().name
            notification = NotificationCompat.Builder(applicationContext, DOWNLOAD_CHANNEL_ID).apply {
                setSmallIcon(R.drawable.ic_file_download)
                setContentTitle("Downloading $sectionName")
                setOnlyAlertOnce(true)
                setStyle(NotificationCompat.BigTextStyle().bigText("0 out of ${sectionList.size} downloaded"))
                setProgress(sectionList.size, 0, false)
                setOngoing(true)
            }
            startDownload(sectionList)
            val stopSelf = Intent(this, SectionDownloadService::class.java)
            stopSelf.action = ACTION_STOP
            val pStopSelf =
                PendingIntent.getService(this, 0, stopSelf, /*Stop Service*/PendingIntent.FLAG_CANCEL_CURRENT)
            notification.addAction(R.drawable.ic_pause_white_24dp, "Cancel", pStopSelf)
            startForeground(1, notification.build())
        } else {
            stopServiceManually()
        }
    }

    private fun stopServiceManually() {
        vdoDownloadManager.removeEventListener(this)
        stopForeground(true)
        stopSelf()
    }

    private suspend fun startDownload(list: List<SectionContentHolder.DownloadableContent>) {
        list.forEach { content ->
            val response: Response<JsonObject> = withContext(Dispatchers.IO) {
                CBOnlineLib.api.getOtp(content.videoId, content.sectionId, attemptId!!, true)
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
                    // enqueue request to VdoDownloadManager for download
                    try {
                        vdoDownloadManager.enqueue(request)
                        vdoDownloadManager.addEventListener(this@SectionDownloadService)
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

    /** This function will be invoked when the progress of any download changes*/
    override fun onChanged(p0: String?, p1: DownloadStatus?) {
        notification.apply {
            setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${downloadList.filterValues { it.isDownloaded }.size} out of ${downloadList.size} downloaded( Current ${p1?.downloadPercent}% )")
            )
        }
        notificationManager.notify(1, notification.build())
    }

    override fun onDeleted(p0: String?) {
    }

    /**
     * This function will be invoked when the download fails
     * it will remove the files when may have been downloaded and got corrupted
     */
    override fun onFailed(videoId: String, p1: DownloadStatus?) {
        val folderFile =
            File(applicationContext.getExternalFilesDir(Environment.getDataDirectory().absolutePath), "/$videoId")
        downloadList.remove(videoId)
        FileUtils.deleteRecursive(folderFile)
    }

    override fun onQueued(p0: String?, p1: DownloadStatus?) {
    }

    /**
     * Updates the [ContentModel] along with updating the notification.
     */
    override fun onCompleted(videoId: String, p1: DownloadStatus?) {
        val data = downloadList[videoId]
        GlobalScope.launch(Dispatchers.IO) {
            contentDao.updateContent(data?.contentId ?: "", 1)
        }
        downloadList[videoId]?.isDownloaded = true
        notification.apply {
            setProgress(downloadList.size, downloadList.filterValues { it.isDownloaded }.size, false)
            setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${downloadList.filterValues { it.isDownloaded }.size} out of ${downloadList.size} downloaded")
            )
        }
        notificationManager.notify(1, notification.build())
        if (downloadList.filterValues { !it.isDownloaded }.isEmpty()) {
            stopServiceManually()
        }
    }

    /** Creates a new notification after completion of the task*/
    private fun createCompletionNotification() {
        notification = NotificationCompat.Builder(applicationContext, DOWNLOAD_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_file_download)
            setContentTitle("Downloaded $sectionName")
            setOnlyAlertOnce(true)
            setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.ic_launcher))
            setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${downloadList.filterValues { it.isDownloaded }.size} out of ${downloadList.size} downloaded")
            )
        }
        notificationManager.notify(2, notification.build())
    }
}
