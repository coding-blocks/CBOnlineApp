package com.codingblocks.cbonlineapp.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.MediaUtils
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.notificationManager
import org.jetbrains.anko.singleTop

class LoginActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.statusBarColor = Color.BLACK

        createDownloadChannel(MediaUtils.DOWNLOAD_CHANNEL_ID)

        loginBtn.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
                .enableUrlBarHiding()
                .setToolbarColor(resources.getColor(R.color.colorPrimaryDark))
                .setShowTitle(true)
                .setSecondaryToolbarColor(resources.getColor(R.color.colorPrimary))
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse("${BuildConfig.OAUTH_URL}?redirect_uri=${BuildConfig.REDIRECT_URI}&response_type=code&client_id=${BuildConfig.CLIENT_ID}"))
        }
        skipBtn.setOnClickListener {
            startActivity(intentFor<HomeActivity>().singleTop())
            finish()
        }
    }

    private fun createDownloadChannel(channelId: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId,
                "Course Download",
                NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
