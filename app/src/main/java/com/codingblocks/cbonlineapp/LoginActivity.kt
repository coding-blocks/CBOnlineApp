package com.codingblocks.cbonlineapp

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.home.HomeActivity
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.extensions.getPrefs
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity(), AnkoLogger {

    val courseDao: CourseDao by inject()
    val courseWithInstructorDao: CourseWithInstructorDao by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(android.R.style.Theme_Material_Light_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        GlobalScope.launch {
            clearData()
        }
        loginBtn.setOnClickListener {
            Components.openChrome(
                this,
                "${BuildConfig.OAUTH_URL}?redirect_uri=${BuildConfig.REDIRECT_URI}&response_type=code&client_id=${BuildConfig.CLIENT_ID}"
            )
        }

        skipBtn.setOnClickListener {
            redirectToHome()
        }
    }

    private fun getAccessToken() = getPrefs().SP_ACCESS_TOKEN_KEY

    private fun redirectToHome() {
        startActivity(intentFor<HomeActivity>().singleTop())
        finish()
    }

    private suspend fun clearData() {
        try {
            val mCurrentVersion = BuildConfig.VERSION_CODE
            val mSharedPreferences =
                getSharedPreferences("com.codingblocks.cbonlineapp.prefs", Context.MODE_PRIVATE)
            val mEditor = mSharedPreferences.edit()
            mEditor.apply()
            val last_version = mSharedPreferences.getInt("last_version", -1)
            if (last_version != mCurrentVersion) {
                courseDao.nukeTable()
                courseWithInstructorDao.nukeTable()
                CBOnlineApp.mInstance.clearApplicationData()
            } else {
                if (getAccessToken() != "access_token") {
                    redirectToHome()
                }
            }
            mEditor.putInt("last_version", mCurrentVersion)
            mEditor.commit()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
}
