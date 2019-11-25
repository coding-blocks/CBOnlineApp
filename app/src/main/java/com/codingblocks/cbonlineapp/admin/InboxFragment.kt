package com.codingblocks.cbonlineapp.admin

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ClientCertRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.fragment_inbox.*

/**
 * A simple [Fragment] subclass.
 */
class InboxFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inbox, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView.settings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
//        runClients.api.getSignature()
        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
//                val script: String =
//                    "?id=${user.id}&name=${user.firstname}&email=&photo=&signature=&appId="

            }

        }
        webView.loadUrl("file:///android_asset/Chat.html")
//        webView.webViewClient = WebViewClient()


    }
}


