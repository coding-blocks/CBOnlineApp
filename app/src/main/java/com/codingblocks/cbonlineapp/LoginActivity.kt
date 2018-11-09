package com.codingblocks.cbonlineapp

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop


class LoginActivity : AppCompatActivity(), AnkoLogger {

    private val CLIENT_ID = "442127787"
    private val REDIRECT_URI = "https://app.codingblocks.com"
    private val OAUTH_URL = "https://account.codingblocks.com/oauth/authorize"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_button.setOnClickListener {
            //            val authdialog = Dialog(this@LoginActivity)
            val web: WebView = webview

//            authdialog.setContentView(R.layout.auth_dialog)
            web.settings.javaScriptEnabled = true
            web.loadUrl("$OAUTH_URL?redirect_uri=$REDIRECT_URI&response_type=token&client_id=$CLIENT_ID")
            web.webViewClient = object : WebViewClient() {

                var authComplete = false

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)

                    if (url.contains("access_token=") && !authComplete) {
                        val authToken = getAccessToken(url)
                        authComplete = true
                        info { "grant code $authToken" }
                        prefs.SP_ACCESS_TOKEN_KEY = authToken
                        prefs.SP_JWT_TOKEN_KEY = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Ijk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJyb2xlSWQiOjIsImNyZWF0ZWRBdCI6IjIwMTgtMDktMjdUMTM6MTA6NTkuMzk2WiIsInVwZGF0ZWRBdCI6IjIwMTgtMTEtMDlUMTA6MTE6MjQuNDc3WiIsImNsaWVudElkIjoiNWQ5NDg3MzItNjJlYi00ZDFmLWFmYjAtZWZmM2U0MTI0NDI5IiwiaXNUb2tlbkZvckFkbWluIjpmYWxzZSwiaWF0IjoxNTQxNzY5MTYxLCJleHAiOjE1NDE3NzA2NjF9.mu_uGar05A7LIXwEQzNiN-pGkpr9QFgGYMdIxvuu3KtRgZArXs4Gqwej8mVTfzpyGp1BbyXBUma3N2s0qjRQl9rR-IG9RKfULi6jUzrOHOcadfdkUzb6EJ5zJzj0H0J__54_xFplLFZwKNvAG-YnK4jMX16i-6OzGLeqQrpEXPcB-LVV5ae18srNztZgihrRDLo50Np_KThh6Ne73IF-zAoUEYAqniYyOJGwilWMd_TD3fMw_j7mm3eAouKqkAgAWLNo79wD1icAek4cXZcnorQ1anwcp2Mnyd5HubvyJf5qrje2bieBiK8Z743HB1otYX-132Imjrh6sfsTBTKKKg"

                        Clients.apiToken.getToken(authToken).enqueue(retrofitcallback { _, response ->
                            info { "token" + response!!.message() }

                            if (response!!.isSuccessful) {


                            }

                        })
                    } else if (url.contains("error=access_denied")) {
                        authComplete = true
                        web.visibility = View.GONE
                    }
                }
            }
            web.visibility = View.VISIBLE
        }
        skipBtn.setOnClickListener {
            startActivity(intentFor<HomeActivity>().singleTop())
            finish()
        }
    }


    private fun getAccessToken(url: String): String {

        val accessTokenIndex = url.indexOf("access_token")
        val loopStartIndex = accessTokenIndex + "access_token".length + 1
        val andIndex = url.indexOf("&")
        val sb = StringBuilder("")
        for (i in loopStartIndex until andIndex) {
            sb.append(url[i])
        }
        return sb.toString()
    }
}
