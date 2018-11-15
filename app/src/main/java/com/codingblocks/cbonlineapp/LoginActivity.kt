package com.codingblocks.cbonlineapp

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop


class LoginActivity : AppCompatActivity(), AnkoLogger {

    private val CLIENT_ID = "5633768694"
    private val REDIRECT_URI = "https://android.codingblocks.com"
    private val OAUTH_URL = "https://account.codingblocks.com/oauth/authorize"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_button.setOnClickListener {
            //            val authdialog = Dialog(this@LoginActivity)
            val web: WebView = webview

//            authdialog.setContentView(R.layout.auth_dialog)
            web.settings.javaScriptEnabled = true
            web.loadUrl("$OAUTH_URL?redirect_uri=$REDIRECT_URI&response_type=code&client_id=$CLIENT_ID")
            web.webViewClient = object : WebViewClient() {

                var authComplete = false

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)

                    if (url.contains("code=") && !authComplete) {
                        val grantCode = Uri.parse(url).getQueryParameter("code")
                        authComplete = true
                        info { "grant code $grantCode" }

                        Clients.apiToken.getToken(grantCode).enqueue(retrofitcallback { _, response ->
                            info { "token" + response!!.message() }

                            if (response!!.isSuccessful) {
                                val jwt = response.body()?.asJsonObject?.get("jwt")?.asString!!
                                val rt = response.body()?.asJsonObject?.get("refresh_token")?.asString!!
                                prefs.SP_ACCESS_TOKEN_KEY = grantCode
                                prefs.SP_JWT_TOKEN_KEY = jwt
                                prefs.SP_JWT_REFRESH_TOKEN = rt
                                Toast.makeText(this@LoginActivity, "Logged In", Toast.LENGTH_SHORT).show()
                                startActivity(intentFor<HomeActivity>().singleTop())
                                finish()
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

}
