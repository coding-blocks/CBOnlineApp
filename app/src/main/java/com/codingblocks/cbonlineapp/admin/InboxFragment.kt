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
import com.codingblocks.cbonlineapp.util.CONVERSATION_ID
import com.codingblocks.cbonlineapp.util.extensions.getPrefs
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.fragment_inbox.*

/**
 * A simple [Fragment] subclass.
 */
class InboxFragment : Fragment() {

    var conversationId: String = "2abf512165e466fa0bfb71d801beff9bbe8e61dd1ddf9f8db869be0f16e73e14"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            conversationId = it.getString(CONVERSATION_ID) ?: ""
        }
        return inflater.inflate(R.layout.fragment_inbox, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(conversationId: String) =
            InboxFragment().apply {
                arguments = Bundle().apply {
                    putString(CONVERSATION_ID, conversationId)
                }
            }
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
            val userId = getPrefs()?.SP_USER_ID
            val userName = getPrefs()?.SP_USER_NAME
            val email = getPrefs()?.SP_EMAIL_ID
            val script: String
//            if (conversationId.isEmpty()) {
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
//            } else {
//                script = """
//                Talk.ready.then(function() {
//                        var me = new Talk.User({
//                            id: $userId,
//                            name: "$userName",
//                            email: "$email"
//                        });
//                        window.talkSession = new Talk.Session({
//                            appId: "2LhQvB3j",
//                            me: me,
//                            signature: $signature
//                        });
//                        var conversation = window.talkSession.getOrCreateConversation($conversationId);
//
//                        var chatBox = talkSession.createChatbox(conversation);
//                        chatBox.mount(document.getElementById("talkjs-container"));
//                    });
//                """.trimIndent()
//            }



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
    }
}
