package com.codingblocks.cbonlineapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.extensions.getPrefs
import com.codingblocks.cbonlineapp.util.extensions.openChrome
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop

class LoginActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        runBlocking {
            if (getAccessToken() != "access_token") {
                redirectToHome()
            }
        }
        loginBtn.setOnClickListener {
            openChrome(
                "${BuildConfig.OAUTH_URL}?redirect_uri=${BuildConfig.REDIRECT_URI}&response_type=code&client_id=${BuildConfig.CLIENT_ID}"
            )
        }
        skipBtn.setOnClickListener {
            redirectToHome()
        }
    }

    private fun getAccessToken() = getPrefs().SP_ACCESS_TOKEN_KEY

    private fun redirectToHome() {
        startActivity(intentFor<DashboardActivity>().singleTop())
        finish()
    }
}
