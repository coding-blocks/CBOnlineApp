package com.codingblocks.cbonlineapp.auth

import android.app.Activity
import android.os.Bundle
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.livedata.observer
import kotlinx.android.synthetic.main.activity_login2.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.design.snackbar
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class LoginActivity : BaseCBActivity(), AnkoLogger {

    val vm: AuthViewModel by stateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
        vm.isLoggedIn.observer(this) {
            navigateToActivity()
        }
        vm.errorLiveData.observer(this) {
            root.snackbar(it.capitalize())
        }

        vm.account.observer(this) {
            when (it) {
                AccountStates.NUMBER_NOT_VERIFIED -> {
                    showEmailSheet()
                }
                AccountStates.DO_NOT_EXIST -> {
                    replaceFragmentSafely(SignUpFragment(), containerViewId = R.id.loginContainer)
                }
                else -> {
                    // Todo - handle this
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

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }
}
