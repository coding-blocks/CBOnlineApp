package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.activity_complete_profile.*
import org.jetbrains.anko.intentFor

class CompleteProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)
        completeBtn.setOnClickListener {
            startActivity(intentFor<DashboardActivity>())
            finish()
        }
    }
}
