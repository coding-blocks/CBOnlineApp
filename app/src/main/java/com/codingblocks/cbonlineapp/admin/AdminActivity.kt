package com.codingblocks.cbonlineapp.admin

import android.os.Bundle
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.admin.doubts.AdminDoubtsFragment
import com.codingblocks.cbonlineapp.admin.overview.AdminOverviewFragment
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.commons.FragmentChangeListener
import com.codingblocks.cbonlineapp.util.CustomDialog
import com.codingblocks.cbonlineapp.util.extensions.getPrefs
import com.codingblocks.cbonlineapp.util.extensions.replaceFragmentSafely
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : BaseCBActivity(), FragmentChangeListener {

    override fun openInbox(conversationId: String) {
        bottomNavAdmin.selectedItemId = R.id.inbox
        replaceFragmentSafely(
            fragment = InboxFragment.newInstance(conversationId),
            containerViewId = R.id.pagerAdmin,
            allowStateLoss = true
        )
    }

    private val roleId by lazy {
        getPrefs().SP_ROLE_ID
    }

    override fun openClassroom() {
    }

    override fun openExplore() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        setToolbar(toolbarAdmin)

        if (roleId == 1 || roleId == 3) {
            initializeUI()
        } else {
            CustomDialog.showConfirmation(this, "admin") {
                finish()
            }
        }
    }

    private fun initializeUI() {
        replaceFragmentSafely(
            fragment = AdminOverviewFragment(),
            containerViewId = R.id.pagerAdmin,
            allowStateLoss = true
        )
        bottomNavAdmin.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inbox -> {
                    replaceFragmentSafely(
                        fragment = InboxFragment(),
                        containerViewId = R.id.pagerAdmin,
                        allowStateLoss = true
                    )
                    true
                }
                R.id.doubts -> {
                    replaceFragmentSafely(
                        fragment = AdminDoubtsFragment(),
                        containerViewId = R.id.pagerAdmin,
                        allowStateLoss = true
                    )
                    true
                }
                else -> {
                    replaceFragmentSafely(
                        fragment = AdminOverviewFragment(),
                        containerViewId = R.id.pagerAdmin,
                        allowStateLoss = true
                    )
                    true
                }
            }
        }
    }
}
