package com.codingblocks.cbonlineapp.admin

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.admin.doubts.AdminDoubtsFragment
import com.codingblocks.cbonlineapp.admin.doubts.DoubtReceiver
import com.codingblocks.cbonlineapp.admin.overview.AdminOverviewFragment
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.commons.FragmentChangeListener
import com.codingblocks.cbonlineapp.util.Actions
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.EndlessService
import com.codingblocks.cbonlineapp.util.KeyboardVisibilityUtil
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.fabnavigation.FabNavigation
import com.codingblocks.fabnavigation.FabNavigationAdapter
import kotlinx.android.synthetic.main.activity_admin.*
import org.jetbrains.anko.contentView

class AdminActivity : BaseCBActivity(), FragmentChangeListener {

    private lateinit var keyboardVisibilityHelper: KeyboardVisibilityUtil

    override fun openInbox(conversationId: String) {
        bottomNavAdmin.setCurrentItem(2)
        replaceFragmentSafely(
            fragment = InboxFragment.newInstance(conversationId),
            containerViewId = R.id.pagerAdmin,
            allowStateLoss = true
        )
    }

    override fun openClassroom() {
    }

    override fun openExplore() {
    }

    private val navigationAdapter: FabNavigationAdapter by lazy {
        FabNavigationAdapter(this, R.menu.bottom_nav_admin)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        setToolbar(toolbarAdmin)
        navigationAdapter.setupWithBottomNavigation(bottomNavAdmin)

        keyboardVisibilityHelper = KeyboardVisibilityUtil(contentView!!) {
            //            completeBtn.isVisible = it
        }

        val roleId = 1
        if (roleId == 1 || roleId == 3) {
            initializeUI()
        } else {
            Components.showConfirmation(this, "admin") {
                finish()
            }
        }
    }

    private fun startMyService() {
        Intent(this, EndlessService::class.java).also {
            it.action = Actions.START.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                log("Starting the service in >=26 Mode")
                startForegroundService(it)
                return
            }
            log("Starting the service in < 26 Mode")
            startService(it)
        }
    }

    private fun setupAlarm() {
        val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val i = Intent(this, DoubtReceiver::class.java)

        val alarmIntent = PendingIntent.getBroadcast(
            this, 0, i, PendingIntent.FLAG_ONE_SHOT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmMgr.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                alarmIntent
            )
        }
    }

    private fun initializeUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bottomNavAdmin.defaultBackgroundColor = getColor(R.color.dark)
        } else {
            bottomNavAdmin.defaultBackgroundColor = resources.getColor(R.color.dark)
        }
        bottomNavAdmin.setOnTabSelectedListener(object : FabNavigation.OnTabSelectedListener {
            override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {
                when (position) {
                    0 -> replaceFragmentSafely(
                        fragment = AdminOverviewFragment(),
                        containerViewId = R.id.pagerAdmin,
                        allowStateLoss = true
                    )
                    1 -> replaceFragmentSafely(
                        fragment = AdminDoubtsFragment(),
                        containerViewId = R.id.pagerAdmin,
                        allowStateLoss = true
                    )
                    2 -> replaceFragmentSafely(
                        fragment = InboxFragment(),
                        containerViewId = R.id.pagerAdmin,
                        allowStateLoss = true
                    )
                }
                return true
            }
        })
        replaceFragmentSafely(
            fragment = AdminOverviewFragment(),
            containerViewId = R.id.pagerAdmin,
            allowStateLoss = true
        )
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
