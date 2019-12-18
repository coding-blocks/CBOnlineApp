package com.codingblocks.cbonlineapp.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.fabnavigation.FabNavigation
import com.codingblocks.fabnavigation.FabNavigationAdapter
import kotlinx.android.synthetic.main.app_bar_dashboard.*

class DashboardActivity : AppCompatActivity() {

    //    private val viewModel by viewModel<DashboardViewModel>()
    private val navigationAdapter: FabNavigationAdapter by lazy {
        FabNavigationAdapter(this, R.menu.bottom_nav_dashboard)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        initializeUI()
    }

    private fun initializeUI() {
        navigationAdapter.setupWithBottomNavigation(dashboardBottomNav)
        dashboardBottomNav.apply {
            defaultBackgroundColor = getColor(R.color.dark)
            titleState = (FabNavigation.TitleState.ALWAYS_SHOW)
            setOnTabSelectedListener(object : FabNavigation.OnTabSelectedListener {
                override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {
                    when (position) {
                        0 -> replaceFragmentSafely(
                            fragment = DashboardDoubtsFragment(),
                            containerViewId = R.id.dashboardPager,
                            allowStateLoss = true
                        )
                    }
                    return true
                }
            })
        }
        replaceFragmentSafely(
            fragment = DashboardDoubtsFragment(),
            containerViewId = R.id.dashboardPager,
            allowStateLoss = true
        )
    }
}

