package com.codingblocks.cbonlineapp.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.auth.onboarding.LoginHomeFragment
import com.codingblocks.cbonlineapp.util.RESOLVEHINT
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        replaceFragmentSafely(LoginHomeFragment(), containerViewId = R.id.loginContainer)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val frg: Fragment? = supportFragmentManager.findFragmentByTag("SignIn")

        if (requestCode == RESOLVEHINT) {
            if (resultCode == RESULT_OK) {
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
