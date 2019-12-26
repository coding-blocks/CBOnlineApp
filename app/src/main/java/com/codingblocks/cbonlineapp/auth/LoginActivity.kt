package com.codingblocks.cbonlineapp.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.auth.onboarding.LoginHomeFragment
import com.codingblocks.cbonlineapp.util.RESOLVEHINT
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credential.EXTRA_KEY
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.api.GoogleApiClient


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        replaceFragmentSafely(LoginHomeFragment(), containerViewId = R.id.loginContainer)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESOLVEHINT) {
            if (resultCode == RESULT_OK) {
                val cred = data?.getParcelableExtra(EXTRA_KEY) as Credential
                val unformattedPhone = cred.id
            }
        }
    }


}
