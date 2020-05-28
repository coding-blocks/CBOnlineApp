package com.codingblocks.cbonlineapp.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.CREDENTIAL_PICKER_REQUEST
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class LoginActivity : BaseCBActivity(), AnkoLogger {

    val vm: AuthViewModel by stateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        vm.isLoggedIn.observer(this) {
            navigateToActivity()
        }

        vm.account.observer(this) {
            when (it) {
                AccountStates.NUMBER_NOT_VERIFIED -> {
                    showEmailSheet()
                }
            }
        }
        replaceFragmentSafely(LoginHomeFragment(), containerViewId = R.id.loginContainer)
    }

    private fun showEmailSheet() {
        val emailBottomSheetFragment = LoginEmailBottomSheet()
        emailBottomSheetFragment.show(supportFragmentManager, "goodiesRequestFragment")
    }

    /**
     * Always call finish after setResult or startActivity otherwise you'll lose reference of previous activity
     */
    private fun navigateToActivity() {

        if (callingActivity == null) {
            startActivity(DashboardActivity.createDashboardActivityIntent(this, true))
        } else {
            setResult(Activity.RESULT_OK)
        }
        finish()

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
