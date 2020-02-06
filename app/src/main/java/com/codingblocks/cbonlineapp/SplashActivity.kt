package com.codingblocks.cbonlineapp

import android.content.Context
import android.os.Bundle
import com.codingblocks.cbonlineapp.auth.onboarding.OnBoardingActivity
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.JWTUtils
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject

class SplashActivity : BaseCBActivity() {
    private val sharedPrefs by inject<PreferenceHelper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalScope.launch {
            //            if (sharedPrefs.SP_ROLE_ID != 0) {
//                withContext(Dispatchers.IO) { runMigration() }
//            }
            val key = sharedPrefs.SP_JWT_TOKEN_KEY
            if (key.isNotEmpty() && !JWTUtils.isExpired(key)) {
                startActivity(intentFor<DashboardActivity>())
            } else {
                startActivity(intentFor<OnBoardingActivity>())
            }
            finish()
        }
    }

    private fun runMigration(): Boolean {
        val oldPrefsMap =
            getSharedPreferences("com.codingblocks.cbonlineapp.prefs", Context.MODE_PRIVATE).all
        val newPrefsMap =
            getSharedPreferences("com.codingblocks.cbonline.prefs", Context.MODE_PRIVATE)

        for (entry in oldPrefsMap) {
            val current = entry.value
            if (current is String) {
                newPrefsMap.edit().putString(entry.key, current).apply()
            }
        }
        return true
    }
}
