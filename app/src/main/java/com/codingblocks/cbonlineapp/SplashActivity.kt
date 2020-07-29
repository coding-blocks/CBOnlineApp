package com.codingblocks.cbonlineapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.codingblocks.cbonlineapp.auth.onboarding.OnBoardingActivity
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.JWTUtils
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.getPrefs
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject

class SplashActivity : BaseCBActivity() {
    private val sharedPrefs: PreferenceHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getPrefs().SP_DARK_MODE)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val key = sharedPrefs.SP_JWT_TOKEN_KEY
        if (key.isNotEmpty() && !JWTUtils.isExpired(key)) {
            startActivity(DashboardActivity.createDashboardActivityIntent(this, true))
        } else {
            startActivity(intentFor<OnBoardingActivity>())
        }
        finish()
    }
}
