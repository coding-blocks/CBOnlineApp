package com.codingblocks.cbonlineapp.auth.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.auth.LoginActivity
import com.codingblocks.cbonlineapp.course.CourseActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.activity_on_boarding.*
import org.jetbrains.anko.intentFor

class OnBoardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)
        browseBtn.setOnClickListener {
            startActivity(intentFor<DashboardActivity>())
        }
        loginBtn.setOnClickListener {
            startActivity(intentFor<LoginActivity>())
        }
    }
}
