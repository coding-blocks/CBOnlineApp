package com.codingblocks.cbonlineapp.auth.onboarding

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.fragment_social_login.*

class SocialLoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_social_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CookieManager.getInstance().apply {
            removeAllCookies {
            }
        }
        val web: WebView = webview
        web.settings.javaScriptEnabled = true
        web.loadUrl("${BuildConfig.OAUTH_URL}?redirect_uri=${BuildConfig.REDIRECT_URI}&response_type=code&client_id=${BuildConfig.CLIENT_ID}")
        web.webViewClient = object : WebViewClient() {

            var authComplete = false

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                if (url.contains("code=") && !authComplete) {
                    val grantCode = Uri.parse(url).getQueryParameter("code")
                    authComplete = true
                } else if (url.contains("error=access_denied")) {
                    authComplete = true
                    web.visibility = View.GONE
                }
            }
        }
        web.visibility = View.VISIBLE
    }
}
