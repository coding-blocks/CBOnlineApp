package com.codingblocks.cbonlineapp.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.fragment_inbox.*

/**
 * A simple [Fragment] subclass.
 */
class InboxFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inbox, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView.settings.apply {
            javaScriptEnabled = true
            webView.settings.javaScriptCanOpenWindowsAutomatically = true
            webView.settings.allowFileAccess = true
            webView.settings.allowFileAccessFromFileURLs = true
        }
        Clients.api.getSignature().enqueue(retrofitCallback { _, response ->
            val signature = response?.body()?.get("signature")
            webView.loadUrl("file:///android_asset/Chat.html")
        })
    }
}
