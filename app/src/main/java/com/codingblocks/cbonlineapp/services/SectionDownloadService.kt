package com.codingblocks.cbonlineapp.services

import android.app.Service
import android.content.Intent
import android.os.Environment
import android.os.IBinder
import android.util.Log
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.SectionWithContentsDao
import com.codingblocks.cbonlineapp.extensions.observeOnce
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.SECTION_ID
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
import org.jetbrains.anko.info
import org.koin.android.ext.android.inject
import java.io.File

class SectionDownloadService : Service(), VdoDownloadManager.EventListener, AnkoLogger {
    private val contentDao: ContentDao by inject()
    private val sectionWithContentsDao: SectionWithContentsDao by inject()
    private var sectionId: String? = null
    private var totalCount = 0
    private var completedCount = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sectionId = intent?.getStringExtra(SECTION_ID)
        sectionWithContentsDao.getVideoIdsWithSectionId(sectionId ?: "").observeOnce {
            //            NotificationCompat.Builder(this, MediaUtils.DOWNLOAD_CHANNEL_ID).apply {
//                setSmallIcon(R.drawable.ic_file_download)
//                setContentTitle("Downloading Section")
//                setOnlyAlertOnce(true)
//                setLargeIcon(BitmapFactory.decodeResource(this@SectionDownloadService.resources, R.mipmap.ic_launcher))
//                setContentText("Waiting to Download")
//                setProgress(100, 0, false)
//                color = resources.getColor(R.color.colorPrimaryDark)
//                setOngoing(true) // THIS is the important line
//                setAutoCancel(false)
//            }
            it.forEach { courseContent ->
                Clients.api.getOtp(courseContent.contentLecture.lectureId, courseContent.section_id, courseContent.attempt_id, true)
                    .enqueue(retrofitCallback { throwable, response ->
                        response?.let { json ->
                            if (json.isSuccessful) {
                                json.body()?.let {
                                    val mOtp = it.get("otp").asString
                                    val mPlaybackInfo = it.get("playbackInfo").asString
                                    initializeDownload(mOtp, mPlaybackInfo, courseContent.contentLecture.lectureId)
                                }
                            }
                        }
                    })
            }
        }
        return START_STICKY
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
                        this@SectionDownloadService.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
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

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onChanged(p0: String?, p1: DownloadStatus?) {
        info { p1?.downloadPercent }
    }

    override fun onDeleted(p0: String?) {
    }

    override fun onFailed(p0: String?, p1: DownloadStatus?) {
    }

    override fun onQueued(p0: String?, p1: DownloadStatus?) {
        info { "Queue" + p1?.status }
    }

    override fun onCompleted(videoId: String, p1: DownloadStatus?) {
        info { "Queue" + p1?.downloadPercent }

        completedCount++
        doAsync {
            sectionId?.let { contentDao.updateContentWithVideoId(it, videoId, "true") }
        }
        if (totalCount == completedCount) {
        }
    }
}
