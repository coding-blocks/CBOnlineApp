package com.codingblocks.cbonlineapp

import android.os.Bundle
import com.codingblocks.cbonlineapp.auth.onboarding.OnBoardingActivity
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.JWTUtils
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.onlineapi.Clients
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject

class SplashActivity : BaseCBActivity() {
    private val sharedPrefs: PreferenceHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val key = sharedPrefs.SP_JWT_TOKEN_KEY
        if (key.isNotEmpty() && !JWTUtils.isExpired(key)) {
            Clients.authJwt = sharedPrefs.SP_JWT_TOKEN_KEY
            Clients.refreshToken = sharedPrefs.SP_JWT_REFRESH_TOKEN
            startActivity(DashboardActivity.createDashboardActivityIntent(this, true))
        } else {
            startActivity(intentFor<OnBoardingActivity>())
        }
        finish()
    }
}
