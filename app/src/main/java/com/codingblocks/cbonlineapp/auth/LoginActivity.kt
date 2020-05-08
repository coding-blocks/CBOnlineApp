package com.codingblocks.cbonlineapp.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.auth.onboarding.AuthViewModel
import com.codingblocks.cbonlineapp.auth.onboarding.LoginHomeFragment
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.util.CREDENTIAL_PICKER_REQUEST
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseCBActivity(), AnkoLogger {

    val vm: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        replaceFragmentSafely(LoginHomeFragment(), containerViewId = R.id.loginContainer)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val frg: Fragment? = supportFragmentManager.findFragmentByTag("SignIn")
        when (requestCode) {
            CREDENTIAL_PICKER_REQUEST ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    frg?.onActivityResult(requestCode, resultCode, data)
                }
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
