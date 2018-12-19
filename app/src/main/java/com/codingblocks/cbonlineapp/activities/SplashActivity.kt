package com.codingblocks.cbonlineapp.activities

import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.getPrefs
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import org.jetbrains.anko.*
import org.jetbrains.anko.coroutines.experimental.asReference
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.custom.async


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}
