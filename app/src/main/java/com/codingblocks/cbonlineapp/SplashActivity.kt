package com.codingblocks.cbonlineapp

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop


class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        scheduleSplashScreen();
    }

    private fun scheduleSplashScreen() {
        val splashScreenDuration = getSplashScreenDuration()
        Handler().postDelayed(
                {
                    // After the splash screen duration, route to the right activities
                    if (prefs.SP_ACCESS_TOKEN_KEY.equals("access_token")) {
                        val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, splashImageView, "trans1")
                        startActivity(intentFor<LoginActivity>().singleTop(), compat.toBundle())
                    } else {
                        startActivity(intentFor<HomeActivity>().singleTop())
                        finish()
                    }
                },
                splashScreenDuration
        )
    }


    private fun getSplashScreenDuration() = 3000L
}
