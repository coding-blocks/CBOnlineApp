package com.codingblocks.cbonlineapp.activities

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.extensions.getPrefs
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import java.io.File

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clearData()
        GlobalScope.launch {
            finishSplashScreen()
        }
    }

    suspend fun getAccessToken() = getPrefs().SP_ACCESS_TOKEN_KEY
    suspend fun finishSplashScreen() {
        // After the splash screen duration, route to the right activities
        if (getAccessToken() == "access_token") {
            startActivity(intentFor<LoginActivity>().singleTop())
            finish()
        } else {
            startActivity(intentFor<HomeActivity>().singleTop())
            finish()
        }
    }

    private fun deleteDatabaseFile(context: Context, databaseName: String) {
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

    private fun clearData() {
        try {
            val mCurrentVersion = BuildConfig.VERSION_CODE
            val mSharedPreferences =
                getSharedPreferences("com.codingblocks.cbonlineapp.prefs", Context.MODE_PRIVATE)
            val mEditor = mSharedPreferences.edit()
            mEditor.apply()
            val last_version = mSharedPreferences.getInt("last_version", -1)
            if (last_version != mCurrentVersion) {
                deleteDatabaseFile(this, "app-database")
            }
            mEditor.putInt("last_version", mCurrentVersion)
            mEditor.commit()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
}
