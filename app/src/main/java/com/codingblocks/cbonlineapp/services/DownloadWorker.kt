package com.codingblocks.cbonlineapp.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.CBOnlineApp
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.activities.VideoPlayerActivity
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.codingblocks.onlineapi.Clients
import okhttp3.ResponseBody
import org.jetbrains.anko.toast
import java.io.*
import kotlin.concurrent.thread

class DownloadWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun onStopped() {
        super.onStopped()
//        contentDao.updateContent(intent.getString("id")!!, intent.getString("lectureContentId")!!, "false")
        notificationManager.cancel(0)
        notificationBuilder.setOngoing(false)
        notificationBuilder.setContentText("Download Failed")

        applicationContext.toast("There was some issue with your network.Please Try Again !!")    }


    private val notificationManager by lazy {
        ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private val notificationBuilder by lazy {
        NotificationCompat.Builder(ctx, MediaUtils.DOWNLOAD_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_file_download)
                .setContentTitle("Download")
                .setOnlyAlertOnce(true)
                .setLargeIcon(BitmapFactory.decodeResource(ctx.resources, R.mipmap.ic_launcher))
                .setContentText("Downloading File")
                .setProgress(100, 0, false)
                .setColor(ctx.resources.getColor(R.color.colorPrimaryDark))
                .setOngoing(true) // THIS is the important line
                .setAutoCancel(false)
    }

    private lateinit var database: AppDatabase
    private lateinit var contentDao: ContentDao


    override fun doWork(): Result {

       return try {
            val title = inputData.getString("title")
            notificationBuilder.setContentTitle(title)
            database = AppDatabase.getInstance(CBOnlineApp.mInstance)
            contentDao = database.contentDao()
            initDownload(inputData)

           Result.success()
       }catch (throwable: Throwable) {
            Result.failure()
        }
    }

    private fun initDownload(intent: Data) {
        notificationManager.notify(0, notificationBuilder.build())
        var downloadCount = 0
        val downloadUrl = intent.getString("url")
        val url = downloadUrl!!.substring(38, (downloadUrl.length - 11))
        val attemptId = intent.getString("attemptId")


        Clients.api.getVideoDownloadKey(downloadUrl).enqueue(retrofitCallback { throwable, downloadKey ->
            downloadKey?.body().let {
                val keyId = it?.get("keyId")?.asString ?: ""
                val signature = it?.get("signature")?.asString ?: ""
                val policy = it?.get("policyString")?.asString ?: ""
                Clients.initiateDownload(url, "index.m3u8", keyId, signature, policy).enqueue(retrofitCallback { _, response ->
                    response?.body()?.let { indexResponse ->
                        writeResponseBodyToDisk(indexResponse, url, "index.m3u8")
                    }
                })

                Clients.initiateDownload(url, "video.key", keyId, signature, policy).enqueue(retrofitCallback { throwable, response ->
                    response?.body()?.let { videoResponse ->
                        writeResponseBodyToDisk(videoResponse, url, "video.key")
                    }
                })

                Clients.initiateDownload(url, "video.m3u8", keyId, signature, policy).enqueue(retrofitCallback { throwable, response ->
                    response?.body()?.let { keyResponse ->
                        writeResponseBodyToDisk(keyResponse, url, "video.m3u8")
                        val videoChunks = MediaUtils.getCourseDownloadUrls(url, applicationContext)
                        videoChunks.forEach { videoName: String ->
                            Clients.initiateDownload(url, videoName, keyId, signature, policy).enqueue(retrofitCallback { throwable, response ->
                                try {
                                    val isDownloaded = writeResponseBodyToDisk(response?.body()!!, url, videoName)
                                    if (isDownloaded) {
                                        if (videoName == "video00000.ts") {
                                            thread {
                                                contentDao.updateContent(intent.getString("id")!!, intent.getString("lectureContentId")!!, "inprogress")
                                            }
                                        }
                                        downloadCount++
                                        val downloadProgress = ((downloadCount.toDouble() / videoChunks.size) * 100).toInt()
                                        sendNotification(downloadProgress)
                                    }
                                    if (downloadCount == videoChunks.size) {
                                        onDownloadComplete(url, attemptId!!, intent.getString("contentId")!!)
                                        thread {
                                            contentDao.updateContent(intent.getString("id")!!, intent.getString("lectureContentId")!!, "true")
                                        }
                                    }
                                } catch (e: Exception) {
                                    contentDao.updateContent(intent.getString("id")!!, intent.getString("lectureContentId")!!, "false")
                                    notificationManager.cancel(0)
                                    notificationBuilder.setOngoing(false)
                                    notificationBuilder.setContentText("Download Failed")

                                    applicationContext.toast("There was some issue with your network.Please Try Again !!")
                                }
                            })
                        }
                    }
                })
            }
        })
    }

    private fun writeResponseBodyToDisk(body: ResponseBody, videoUrl: String?, fileName: String): Boolean {
        try {

            val file = applicationContext.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
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

    //function to update progress according to download progress
    private fun sendNotification(download: Int) {
        notificationBuilder.setProgress(100, download, false)
        notificationBuilder.setContentText("Downloaded $download %")
        notificationManager.notify(0, notificationBuilder.build())
    }


    private fun onDownloadComplete(url: String, attemptId: String, contentId: String) {
        val intent = Intent(applicationContext, VideoPlayerActivity::class.java)
        intent.putExtra("FOLDER_NAME", url)
        intent.putExtra("attemptId", attemptId)
        intent.putExtra("contentId", contentId)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)
        notificationManager.cancel(0)
        notificationBuilder.setProgress(0, 0, false)
        notificationBuilder.setContentText("File Downloaded")
        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.setOngoing(false)
        notificationBuilder.setAutoCancel(true)
        notificationManager.notify(0, notificationBuilder.build())
    }
}