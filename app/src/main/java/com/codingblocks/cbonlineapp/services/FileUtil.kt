package com.codingblocks.cbonlineapp.services

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.codingblocks.cbonlineapp.CBOnlineApp
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.services.DownloadUtil.DOWNLOAD_SUCCESS
import com.codingblocks.cbonlineapp.services.DownloadUtil.TAG_DOWNLOAD
import com.codingblocks.cbonlineapp.services.DownloadUtil.downloadManager
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.codingblocks.onlineapi.Clients
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object FileUtil {
    private val okHttpClient = OkHttpClient()
    fun downloadFileFromUrl(downloadFileUrl: String): Int {
        var ret = DOWNLOAD_SUCCESS
        var downloadCount = 0

        try {
            val url = downloadFileUrl.substring(38, (downloadFileUrl.length - 11))
            Clients.api.getVideoDownloadKey(downloadFileUrl).enqueue(retrofitCallback { throwable, response ->
                response?.body().let {
                    val keyId = it?.get("keyId")?.asString ?: ""
                    val signature = it?.get("signature")?.asString ?: ""
                    val policy = it?.get("policyString")?.asString ?: ""
                    Clients.initiateDownload(url, "index.m3u8", keyId, signature, policy).enqueue(retrofitCallback { throwable, response ->
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
                            val videoChunks = MediaUtils.getCourseDownloadUrls(url, CBOnlineApp.mInstance)
                            videoChunks.forEach { videoName: String ->
                                Clients.initiateDownload(url, videoName, keyId, signature, policy).enqueue(retrofitCallback { throwable, response ->
                                    val isDownloaded = writeResponseBodyToDisk(response?.body()!!, url, videoName)
                                    if (isDownloaded) {
                                        if (videoName == "video00000.ts") {
//                                            thread {
//                                                contentDao.updateContent(intent.getStringExtra("id"), intent.getStringExtra("lectureContentId"), "inprogress")
//                                            }
                                        }
                                        downloadCount++
                                        val downloadProgress = ((downloadCount / videoChunks.size) * 100)
                                        downloadManager!!.updateTaskProgress(downloadProgress)
                                    }
                                    if (downloadCount == videoChunks.size) {
//                                        thread {
//                                            contentDao.updateContent(intent.getStringExtra("id"), intent.getStringExtra("lectureContentId"), "true")
//                                        }
                                    }
                                })
                            }
                        }
                    })
                }
            })
//            //assign file size
//            val downloadFileLength = getRequestFileSize(downloadFileUrl)
//            //check if the file size is the same as the local file
//            val existLocalFileLength = existLocalFile.length()
//
//            //// check file size
//            if (downloadFileLength == 0L) {
//                ret = DOWNLOAD_FAILED
//            } else if (downloadFileLength == existLocalFileLength) {
//                ret = DOWNLOAD_SUCCESS
//            } else {
//
//                var builder = Request.Builder()
//                builder = builder.url(downloadFileUrl)
//                builder = builder.addHeader("RANGE", "bytes=$existLocalFileLength")
//                val request = builder.build()
//
//                val call = okHttpClient.newCall(request)
//                val response = call.execute()
//
//                if (response != null && response.isSuccessful) {
//
//                    val downloadFile = RandomAccessFile(existLocalFile, "rw")
//                    downloadFile.seek(existLocalFileLength)
//
//                    val responseBody = response.body()
//                    val inputStream = responseBody!!.byteStream()
//                    val bufferedInputStream = BufferedInputStream(inputStream)
//
//                    val data = ByteArray(102400)
//
//                    var totalReadLength: Long = 0
//
//                    var readLength = bufferedInputStream.read(data)
//
//                    while (readLength != -1) {
//
//                        downloadFile.write(data, 0, readLength)
//
//                        totalReadLength += readLength
//
//                        val downloadProgress = ((totalReadLength + existLocalFileLength) * 100 / downloadFileLength).toInt()
//
//                        downloadManager!!.updateTaskProgress(downloadProgress)
//
//                        readLength = bufferedInputStream.read(data)
//                    }
//                }
//            }
        } catch (ex: Exception) {
            Log.e(TAG_DOWNLOAD, ex.message, ex)
        } finally {
            return ret
        }
    }

    fun getRequestFileSize(downloadUrl: String?): Long {
        var ret: Long = 0

        try {
            if (downloadUrl != null && !TextUtils.isEmpty(downloadUrl)) {
                var builder = Request.Builder()
                builder = builder.url(downloadUrl)
                val request = builder.build()
                val call = okHttpClient.newCall(request)
                val response = call.execute()

                if (response != null) {
                    if (response.isSuccessful) {
                        val contentLength = response.header("Content-Length")
                        ret = java.lang.Long.parseLong(contentLength)
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG_DOWNLOAD, ex.message, ex)
        } finally {
            return ret
        }
    }

    //creates and returns the local file
    fun createDownloadLocalFile(downloadFileUrl: String?): File? {
        var file: File? = null

        try {
            if (downloadFileUrl != null && !TextUtils.isEmpty(downloadFileUrl)) {
                val lastIndex = downloadFileUrl.lastIndexOf("/")
                if (lastIndex > -1) {
                    val downloadFileName = downloadFileUrl.substring(lastIndex + 1)
                    /** TODO Pointing to the Download Directory **/
                    val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val downloadDirectoryPath = downloadDirectory.path

                    file = File("$downloadDirectoryPath/$downloadFileName")

                    if (!file.exists()) {
                        file.createNewFile()
                    }
                }
            }
        } catch (ex: IOException) {
            Log.e(DownloadUtil.TAG_DOWNLOAD, ex.message, ex)
        } finally {
            return file
        }
    }

    private fun writeResponseBodyToDisk(body: ResponseBody, videoUrl: String?, fileName: String): Boolean {
        try {
            val file = CBOnlineApp.mInstance.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
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
}
//class DownloadService : IntentService("Download Service"), AnkoLogger {
//
//    private val notificationManager by lazy {
//        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    }
//
//    private val notificationBuilder by lazy {
//        NotificationCompat.Builder(this, MediaUtils.DOWNLOAD_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_file_download)
//                .setContentTitle("Download")
//                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
//                .setContentText("Downloading File")
//                .setProgress(0, 0, true)
//                .setColor(resources.getColor(R.color.colorPrimaryDark))
//                .setOngoing(true) // THIS is the important line
//                .setAutoCancel(false)
//    }
//
//    private var totalFileSize: Int = 0
//    private lateinit var database: AppDatabase
//    private lateinit var contentDao: ContentDao
//
//
//    override fun onHandleIntent(intent: Intent) {
//        val title = intent.getStringExtra("title")
//        notificationBuilder.setContentTitle(title)
//        database = AppDatabase.getInstance(this)
//        contentDao = database.contentDao()
//
//        initDownload(intent)
//    }
//
//    private fun initDownload(intent: Intent) {
//        notificationManager.notify(0, notificationBuilder.build())
//        var downloadCount = 0
//        val downloadUrl = intent.getStringExtra("url")
//        val url = downloadUrl.substring(38, (downloadUrl.length - 11))
//
//        Clients.api.getVideoDownloadKey(downloadUrl).enqueue(retrofitCallback { throwable, downloadKey ->
//            downloadKey?.body().let {
//                val keyId = it?.get("keyId")?.asString ?: ""
//                val signature = it?.get("signature")?.asString ?: ""
//                val policy = it?.get("policyString")?.asString ?: ""
//                Clients.initiateDownload(url, "index.m3u8", keyId, signature, policy).enqueue(retrofitCallback { _, response ->
//                    response?.body()?.let { indexResponse ->
//                        writeResponseBodyToDisk(indexResponse, url, "index.m3u8")
//                    }
//                })
//
//                Clients.initiateDownload(url, "video.key", keyId, signature, policy).enqueue(retrofitCallback { throwable, response ->
//                    response?.body()?.let { videoResponse ->
//                        writeResponseBodyToDisk(videoResponse, url, "video.key")
//                    }
//                })
//
//                Clients.initiateDownload(url, "video.m3u8", keyId, signature, policy).enqueue(retrofitCallback { throwable, response ->
//                    response?.body()?.let { keyResponse ->
//                        writeResponseBodyToDisk(keyResponse, url, "video.m3u8")
//                        val videoChunks = MediaUtils.getCourseDownloadUrls(url, this)
//                        videoChunks.forEach { videoName: String ->
//                            Clients.initiateDownload(url, videoName, keyId, signature, policy).enqueue(retrofitCallback { throwable, response ->
//                                val isDownloaded = writeResponseBodyToDisk(response?.body()!!, url, videoName)
//                                if (isDownloaded) {
//                                    if (videoName == "video00000.ts") {
//                                        thread {
//                                            contentDao.updateContent(intent.getStringExtra("id"), intent.getStringExtra("lectureContentId"), "inprogress")
//                                        }
//                                    }
//                                    downloadCount++
//                                }
//                                if (downloadCount == videoChunks.size) {
//                                    onDownloadComplete(url)
//                                    thread {
//                                        contentDao.updateContent(intent.getStringExtra("id"), intent.getStringExtra("lectureContentId"), "true")
//                                    }
//                                }
//                            })
//                        }
//                    }
//                })
//            }
//        })
//    }
//
//
//    //function to update progress according to download progress
//    private fun sendNotification(download: Int) {
//        notificationBuilder.setProgress(100, download, false)
//        notificationBuilder.setContentText(String.format("Downloaded (%d/%d) MB", download, download))
//        notificationManager.notify(0, notificationBuilder.build())
//    }
//
//
//    private fun onDownloadComplete(url: String) {
//        val intent = Intent(this, VideoPlayerActivity::class.java)
//        intent.putExtra("FOLDER_NAME", url)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT)
//        notificationManager.cancel(0)
//        notificationBuilder.setProgress(0, 0, false)
//        notificationBuilder.setContentText("File Downloaded")
//        notificationBuilder.setContentIntent(pendingIntent)
//        notificationBuilder.setOngoing(false)
//        notificationBuilder.setAutoCancel(true)
//        notificationManager.notify(0, notificationBuilder.build())
//    }
//
//    override fun onTaskRemoved(rootIntent: Intent) {
//        notificationManager.cancel(0)
//    }
//
//}
