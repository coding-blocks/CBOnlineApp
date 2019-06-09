package com.codingblocks.cbonlineapp.util

import android.content.Context
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.folderSize
import com.codingblocks.cbonlineapp.extensions.getPrefs
import kotlinx.android.synthetic.main.custom_dialog.view.*
import org.jetbrains.anko.layoutInflater
import java.io.File
import kotlin.Comparator

const val FILE_THRESHOLD = 256000
const val GB_TO_KB = 1024 * 1024

object FileUtils {

    private fun getCommonPath(context: Context) = context.getExternalFilesDir(Environment.getDataDirectory().absolutePath)

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
        val sizeAfterDownload = folderSize(getCommonPath(context)).div(1024).plus(FILE_THRESHOLD)
        return sizeAfterDownload > available
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
        val confirmDialog = AlertDialog.Builder(context).create()
        val dialogView = context.layoutInflater.inflate(R.layout.custom_dialog, null)
        dialogView.title.text = context.getString(R.string.clean_dialog_title)
        dialogView.description.text = context.getString(R.string.clean_dialog_description)
        dialogView.okBtn.text = context.getString(R.string.clean_dialog_okBtn)
        dialogView.cancelBtn.text = context.getString(R.string.clean_dialog_cancelBtn)

        dialogView.okBtn.setOnClickListener {
            clearOldestDirectory(context)
            onCleanDialogListener.onComplete()
            confirmDialog.dismiss()
        }
        dialogView.cancelBtn.setOnClickListener {
            confirmDialog.dismiss()
        }
        confirmDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        confirmDialog.setView(dialogView)
        confirmDialog.setCancelable(false)
        confirmDialog.show()
    }
}

interface OnCleanDialogListener {
    fun onComplete()
}
