package com.codingblocks.cbonlineapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.caverock.androidsvg.SVG
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.regex.Pattern


object MediaUtils {

    const val DOWNLOAD_CHANNEL_ID = "downloadChannel"
    val okHttpClient = OkHttpClient.Builder().build()
    //Call this when you want to play a video and pass the return type to exoplayer
    fun getCourseVideoUri(videoUrl: String, context: Context): Uri {
        val file = context.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
        val dataFile = File(file, "/$videoUrl/index.m3u8")
        return Uri.parse(dataFile.toURI().toString())
    }

    fun getCourseDownloadUrls(videoUrl: String, context: Context): ArrayList<String> {
        val videoNames = arrayListOf<String>()

        val file = context.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
        val dataFile = File(file, "/$videoUrl/video.m3u8")

        dataFile.forEachLine {
            Log.i("fileName", it)
            if (it.contains(".ts")) {
                Log.i("fileName", it)
                videoNames.add(it)
            }
        }        //Read the file above and add the ts names to videoNames

        return videoNames
    }

    fun getYotubeVideoId(videoUrl: String): String {
        var vId = ""
        val pattern = Pattern.compile(
            "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = pattern.matcher(videoUrl)
        if (matcher.matches()) {
            vId = matcher.group(1)
        }
        return vId
    }

    fun checkPermission(context: Context): Boolean {

        val readExternal = ContextCompat.checkSelfPermission(
            context.applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writeExternal = ContextCompat.checkSelfPermission(
            context.applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        return readExternal == PackageManager.PERMISSION_GRANTED && writeExternal == PackageManager.PERMISSION_GRANTED
    }

    fun isStoragePermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {

                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

}