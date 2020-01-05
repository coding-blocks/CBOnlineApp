package com.codingblocks.cbonlineapp.util

import android.content.Context
import android.os.Environment
import com.codingblocks.cbonlineapp.util.extensions.folderSize
import com.codingblocks.cbonlineapp.util.extensions.getPrefs
import java.io.File

const val FILE_THRESHOLD = 256000
const val GB_TO_KB = 1024 * 1024

object FileUtils {

    private fun getCommonPath(context: Context) =
        context.getExternalFilesDir(Environment.getDataDirectory().absolutePath)

    fun deleteDatabaseFile(context: Context, databaseName: String) {
        val databases = File(context.applicationInfo.dataDir + "/databases")
        val db = File(databases, databaseName)
        if (db.delete())
            println("Database deleted")
        else
            println("Failed to delete database")
        val journal = File(databases, "$databaseName-journal")
        if (journal.exists()) {
            if (journal.delete())
                println("Database journal deleted")
            else
                println("Failed to delete database journal")
        }
    }

    fun checkIfCannotDownload(context: Context): Boolean {
        val available = context.getPrefs().SP_DATA_LIMIT.times(GB_TO_KB).toInt()
        val sizeAfterDownload = getCommonPath(context)?.let { folderSize(it).div(1024).plus(FILE_THRESHOLD) }
        return sizeAfterDownload!! > available
    }

    private fun clearOldestDirectory(context: Context) {
        val files = getCommonPath(context)?.listFiles()
        val mutableFiles = mutableListOf<File>()
        if (files != null && files.isNotEmpty()) {
            for (file in files)
                mutableFiles.add(file)

            mutableFiles.sortWith(Comparator { o1, o2 ->
                o1.lastModified().compareTo(o2.lastModified())
            })
            mutableFiles[0].delete()
        }
    }

    fun showIfCleanDialog(context: Context, onCleanDialogListener: OnCleanDialogListener) {
        Components.showConfirmation(context, "file") {
            clearOldestDirectory(context)
            onCleanDialogListener.onComplete()
        }
    }

    fun checkDownloadFileExists(context: Context, lectureId: String): Boolean {
        return File(getCommonPath(context), "/$lectureId").exists()
    }
}

interface OnCleanDialogListener {
    fun onComplete()
}
