package com.codingblocks.cbonlineapp.dashboard

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.codingblocks.cbonlineapp.AboutActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.admin.AdminActivity
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.dashboard.doubts.DashboardDoubtsFragment
import com.codingblocks.cbonlineapp.dashboard.explore.DashboardExploreFragment
import com.codingblocks.cbonlineapp.dashboard.home.DashboardHomeFragment
import com.codingblocks.cbonlineapp.dashboard.library.DashboardLibraryFragment
import com.codingblocks.cbonlineapp.dashboard.mycourses.DashboardMyCoursesFragment
import com.codingblocks.cbonlineapp.notifications.NotificationsActivity
import com.codingblocks.cbonlineapp.settings.SettingsActivity
import com.codingblocks.cbonlineapp.util.extensions.colouriseToolbar
import com.codingblocks.fabnavigation.FabNavigation
import com.codingblocks.fabnavigation.FabNavigationAdapter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop


class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_contatus -> {
                startActivity(intentFor<AboutActivity>())
            }
            R.id.nav_admin -> {
                startActivity(intentFor<AdminActivity>().singleTop())
            }
            R.id.nav_settings -> {
                startActivity(intentFor<SettingsActivity>().singleTop())
            }
        }
        dashboardDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    private val pagerAdapter by lazy {
        TabLayoutAdapter(supportFragmentManager)
    }
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
        dashboardNavigation.setNavigationItemSelectedListener(this)
        navigationAdapter.setupWithBottomNavigation(dashboardBottomNav)
        setupViewPager()

        dashboardBottomNav.apply {
            defaultBackgroundColor = getColor(R.color.dark)
            titleState = (FabNavigation.TitleState.ALWAYS_SHOW)
            setOnTabSelectedListener(object : FabNavigation.OnTabSelectedListener {
                override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {
                    if (position == 2) {
                        dashboardToolbarSecondary.isVisible = true
                        dashboardToolbar.colouriseToolbar(this@DashboardActivity, R.drawable.toolbar_bg_dark, getColor(R.color.white))
                    } else {
                        dashboardToolbarSecondary.isVisible = false
                        dashboardToolbar.colouriseToolbar(this@DashboardActivity, R.drawable.toolbar_bg, getColor(R.color.black))
                    }
                    dashboardPager.setCurrentItem(position, true)
                    return true
                }
            })
        }
    }

    private fun setupViewPager() {
        pagerAdapter.apply {
            add(DashboardExploreFragment())
            add(DashboardMyCoursesFragment())
            add(DashboardHomeFragment())
            add(DashboardDoubtsFragment())
            add(DashboardLibraryFragment())
        }
        dashboardPager.apply {
            adapter = pagerAdapter
            currentItem = 1
            offscreenPageLimit = 4
        }
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
            TODO("Implement this")
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


}


