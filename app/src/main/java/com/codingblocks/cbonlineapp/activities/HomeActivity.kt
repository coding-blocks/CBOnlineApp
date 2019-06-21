package com.codingblocks.cbonlineapp.activities

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ShortcutManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.observeOnce
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.fragments.AllCourseFragment
import com.codingblocks.cbonlineapp.fragments.HomeFragment
import com.codingblocks.cbonlineapp.fragments.MyCoursesFragment
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.viewmodels.HomeActivityViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.squareup.picasso.Picasso
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_home.drawer_layout
import kotlinx.android.synthetic.main.activity_home.nav_view
import kotlinx.android.synthetic.main.app_bar_home.toolbar
import kotlinx.android.synthetic.main.nav_header_home.login_button
import kotlinx.android.synthetic.main.nav_header_home.nav_header_imageView
import kotlinx.android.synthetic.main.nav_header_home.view.login_button
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Arrays

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    AnkoLogger, View.OnClickListener, DrawerLayout.DrawerListener {

    private var updateUIReceiver: BroadcastReceiver? = null
    private var filter: IntentFilter? = null
    private val viewModel by viewModel<HomeActivityViewModel>()
    private val appUpdateManager by lazy {
        AppUpdateManagerFactory.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel.prefs = getPrefs()

        setSupportActionBar(toolbar)
        title = "Coding Blocks"

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        drawer_layout.addDrawerListener(this)
        nav_view.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {

            val transaction = supportFragmentManager.beginTransaction()
            if (viewModel.prefs.SP_ACCESS_TOKEN_KEY != "access_token") {
                val navMenu = nav_view.menu
                navMenu.findItem(R.id.nav_my_courses).isVisible = true
                transaction.replace(R.id.fragment_holder, MyCoursesFragment()).commit()
                setUser()
            } else {
                transaction.replace(R.id.fragment_holder, HomeFragment()).commit()
            }
            when {
                intent.getStringExtra("course") == "mycourses" -> transaction.replace(
                    R.id.fragment_holder,
                    MyCoursesFragment()
                )
                intent.getStringExtra("course") == "allcourses" -> transaction.replace(
                    R.id.fragment_holder,
                    AllCourseFragment()
                )
            }
            nav_view.getHeaderView(0).login_button.setOnClickListener(this)
            fetchUser()
        }

        // adding label to nav drawer items
//        nav_view.menu.getItem(3).setActionView(R.layout.menu_new)

        filter = IntentFilter()

        filter?.addAction("com.codingblocks.notification")

        updateUIReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                invalidateOptionsMenu()
            }
        }

        viewModel.invalidateTokenProgress.observer(this) {
            if (it) {
                doAsync {
                    FileUtils.deleteDatabaseFile(this@HomeActivity, "app-database")
                }
            }
        }
    }

    private fun setUser() {
        if (viewModel.prefs.SP_USER_IMAGE.isNotEmpty())
            nav_header_imageView.apply {
                setOnClickListener(this@HomeActivity)
                Picasso.with(this@HomeActivity).load(viewModel.prefs.SP_USER_IMAGE)
                    .placeholder(R.drawable.defaultavatar).fit().into(this)
            }
        viewModel.prefs.SP_ACCESS_TOKEN_KEY = PreferenceHelper.ACCESS_TOKEN
        viewModel.prefs.SP_JWT_TOKEN_KEY = PreferenceHelper.JWT_TOKEN
        login_button.apply {
            text = resources.getString(R.string.logout)
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

        checkForUpdates()
    }

    private fun checkForUpdates() {
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                // For a flexible update, use AppUpdateType.FLEXIBLE
                appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)
            ) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    1001
                )
            }
        }
    }

    private fun fetchToken(data: Uri) {
        val grantCode = data.getQueryParameter("code") as String

        viewModel.fetchTokenProgress.observeOnce {
            if (it) {
                fetchUser()
                Toast.makeText(this@HomeActivity, "Logged In", Toast.LENGTH_SHORT).show()
            } else {
                Components.showconfirmation(this@HomeActivity, "verify")
            }
        }
        viewModel.fetchToken(grantCode)
    }

    private fun fetchUser() {

        viewModel.getMeProgress.observer(this) {
            if (it) {
                setUser()
            }
        }
        viewModel.getMe()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (viewModel.doubleBackToExitPressedOnce) {
                finishAffinity()
                return
            }
            viewModel.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({
                viewModel.doubleBackToExitPressedOnce = false
            }, 2000)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_all_courses -> {
                changeFragment("All Courses")
            }
            R.id.nav_home -> {
                changeFragment("Home")
            }
            R.id.nav_my_courses -> {
                changeFragment("My Courses")
            }
            R.id.nav_whatsapp -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setPackage("com.whatsapp")
                intent.data = Uri.parse("https://wa.me/919811557517")
                if (packageManager.resolveActivity(intent, 0) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Please install whatsApp", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_preferences -> {
                startActivity(intentFor<SettingsActivity>().singleTop())
            }
            R.id.nav_contactUs -> {
                startActivity(intentFor<AboutActivity>().singleTop())
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    private fun changeFragment(filter: String) {
        when (filter) {
            "All Courses" -> viewModel.mFragmentToSet = AllCourseFragment()
            "Home" -> viewModel.mFragmentToSet = HomeFragment()
            "My Courses" -> viewModel.mFragmentToSet = MyCoursesFragment()
        }
        onBackPressed()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(updateUIReceiver, filter)
        invalidateOptionsMenu()

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        IMMEDIATE,
                        this,
                        1001
                    )
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_notifications, menu)
        val menuItem = menu.findItem(R.id.action_notifications)
        if (viewModel.getNotificationCount() == 0) {
            menuItem.icon = resources.getDrawable(R.drawable.ic_notification)
        } else {
            menuItem.icon = resources.getDrawable(R.drawable.ic_notification_active)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                startActivity(intentFor<NotificationsActivity>())
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(updateUIReceiver)
    }

    @TargetApi(25)
    private fun removeShortcuts() {
        val shortcutManager = getSystemService(ShortcutManager::class.java)
        shortcutManager.disableShortcuts((Arrays.asList("topcourse$0")))
        shortcutManager.disableShortcuts((Arrays.asList("topcourse$1")))
        shortcutManager.removeAllDynamicShortcuts()
    }

    override fun onDrawerStateChanged(newState: Int) {
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
    }

    override fun onDrawerClosed(drawerView: View) {
    }

    override fun onDrawerOpened(drawerView: View) {
        if (viewModel.mFragmentToSet != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_holder, viewModel.mFragmentToSet ?: HomeFragment())
                .commit()
            viewModel.mFragmentToSet = null
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.login_button -> {
                if (viewModel.prefs.SP_ACCESS_TOKEN_KEY != "access_token") {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
                        removeShortcuts()
                    }
                    viewModel.invalidateToken()
                }
                startActivity(intentFor<LoginActivity>().singleTop())
                finish()
            }
            R.id.nav_header_imageView -> Components.openChrome(
                this,
                "https://account.codingblocks.com"
            )
        }
    }
}
