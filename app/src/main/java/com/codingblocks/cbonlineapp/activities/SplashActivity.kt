package com.codingblocks.cbonlineapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.utils.getPrefs
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop

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
