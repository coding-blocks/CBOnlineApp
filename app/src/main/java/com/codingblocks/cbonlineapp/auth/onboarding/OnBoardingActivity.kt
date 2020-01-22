package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.auth.LoginActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import kotlinx.android.synthetic.main.activity_on_boarding.*
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject

class OnBoardingActivity : AppCompatActivity() {

    private val sharedPrefs by inject<PreferenceHelper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)
        if (sharedPrefs.SP_JWT_TOKEN_KEY.isNotEmpty()) {
            startActivity(intentFor<DashboardActivity>())
        }
        browseBtn.setOnClickListener {
            startActivity(intentFor<DashboardActivity>())
        }
        loginBtn.setOnClickListener {
            startActivity(intentFor<LoginActivity>())
        }
    }
}
