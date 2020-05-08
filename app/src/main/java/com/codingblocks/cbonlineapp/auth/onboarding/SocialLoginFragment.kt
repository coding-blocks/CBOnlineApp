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
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.extensions.observer
import kotlinx.android.synthetic.main.fragment_social_login.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SocialLoginFragment : Fragment() {

    val vm by sharedViewModel<AuthViewModel>()

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
        web.settings.setSupportMultipleWindows(true)
        web.settings.userAgentString = "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36"
        web.loadUrl("${BuildConfig.OAUTH_URL}?redirect_uri=${BuildConfig.REDIRECT_URI}&response_type=code&client_id=${BuildConfig.CLIENT_ID}")
        web.webViewClient = object : WebViewClient() {

            var authComplete = false

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                if (url.contains("code=") && !authComplete) {
                    val grantCode = Uri.parse(url).getQueryParameter("code")
                    if (grantCode != null) {
                        vm.fetchToken(grantCode).observer(viewLifecycleOwner) {
                            if (it) {
                                startActivity(DashboardActivity.createDashboardActivityIntent(requireContext(), true))
                            }
                        }
                    }
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
