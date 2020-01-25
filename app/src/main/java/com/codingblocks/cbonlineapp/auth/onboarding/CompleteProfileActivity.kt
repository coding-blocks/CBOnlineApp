package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.JWTUtils
import com.codingblocks.cbonlineapp.util.KeyboardVisibilityUtil
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import kotlinx.android.synthetic.main.activity_complete_profile.*
import org.jetbrains.anko.contentView
import org.jetbrains.anko.intentFor
import org.koin.android.ext.android.inject

class CompleteProfileActivity : AppCompatActivity() {

    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityUtil
    private val sharedPrefs by inject<PreferenceHelper>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)
        val id = JWTUtils.getIdentity(sharedPrefs.SP_JWT_TOKEN_KEY).toString()
        completeBtn.setOnClickListener {
            startActivity(intentFor<DashboardActivity>())
            finish()
        }
        courseResumeBtn.setOnClickListener {
            startActivity(intentFor<DashboardActivity>())
            finish()
        }

        keyboardVisibilityHelper = KeyboardVisibilityUtil(contentView!!) {
            completeBtn.isVisible = it
        }
    }

    override fun onResume() {
        super.onResume()
        contentView!!.viewTreeObserver
            .addOnGlobalLayoutListener(keyboardVisibilityHelper.visibilityListener)
    }

    override fun onPause() {
        super.onPause()
        contentView!!.viewTreeObserver
            .removeOnGlobalLayoutListener(keyboardVisibilityHelper.visibilityListener)
    }
}
