package com.codingblocks.cbonlineapp.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File

object MediaUtils {

    fun getCourseVideoUri(videoUrl: String, context: Context): Uri {
        //TODO : Request storage permissions on the app launch
        val file = context.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
        val dataFile = File(file, "/$videoUrl/index.m3u8")
        return Uri.parse(dataFile.toURI().toString())
    }

    fun getCourseDownloadUrls(videoUrl: String, context: Context): ArrayList<String> {
        //TODO
        return arrayListOf()
    }

}