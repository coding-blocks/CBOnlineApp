package com.codingblocks.cbonlineapp.activities

import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.prefs
import org.jetbrains.anko.*


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (prefs.SP_ACCESS_TOKEN_KEY == "access_token") {
            startActivity(intentFor<LoginActivity>().singleTop())
            finish()
        } else {
            startActivity(intentFor<HomeActivity>().singleTop())
            finish()
        }
    }
}
