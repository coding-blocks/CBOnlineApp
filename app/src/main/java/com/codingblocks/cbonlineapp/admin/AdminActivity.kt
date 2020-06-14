package com.codingblocks.cbonlineapp.admin

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.core.content.ContextCompat
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.admin.doubts.AdminDoubtsFragment
import com.codingblocks.cbonlineapp.admin.doubts.DoubtReceiver
import com.codingblocks.cbonlineapp.admin.overview.AdminOverviewFragment
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.commons.FragmentChangeListener
import com.codingblocks.cbonlineapp.util.CustomDialog
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
            CustomDialog.showConfirmation(this, "admin") {
                finish()
            }
        }
    }


    private fun initializeUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bottomNavAdmin.defaultBackgroundColor = getColor(R.color.dark)
        } else {
            bottomNavAdmin.defaultBackgroundColor = ContextCompat.getColor(this, R.color.dark)
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
