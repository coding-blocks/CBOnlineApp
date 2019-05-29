package com.codingblocks.cbonlineapp.util

import android.content.Context
import java.io.File

object FileUtils {
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
}
