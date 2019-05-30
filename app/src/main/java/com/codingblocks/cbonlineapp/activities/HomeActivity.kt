package com.codingblocks.cbonlineapp.activities

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
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
import com.codingblocks.cbonlineapp.fragments.AllCourseFragment
import com.codingblocks.cbonlineapp.fragments.HomeFragment
import com.codingblocks.cbonlineapp.fragments.MyCoursesFragment
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.FileUtils
import com.codingblocks.cbonlineapp.util.NetworkUtils
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.viewmodels.HomeActivityViewModel
import com.codingblocks.cbonlineapp.viewmodels.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.UpdateAvailability
import com.squareup.picasso.Picasso
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_home.drawer_layout
import kotlinx.android.synthetic.main.activity_home.nav_view
import kotlinx.android.synthetic.main.app_bar_home.toolbar
import kotlinx.android.synthetic.main.nav_header_home.view.login_button
import kotlinx.android.synthetic.main.nav_header_home.view.nav_header_imageView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Arrays

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    AnkoLogger {
    private var updateUIReciver: BroadcastReceiver? = null
    private var filter: IntentFilter? = null

    private val viewModel by viewModel<HomeActivityViewModel>()

    private val appUpdateManager by lazy {
        AppUpdateManagerFactory.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.prefs = getPrefs()
        setContentView(R.layout.activity_home)
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
        drawer_layout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerClosed(drawerView: View) {
                if (viewModel.mFragmentToSet != null) {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_holder, viewModel.mFragmentToSet ?: HomeFragment())
                        .commit()
                    viewModel.mFragmentToSet = null
                }
            }

            override fun onDrawerOpened(drawerView: View) {}
        })
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {

            val transaction = supportFragmentManager.beginTransaction()
            transaction.commit()
            if (viewModel.prefs.SP_ACCESS_TOKEN_KEY != "access_token" && !NetworkUtils.isNetworkAvailable(
                    this
                )
            ) {
                transaction.replace(R.id.fragment_holder, MyCoursesFragment())
                setUser()
            } else {
                transaction.replace(R.id.fragment_holder, HomeFragment())
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
            nav_view.getHeaderView(0).login_button.setOnClickListener {
                startActivity(intentFor<LoginActivity>().singleTop())
            }
            fetchUser()
        }

        // adding label to nav drawer items
        nav_view.menu.getItem(3).setActionView(R.layout.menu_new)

        filter = IntentFilter()

        filter?.addAction("com.codingblocks.notification")

        updateUIReciver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                invalidateOptionsMenu()
            }
        }
    }

    private fun setUser() {
        if (viewModel.prefs.SP_USER_IMAGE.isNotEmpty())
            Picasso.with(this).load(viewModel.prefs.SP_USER_IMAGE).placeholder(R.drawable.defaultavatar).fit().into(
                nav_view.getHeaderView(0).nav_header_imageView
            )
        nav_view.getHeaderView(0).login_button.text = resources.getString(R.string.logout)
        if (Build.VERSION.SDK_INT >= 25) {
            createShortcut()
        }
        nav_view.getHeaderView(0).login_button.setOnClickListener {
            viewModel.prefs.SP_ACCESS_TOKEN_KEY = PreferenceHelper.ACCESS_TOKEN
            viewModel.prefs.SP_JWT_TOKEN_KEY = PreferenceHelper.JWT_TOKEN
            if (nav_view.getHeaderView(0).login_button.text == "Logout") {
                removeShortcuts()
                invalidateToken()
            }
            startActivity(intentFor<LoginActivity>().singleTop())
            finish()
        }
        val navMenu = nav_view.menu
        navMenu.findItem(R.id.nav_my_courses).isVisible = true
    }

    private fun invalidateToken() {
        doAsync {
            viewModel.invalidateToken(object : OnCompleteListener {
                override fun onResponseComplete() {
                    FileUtils.deleteDatabaseFile(this@HomeActivity, "app-database")
                }

                override fun onResponseFailed() {
                }
            })
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
        viewModel.fetchToken(grantCode, object : OnCompleteListener {
            override fun onResponseComplete() {
                fetchUser()
                Toast.makeText(this@HomeActivity, "Logged In", Toast.LENGTH_SHORT).show()
            }

            override fun onResponseFailed() {
                Components.showconfirmation(this@HomeActivity, "verify")
            }
        })
    }

    private fun fetchUser() {
        if (viewModel.prefs.SP_ACCESS_TOKEN_KEY != "access_token") {
            viewModel.getMe(object : OnCompleteListener {
                override fun onResponseComplete() {
                    setUser()
                }

                override fun onResponseFailed() {
                    nav_view.getHeaderView(0).login_button.setOnClickListener {
                        startActivity(intentFor<LoginActivity>().singleTop())
                    }
                }
            })
        } else {
            nav_view.getHeaderView(0).login_button.setOnClickListener {
                startActivity(intentFor<LoginActivity>().singleTop())
            }
        }
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

    @TargetApi(25)
    private fun createShortcut() {
        val sM = getSystemService(ShortcutManager::class.java)
        val intent1 = Intent(applicationContext, HomeActivity::class.java)
        intent1.action = Intent.ACTION_VIEW
        val shortcut1 = ShortcutInfo.Builder(this, "shortcut1")
            .setIntent(intent1)
            .setLongLabel("My Tickets")
            .setShortLabel("Open to show all tickets")
            .setDisabledMessage("Login to open this")
            .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
            .build()
        sM.dynamicShortcuts = Arrays.asList(shortcut1)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(updateUIReciver, filter)
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
        if (viewModel.notificationDao.count == 0) {
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
        unregisterReceiver(updateUIReciver)
    }

    @TargetApi(25)
    private fun removeShortcuts() {
        val shortcutManager = getSystemService(ShortcutManager::class.java)
        shortcutManager.disableShortcuts(Arrays.asList("shortcut1"))
        shortcutManager.removeAllDynamicShortcuts()
    }
}
