package com.codingblocks.cbonlineapp.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.utils.MediaUtils
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.notificationManager
import org.jetbrains.anko.singleTop


class LoginActivity : AppCompatActivity(), AnkoLogger {

    private val CLIENT_ID = "5633768694"
    private val REDIRECT_URI = "app://online.codingblocks.com"
    private val OAUTH_URL = "https://account.codingblocks.com/oauth/authorize"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.statusBarColor = Color.BLACK

        createDownloadChannel(MediaUtils.DOWNLOAD_CHANNEL_ID)

        login_button.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
                    .enableUrlBarHiding()
                    .setToolbarColor(resources.getColor(R.color.colorPrimaryDark))
                    .setShowTitle(true)
                    .setSecondaryToolbarColor(resources.getColor(R.color.colorPrimary))
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse("$OAUTH_URL?redirect_uri=$REDIRECT_URI&response_type=code&client_id=$CLIENT_ID"))

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
