package com.codingblocks.cbonlineapp.services

import android.app.IntentService
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.app.NotificationCompat
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.codingblocks.onlineapi.Clients
import okhttp3.ResponseBody
import org.jetbrains.anko.AnkoLogger
import java.io.*
import kotlin.concurrent.thread

class DownloadService : IntentService("Download Service"), AnkoLogger {

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val notificationBuilder by lazy {
        NotificationCompat.Builder(this, MediaUtils.DOWNLOAD_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_announcement)
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setProgress(0, 0, true)
                .setAutoCancel(true)
    }

    private var totalFileSize: Int = 0
    private lateinit var database: AppDatabase
    private lateinit var contentDao: ContentDao


    override fun onHandleIntent(intent: Intent) {


        notificationManager.notify(0, notificationBuilder.build())

        database = AppDatabase.getInstance(this)
        contentDao = database.contentDao()

        initDownload(intent)
    }

    private fun initDownload(intent: Intent) {
        var downloadCount = 0
        val url = intent.getStringExtra("url")
        Clients.initiateDownload(url, "index.m3u8").enqueue(retrofitCallback { _, response ->
            response?.body()?.let { indexResponse ->
                writeResponseBodyToDisk(indexResponse, url, "index.m3u8")
            }
        })

        Clients.initiateDownload(url, "video.key").enqueue(retrofitCallback { throwable, response ->
            response?.body()?.let { videoResponse ->
                writeResponseBodyToDisk(videoResponse, url, "video.key")
            }
        })

        Clients.initiateDownload(url, "video.m3u8").enqueue(retrofitCallback { throwable, response ->
            response?.body()?.let { keyResponse ->
                writeResponseBodyToDisk(keyResponse, url, "video.m3u8")
                val videoChunks = MediaUtils.getCourseDownloadUrls(url, this)
                videoChunks.forEach { videoName: String ->
                    Clients.initiateDownload(url, videoName).enqueue(retrofitCallback { throwable, response ->
                        val isDownloaded = writeResponseBodyToDisk(response?.body()!!, url, videoName)
                        if (isDownloaded) {
                            downloadCount++
                        }
                        if (downloadCount == videoChunks.size) {
                            onDownloadComplete()
                            thread {
                                contentDao.updateContent(intent.getStringExtra("id"), intent.getStringExtra("lectureContentId"))
                            }
                        }
                    })
                }
            }
        })
    }

    private fun writeResponseBodyToDisk(body: ResponseBody, videoUrl: String?, fileName: String): Boolean {
        try {

            val file = this.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
            val folderFile = File(file, "/$videoUrl")
            val dataFile = File(file, "/$videoUrl/$fileName")
            if (!folderFile.exists()) {
                folderFile.mkdir()
            }
            // todo change the file location/name according to your needs

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)

                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0


                inputStream = body.byteStream()
                outputStream = FileOutputStream(dataFile)

                while (true) {
                    val read = inputStream!!.read(fileReader)

                    if (read == -1) {
                        break
                    }

                    outputStream.write(fileReader, 0, read)

                    fileSizeDownloaded += read.toLong()

                }

                outputStream.flush()

                return true
            } catch (e: IOException) {
                return false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            return false
        }
    }

    private fun sendNotification(download: Int) {
        notificationBuilder!!.setProgress(100, download, false)
        notificationBuilder!!.setContentText(String.format("Downloaded (%d/%d) MB", download, download))
        notificationManager.notify(0, notificationBuilder!!.build())
    }


    private fun onDownloadComplete() {
        notificationManager.cancel(0)
        notificationBuilder!!.setProgress(0, 0, false)
        notificationBuilder!!.setContentText("File Downloaded")
        notificationManager.notify(0, notificationBuilder!!.build())
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        notificationManager.cancel(0)
    }

}
