package com.codingblocks.cbonlineapp.dashboard

import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.CONVERSATION_ID
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.activity_chat.*
import org.koin.android.ext.android.inject

class ChatActivity : BaseCBActivity() {

    private val conversationId: String by lazy {
        intent.getStringExtra(CONVERSATION_ID) ?: ""
    }
    private val prefs by inject<PreferenceHelper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        setSupportActionBar(chatToolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        WebView.setWebContentsDebuggingEnabled(true)

        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            allowFileAccess = true
            allowFileAccessFromFileURLs = true
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
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
