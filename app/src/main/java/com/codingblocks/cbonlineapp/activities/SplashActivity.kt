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
    val ui = SplashActivityUI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui.setContentView(this)
        scheduleSplashScreen()
    }

    private fun scheduleSplashScreen() {
        val splashScreenDuration = getSplashScreenDuration()
        Handler().postDelayed(
                {
                    // After the splash screen duration, route to the right activities
                    if (prefs.SP_ACCESS_TOKEN_KEY.equals("access_token")) {
                        val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, ui.logo, "trans1")
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

    class SplashActivityUI : AnkoComponent<SplashActivity> {
        lateinit var logo: ImageView
        override fun createView(ui: AnkoContext<SplashActivity>): View = with(ui) {
            frameLayout {
                logo = imageView(R.drawable.cblogo)
                        .lparams {
                            width = wrapContent
                            height = wrapContent
                            gravity = Gravity.CENTER
                        }
            }
        }
    }
}
