package com.codingblocks.cbonlineapp.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.PictureDrawable
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

object MediaUtils {

    fun deleteRecursive(fileOrDirectory: File) {

        if (fileOrDirectory.isDirectory) {
            for (child in fileOrDirectory.listFiles()) {
                deleteRecursive(child)
            }
        }

        fileOrDirectory.delete()
    }

    fun getYoutubeVideoId(videoUrl: String): String {
        var videoId: String
        val url = videoUrl.split("v=")
        videoId = if (url.size == 1) {
            videoUrl.split("embed/")[1]
        } else {
            url[1]
        }
        val ampersandPosition = videoId.indexOf('&')
        if (ampersandPosition != -1) {
            videoId = videoId.substring(0, ampersandPosition)
        }
        val questionPosition = videoId.indexOf('?')
        if (questionPosition != -1) {
            videoId = videoId.substring(0, questionPosition)
        }
        return videoId
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
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {

                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else { // permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    fun getBitmapFromPictureDrawable(picDrawable: PictureDrawable): Bitmap {
        val bitmap = Bitmap.createBitmap(picDrawable.intrinsicWidth, picDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        picDrawable.setBounds(0, 0, canvas.width, canvas.height)
        picDrawable.draw(canvas)

        return bitmap
    }

    fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val circlebitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(circlebitmap)
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle((bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(), (bitmap.width / 2).toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return circlebitmap
    }
}
