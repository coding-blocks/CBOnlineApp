package com.codingblocks.cbonlineapp.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.CONVERSATION_ID
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.fragment_inbox.*
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 */
class InboxFragment : BaseCBFragment() {
    private var conversationId: String = ""
    private val prefs by inject<PreferenceHelper>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inbox, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String) =
            InboxFragment().apply {
                arguments = Bundle().apply {
                    putString(CONVERSATION_ID, id)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            inboxRoot.removeAllViews()
            conversationId = it.getString(CONVERSATION_ID) ?: ""
        }
        val webView = WebView(requireContext())
        webView.settings.apply {
            javaScriptEnabled = true
            webView.settings.javaScriptCanOpenWindowsAutomatically = true
            webView.settings.allowFileAccess = true
            webView.settings.allowFileAccessFromFileURLs = true
        }
        Clients.api.getSignature().enqueue(retrofitCallback { _, response ->
            val signature = response?.body()?.get("signature")
            val userId = prefs.SP_USER_ID
            val userName = prefs.SP_USER_NAME
            val email = prefs.SP_EMAIL_ID
            val script: String
            if (conversationId.isEmpty()) {
                script = """
                    Talk.ready.then(function() {
                        var me = new Talk.User({
                            id: $userId,
                            name: "$userName",
                            email: "$email"
                        });
                        window.talkSession = new Talk.Session({
                            appId: "2LhQvB3j",
                            me: me,
                            signature: $signature
                        });
                        var inbox = talkSession.createInbox();
                        inbox.mount(document.getElementById("talkjs-container"));
                    });
                """.trimIndent()
            } else {
                script = """
                    Talk.ready.then(function() {
                        var me = new Talk.User({
                            id: $userId,
                            name: "$userName",
                            email: "$email"
                        });
                        window.talkSession = new Talk.Session({
                            appId: "2LhQvB3j",
                            me: me,
                            signature: $signature
                        });
                        var conversation = window.talkSession.getOrCreateConversation("$conversationId");
                        conversation.setParticipant(me);
                        var inbox = talkSession.createChatbox(conversation);
                        inbox.mount(document.getElementById("talkjs-container"));
                    });
                """.trimIndent()
            }

            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    return false
                }

                override fun onPageFinished(view: WebView, url: String?) {
                    super.onPageFinished(view, url)
                    view.evaluateJavascript("javascript:$script", null)
                }
            }

            webView.loadUrl("file:///android_asset/Chat.html")
        })
        if (inboxRoot != null)
            inboxRoot.addView(webView)
    }
}
