package com.codingblocks.cbonlineapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.home.HomeActivity
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.extensions.getPrefs
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
}
