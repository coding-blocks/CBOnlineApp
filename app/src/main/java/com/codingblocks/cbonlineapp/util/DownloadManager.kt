package com.codingblocks.cbonlineapp.util

import android.content.Context
import android.os.Environment
import android.util.Log
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.offline.*
import com.vdocipher.aegis.offline.VdoDownloadManager.QueryResultListener
import java.io.File


class DownloadManager : VdoDownloadManager.EventListener {

    private var downloadStatusList = mutableListOf<DownloadStatus>()

    @Volatile
    private lateinit var vdoDownloadManager: VdoDownloadManager

    fun start(context: Context, mOtp: String, mPlaybackInfo: String, videoId: String) {
        val optionsDownloader = OptionsDownloader()
        optionsDownloader.downloadOptionsWithOtp(
            mOtp,
            mPlaybackInfo,
            object : OptionsDownloader.Callback {
                override fun onOptionsReceived(options: DownloadOptions) {
                    // we have received the available download options
                    val selectionIndices = intArrayOf(0, 1)
                    val downloadSelections = DownloadSelections(options, selectionIndices)
                    val file = File(Environment.getDataDirectory().absolutePath)
                    val folderFile = File(file, "/$videoId")
                    if (!folderFile.exists()) {
                        folderFile.mkdir()
                    }
                    val request =
                        DownloadRequest.Builder(downloadSelections, folderFile.absolutePath).build()
                    refreshList()
                    try {
                        vdoDownloadManager = VdoDownloadManager.getInstance(context)
                        vdoDownloadManager.enqueue(request)
                        vdoDownloadManager.addEventListener(this@DownloadManager)
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

    private fun refreshList() {

        vdoDownloadManager.query(VdoDownloadManager.Query(), QueryResultListener { statusList ->
            downloadStatusList.clear()
            downloadStatusList.addAll(statusList)
            if (statusList.isEmpty()) {
                Log.e("Query results", "No query results found")
                return@QueryResultListener
            }
            Log.e("Status List", statusList.size.toString() + " results found")
            val builder = StringBuilder()
            builder.append("query results:").append("\n")
            for (status in statusList) {
                builder.append((status)).append(" : ")
                    .append(status.mediaInfo.mediaId).append(", ")
                    .append(status.mediaInfo.title).append("\n")
            }
        })
    }

    override fun onChanged(mediaId: String?, downloadStatus: DownloadStatus) {
        Log.e("Download changed : ", mediaId ?: "null");
        updateListItem(downloadStatus);
    }

    override fun onDeleted(mediaId: String?) {
        removeListItem(mediaId ?: "null");
    }

    override fun onFailed(mediaId: String?, downloadStatus: DownloadStatus) {
        Log.e("Download failed : ", mediaId ?: "null");
        updateListItem(downloadStatus);
    }

    override fun onQueued(mediaId: String?, downloadStatus: DownloadStatus) {
        Log.e("Download queued : ", mediaId ?: "null");
        addListItem(downloadStatus);
    }

    override fun onCompleted(mediaId: String?, downloadStatus: DownloadStatus) {
        Log.e("Download completed : ", mediaId ?: "null");
        updateListItem(downloadStatus);
    }


    private fun updateListItem(status: DownloadStatus) { // if media already in downloadStatusList, update it
        val mediaId = status.mediaInfo.mediaId
        var position = -1
        for (i in 0 until downloadStatusList.size) {
            if (downloadStatusList[i].mediaInfo.mediaId.equals(mediaId)) {
                position = i
                break
            }
        }
        if (position >= 0) {
            downloadStatusList[position] = status
        } else {
            Log.e("DownloadManager", "item not found in adapter")
        }
    }

    private fun addListItem(downloadStatus: DownloadStatus) {
        downloadStatusList.add(0, downloadStatus)
    }

    private fun removeListItem(status: DownloadStatus) { // remove by comparing mediaId; status may change
        val mediaId = status.mediaInfo.mediaId
        removeListItem(mediaId)
    }

    private fun removeListItem(mediaId: String) {
        var position = -1
        for (i in 0 until downloadStatusList.size) {
            if (downloadStatusList[i].mediaInfo.mediaId == mediaId) {
                position = i
                break
            }
        }
        if (position >= 0) {
            downloadStatusList.removeAt(position)
        }
    }
}
