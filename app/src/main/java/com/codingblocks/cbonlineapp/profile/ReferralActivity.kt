package com.codingblocks.cbonlineapp.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.safeApiCall
import kotlinx.android.synthetic.main.activity_referral.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.share
import org.jetbrains.anko.toast

class ReferralActivity : BaseCBActivity() {

    private val myClipboard: ClipboardManager by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_referral)
        setToolbar(referralToolbar)

        copy_clipboard.setOnClickListener(View.OnClickListener {
            val text = referralTv?.text
            val myClip = ClipData.newPlainText("referral", text)
            myClipboard.setPrimaryClip(myClip)
            toast("Copied to clipboad")
        })

        GlobalScope.launch {
            when (val response = safeApiCall { Clients.api.myReferral() }) {
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        runOnUiThread {
                            referralTv.append(body()?.get("code")?.asString)
                            shareReferral.setOnClickListener {
                                share(referralTv.text.toString() + "\n You get 500 CB Credits on your successful Sign Up.")
                            }
                            shareWhatsapp.setOnClickListener {
                                share(referralTv.text.toString() + "\n You get 500 CB Credits on your successful Sign Up.")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
