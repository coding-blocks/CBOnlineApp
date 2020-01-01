package com.codingblocks.cbonlineapp.dashboard

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.AboutActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.admin.AdminActivity
import com.codingblocks.cbonlineapp.commons.FragmentChangeListener
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.course.checkout.CheckoutActivity
import com.codingblocks.cbonlineapp.dashboard.doubts.DashboardDoubtsFragment
import com.codingblocks.cbonlineapp.dashboard.explore.DashboardExploreFragment
import com.codingblocks.cbonlineapp.dashboard.home.DashboardHomeFragment
import com.codingblocks.cbonlineapp.dashboard.library.DashboardLibraryFragment
import com.codingblocks.cbonlineapp.dashboard.mycourses.DashboardMyCoursesFragment
import com.codingblocks.cbonlineapp.notifications.NotificationsActivity
import com.codingblocks.cbonlineapp.settings.SettingsActivity
import com.codingblocks.cbonlineapp.util.extensions.colouriseToolbar
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.fabnavigation.FabNavigation
import com.codingblocks.fabnavigation.FabNavigationAdapter
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.koin.androidx.viewmodel.ext.android.viewModel


class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, FragmentChangeListener, FabNavigation.OnTabSelectedListener {

    private val pagerAdapter by lazy { TabLayoutAdapter(supportFragmentManager) }
    private val navigationAdapter: FabNavigationAdapter by lazy {
        FabNavigationAdapter(this, R.menu.bottom_nav_dashboard)
    }
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val viewModel by viewModel<DashboardViewModel>()
    private var doubleBackToExitPressedOnce = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setUser()
        initializeUI()
    }

    private fun setUser() {
        viewModel.isAdmin.observer(this) {
            val navMenu = dashboardNavigation.menu
            navMenu.findItem(R.id.nav_admin).isVisible = it
        }
        viewModel.prefs.run {
            dashboardNavigation.getHeaderView(0).apply {
                navHeaderImageView.loadImage(userImage, true)
                navUsernameTv.append(" $firstName")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val data = this.intent.data
        if (data != null && data.isHierarchical) {
            if (data.getQueryParameter("code") != null) {
                fetchToken(data)
            }
        }
    }

    private fun fetchToken(data: Uri) {
        val grantCode = data.getQueryParameter("code") as String
        viewModel.fetchToken(grantCode)
    }

    private fun initializeUI() {
        setToolbar(dashboardToolbar, hasUpEnabled = false, homeButtonEnabled = false, title = "Dashboard")
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
            setOnTabSelectedListener(this@DashboardActivity)
        }
        dashboardBottomNav.setCurrentItem(1)
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
            setPagingEnabled(true)
            adapter = pagerAdapter
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
            startActivity(intentFor<CheckoutActivity>())
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

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

    override fun onResume() {
        super.onResume()
        checkForUpdates()
    }

    private fun checkForUpdates() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    this,
                    1001
                )
            }
        }
    }

    override fun onBackPressed() {
        if (dashboardDrawer.isDrawerOpen(GravityCompat.START)) {
            dashboardDrawer.closeDrawer(GravityCompat.START)
        } else {
            if (doubleBackToExitPressedOnce) {
                finishAffinity()
                return
            }
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
            GlobalScope.launch {
                delay(2000)
                doubleBackToExitPressedOnce = false
            }
        }
    }

    override fun openInbox(conversationId: String) {
    }

    override fun openClassroom() {
        dashboardBottomNav.setCurrentItem(1)
//        dashboardPager.setCurrentItem(1,true)
    }

    override fun openExplore() {
        dashboardBottomNav.setCurrentItem(2)
//        dashboardPager.setCurrentItem(2,true)
    }

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

}


