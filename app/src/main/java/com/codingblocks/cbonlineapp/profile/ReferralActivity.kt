package com.codingblocks.cbonlineapp.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.safeApiCall
import kotlinx.android.synthetic.main.activity_referral.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.share

class ReferralActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_referral)
        GlobalScope.launch {
            when (val response = safeApiCall { Clients.api.myReferral() }) {
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful) {
                        runOnUiThread {
                            referralTv.append(body()?.get("code")?.asString)
                            shareReferral.setOnClickListener {
                                share(referralTv.text.toString() + "\n You get 500 CB Credits on your successful Sign Up.")
                            }
                        }
                    }
                }
            }
        }
    }
}
