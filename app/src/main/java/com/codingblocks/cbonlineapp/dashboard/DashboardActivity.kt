package com.codingblocks.cbonlineapp.dashboard

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.notifications.NotificationsActivity
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.fabnavigation.FabNavigation
import com.codingblocks.fabnavigation.FabNavigationAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import org.jetbrains.anko.intentFor

class DashboardActivity : AppCompatActivity(), DrawerLayout.DrawerListener, NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

    override fun onDrawerStateChanged(newState: Int) {
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
    }

    override fun onDrawerClosed(drawerView: View) {
    }

    override fun onDrawerOpened(drawerView: View) {
    }

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
        setSupportActionBar(dashboardToolbar)
        title = "Dashboard"
        val toggle = ActionBarDrawerToggle(
            this,
            dashboardDrawer,
            dashboardToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        dashboardDrawer.addDrawerListener(toggle)
        toggle.syncState()

        dashboardDrawer.addDrawerListener(this)
        dashboardNavigation.setNavigationItemSelectedListener(this)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.dashboard_notification -> {
            startActivity(intentFor<NotificationsActivity>())
            true
        }
        R.id.dashboard_cart -> {
            //Todo - Implement this
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}

