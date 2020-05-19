package com.codingblocks.cbonlineapp.dashboard

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.admin.AdminActivity
import com.codingblocks.cbonlineapp.auth.LoginActivity
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.course.checkout.CheckoutActivity
import com.codingblocks.cbonlineapp.jobs.JobsActivity
import com.codingblocks.cbonlineapp.notifications.NotificationsActivity
import com.codingblocks.cbonlineapp.profile.ProfileActivity
import com.codingblocks.cbonlineapp.profile.ReferralActivity
import com.codingblocks.cbonlineapp.purchases.PurchasesActivity
import com.codingblocks.cbonlineapp.settings.AboutActivity
import com.codingblocks.cbonlineapp.settings.SettingsActivity
import com.codingblocks.cbonlineapp.tracks.LearningTracksActivity
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.JWTUtils
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.colouriseToolbar
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.cbonlineapp.util.extensions.slideDown
import com.codingblocks.cbonlineapp.util.extensions.slideUp
import com.codingblocks.onlineapi.ErrorStatus
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.report_dialog.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.stateViewModel

const val LOGGED_IN = "loggedIn"

class DashboardActivity : BaseCBActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {
    private val vm: DashboardViewModel by stateViewModel()

    private val pagerAdapter: ViewPager2Adapter by lazy { ViewPager2Adapter(this) }
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        vm.isLoggedIn = intent?.getBooleanExtra(LOGGED_IN, false)

        setToolbar(dashboardToolbar, hasUpEnabled = false, homeButtonEnabled = false, title = getString(R.string.dashboard))
        val toggle = ActionBarDrawerToggle(this, dashboardDrawer, dashboardToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        dashboardDrawer.addDrawerListener(toggle)
        toggle.syncState()
        dashboardNavigation.setNavigationItemSelectedListener(this)
        dashboardBottomNav.setOnNavigationItemSelectedListener(this)
        initializeUI(vm.isLoggedIn ?: false)
        vm.errorLiveData.observer(this) { error ->
            when (error) {
                ErrorStatus.UNAUTHORIZED -> {
                    Components.showConfirmation(this, UNAUTHORIZED) {
                        if (it) {
                            startActivity(intentFor<LoginActivity>())
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
//        checkForUpdates()
        fetchToken()
    }

    private fun setUser() {
        vm.fetchUser().observe(this, Observer {
            referralContainer.isVisible = true
            if (JWTUtils.isExpired(vm.prefs.SP_JWT_TOKEN_KEY))
                vm.refreshToken()
            val navMenu = dashboardNavigation.menu
            navMenu.findItem(R.id.nav_inbox).isVisible = true
            navMenu.findItem(R.id.nav_purchases).isVisible = true
            navMenu.findItem(R.id.nav_admin).isVisible = vm.prefs.SP_ADMIN

            dashboardNavigation.getHeaderView(0).apply {
                findViewById<CircleImageView>(R.id.navHeaderImageView).loadImage(vm.prefs.SP_USER_IMAGE, true)
                findViewById<TextView>(R.id.navUsernameTv).text = ("Hello ${vm.prefs.SP_USER_NAME}")
            }
        })
    }

    private fun fetchToken() {
        val data = intent.data
        if (data != null && data.isHierarchical) {
            if (data.getQueryParameter("code") != null) {
                val grantCode = data.getQueryParameter("code") as String
                vm.fetchToken(grantCode)
            }
        }
    }

    private fun initializeUI(loggedIn: Boolean) {
        searchBtn.setOnClickListener {
            startActivity(intentFor<LearningTracksActivity>().singleTop())
        }
        pagerAdapter.apply {
            add(ViewPager2Adapter.FragmentName.EXPLORE)
            add(ViewPager2Adapter.FragmentName.COURSES)
            add(ViewPager2Adapter.FragmentName.HOME)
            add(ViewPager2Adapter.FragmentName.DOUBTS)
            add(ViewPager2Adapter.FragmentName.LIBRARY)
        }
        dashboardPager.apply {
            isUserInputEnabled = false
            adapter = pagerAdapter
            offscreenPageLimit = 4
        }
        if (loggedIn) {
            setUser()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                createShortcut()
            }
            dashboardBottomNav.selectedItemId = R.id.dashboard_home
        } else {
            dashboardNavigation.getHeaderView(0).apply {
                findViewById<TextView>(R.id.navUsernameTv).text = "Login/Signup"
            }
            dashboardBottomNav.selectedItemId = R.id.dashboard_explore
        }

        dashboardAppBarLayout.bringToFront()
    }

    @TargetApi(25)
    fun createShortcut() {

        val sM = getSystemService(ShortcutManager::class.java)
        val shortcutList: MutableList<ShortcutInfo> = ArrayList()

//        vm.courses.observeOnce {
//
//            doAsync {
//                it.take(2).forEachIndexed { index, courseRun ->
//
//                    val intent = Intent(this@DashboardActivity, MyCourseActivity::class.java)
//                    intent.action = Intent.ACTION_VIEW
//                    intent.putExtra(COURSE_ID, courseRun.courseRun.course.cid)
//                    intent.putExtra(RUN_ID, courseRun.courseRun.run.crUid)
//                    intent.putExtra(RUN_ATTEMPT_ID, courseRun.courseRun.runAttempt.attemptId)
//                    intent.putExtra(COURSE_NAME, courseRun.courseRun.course.title)
//
//                    val shortcut = ShortcutInfo.Builder(this@DashboardActivity, "topcourse$index")
//                    shortcut.setIntent(intent)
//                    shortcut.setLongLabel(courseRun.courseRun.course.subtitle)
//                    shortcut.setShortLabel(courseRun.courseRun.course.title)
//                    shortcut.setDisabledMessage("Login to open this")
//
//                    okHttpClient.newCall(Request.Builder().url(courseRun.courseRun.course.logo).build())
//                        .execute().body?.let {
//                        with(SVG.getFromInputStream(it.byteStream())) {
//                            val picDrawable = PictureDrawable(
//                                this.renderToPicture(
//                                    80, 80
//                                )
//                            )
//                            val bitmap = MediaUtils.getBitmapFromPictureDrawable(picDrawable)
//                            val circularBitmap = MediaUtils.getCircularBitmap(bitmap)
//                            shortcut.setIcon(Icon.createWithBitmap(circularBitmap))
//                            shortcutList.add(index, shortcut.build())
//                        }
//                    }
//                }
//                sM?.apply {
//                    dynamicShortcuts.clear()
//                    dynamicShortcuts = shortcutList
//                }
//            }
//        }
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

    private fun showReportDialog() {
        val dialog = AlertDialog.Builder(this).create()
        val view = layoutInflater.inflate(R.layout.report_dialog, null)
        view.primaryBtn.setOnClickListener {

            if (view.nameLayout.editText?.text.isNullOrEmpty()) {
                view.nameLayout.error = "Title Cannot Be Empty"
                return@setOnClickListener
            } else if (view.mobile.editText?.text.isNullOrEmpty() && view.mobile.editText?.text?.length!! < 10) {
                view.mobile.error = "Description Cannot Be Empty"
                return@setOnClickListener
            } else {
                val data = hashMapOf(
                    "title" to view.nameLayout.editText?.text.toString(),
                    "description" to view.mobile.editText?.text.toString(),
                    "oneauth-id" to vm.prefs.SP_ONEAUTH_ID,
                    "device" to Build.MODEL,
                    "version" to Build.VERSION.SDK_INT,
                    "app-version" to BuildConfig.VERSION_CODE

                )
                FirebaseFirestore.getInstance().collection("Reports")
                    .add(data)
                    .addOnSuccessListener {
                        dashboardDrawer.showSnackbar("Bug has been reported !!", Snackbar.LENGTH_SHORT)
                    }.addOnFailureListener {
                        toast("There was some error reporting the bug,Please Try Again")
                    }
                dialog.dismiss()
            }
        }
        dialog.apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            setView(view)
            setCancelable(true)
            show()
        }
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
            Toast.makeText(this, getString(R.string.back_press), Toast.LENGTH_SHORT).show()
            GlobalScope.launch {
                delay(2000)
                doubleBackToExitPressedOnce = false
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        fetchToken()
    }

    fun openProfile(view: View) {
        if (vm.isLoggedIn == false) {
            startActivity<LoginActivity>()
            finish()
        } else {
            startActivity<ProfileActivity>()
        }
    }

    fun openReferral(view: View) {
        startActivity<ReferralActivity>()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_contatus -> startActivity(intentFor<AboutActivity>())

            R.id.nav_admin -> startActivity(intentFor<AdminActivity>().singleTop())

            R.id.nav_settings -> startActivity(intentFor<SettingsActivity>().singleTop())

            R.id.nav_tracks -> startActivity(intentFor<LearningTracksActivity>().singleTop())

            R.id.nav_purchases -> startActivity(intentFor<PurchasesActivity>().singleTop())

            R.id.nav_hiring -> startActivity(intentFor<JobsActivity>().singleTop())

            R.id.nav_inbox -> startActivity(intentFor<ChatActivity>().singleTop())

            R.id.nav_feedback -> showReportDialog()

            R.id.dashboard_explore -> changeToolbar(getString(R.string.welcome), 0)

            R.id.dashboard_courses -> changeToolbar(getString(R.string.my_courses), 1)

            R.id.dashboard_home -> changeToolbar(getString(R.string.dashboard), 2)

            R.id.dashboard_doubts -> changeToolbar(getString(R.string.doubts), 3)

            R.id.dashboard_library -> changeToolbar(getString(R.string.library), 4)
        }
        dashboardDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun changeToolbar(title: String, pos: Int) {
        dashboardPager.setCurrentItem(pos, true)
        supportActionBar?.title = title
        if (pos == 0 || pos == 2)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dashboardToolbar.colouriseToolbar(this@DashboardActivity, R.drawable.toolbar_bg_dark, getColor(R.color.white))
            } else {
                dashboardToolbar.colouriseToolbar(this@DashboardActivity, R.drawable.toolbar_bg_dark, resources.getColor(R.color.white))
            }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dashboardToolbar.colouriseToolbar(this@DashboardActivity, R.drawable.toolbar_bg, getColor(R.color.black))
        } else {
            dashboardToolbar.colouriseToolbar(this@DashboardActivity, R.drawable.toolbar_bg, resources.getColor(R.color.black))
        }

        dashboardToolbarSecondary.post {
            when (pos) {
                0 -> {
                    dashboardToolbarSearch.slideDown()
                    dashboardToolbarSecondary.slideUp()
                }
                2 -> {
                    dashboardToolbarSearch.slideUp()
                    dashboardToolbarSecondary.slideDown()
                }
                else -> {
                    dashboardToolbarSearch.slideUp()
                    dashboardToolbarSecondary.slideUp()
                }
            }
        }
    }

    companion object {

        fun createDashboardActivityIntent(context: Context, loggedIn: Boolean = false): Intent {
            return context.intentFor<DashboardActivity>(LOGGED_IN to loggedIn)
        }
    }
}
