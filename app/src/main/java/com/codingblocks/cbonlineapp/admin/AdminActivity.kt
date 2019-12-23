package com.codingblocks.cbonlineapp.admin

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.admin.doubts.AdminDoubtsFragment
import com.codingblocks.cbonlineapp.admin.doubts.DoubtReceiver
import com.codingblocks.cbonlineapp.admin.overview.AdminOverviewFragment
import com.codingblocks.cbonlineapp.commons.FragmentChangeListener
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.fabnavigation.FabNavigation
import com.codingblocks.fabnavigation.FabNavigationAdapter
import kotlinx.android.synthetic.main.activity_admin.*
import org.jetbrains.anko.contentView

class AdminActivity : AppCompatActivity(), FragmentChangeListener {

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
        setupAlarm()

        val roleId = 1
        if (roleId == 1 || roleId == 3) {
            initializeUI()
        } else {
            Components.showConfirmation(this, "admin") {
                finish()
            }
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
        bottomNavAdmin.defaultBackgroundColor = getColor(R.color.dark)
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

    private fun onKeyboardShown(state: Boolean) {
        bottomNavAdmin.isVisible = state
    }

    private var mKeyboardVisible: Boolean = false

    private val mLayoutKeyboardVisibilityListener = {
        val rectangle = Rect()
        val contentView = contentView!!
        contentView.getWindowVisibleDisplayFrame(rectangle)
        val screenHeight = contentView.rootView.height

        // r.bottom is the position above soft keypad or device button.
        // If keypad is shown, the rectangle.bottom is smaller than that before.
        val keypadHeight = screenHeight.minus(rectangle.bottom)
        // 0.15 ratio is perhaps enough to determine keypad height.
        val isKeyboardNowVisible = keypadHeight > screenHeight * 0.15

        if (mKeyboardVisible !== isKeyboardNowVisible) {
            if (isKeyboardNowVisible) {
                onKeyboardShown(false)
            } else {
                onKeyboardShown(true)
            }
        }

        mKeyboardVisible = isKeyboardNowVisible
    }

    override fun onResume() {
        super.onResume()
        contentView!!.viewTreeObserver
            .addOnGlobalLayoutListener(mLayoutKeyboardVisibilityListener)
    }

    override fun onPause() {
        super.onPause()
        contentView!!.viewTreeObserver
            .removeOnGlobalLayoutListener(mLayoutKeyboardVisibilityListener)
    }
}
