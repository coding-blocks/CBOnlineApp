package com.codingblocks.cbonlineapp

import android.app.Dialog
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop


class LoginActivity : AppCompatActivity(), AnkoLogger {

    private val CLIENT_ID = "9047417470"
    private val REDIRECT_URI = "http://localhost"
    private val OAUTH_URL = "https://account.codingblocks.com/oauth/authorize"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_button.setOnClickListener {
            val authdialog = Dialog(this@LoginActivity)
            val web: WebView

            authdialog.setContentView(R.layout.auth_dialog)
            web = authdialog.findViewById(R.id.webv)
            web.settings.javaScriptEnabled = true
            web.loadUrl("$OAUTH_URL?redirect_uri=$REDIRECT_URI&response_type=token&client_id=$CLIENT_ID")
            web.webViewClient = object : WebViewClient() {

                var authComplete = false

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)

                    if (url.contains("access_token=") && !authComplete) {
                        val authToken = getAccessToken(url)
                        prefs.SP_ACCESS_TOKEN_KEY = authToken
                        authComplete = true
                        authdialog.dismiss()
                        startActivity(intentFor<MainActivity>().singleTop())
                    } else if (url.contains("error=access_denied")) {
                        authComplete = true
                        authdialog.dismiss()
                    }
                }
            }
            authdialog.show()
            authdialog.setTitle("")
            authdialog.setCancelable(true)
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
