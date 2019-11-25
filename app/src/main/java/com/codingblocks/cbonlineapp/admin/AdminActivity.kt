package com.codingblocks.cbonlineapp.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.admin.doubts.DoubtsFragment
import com.codingblocks.cbonlineapp.admin.overview.AdminOverviewFragment
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.ROLE_ID
import kotlinx.android.synthetic.main.activity_admin.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminActivity : AppCompatActivity() {

    private val viewModel by viewModel<MyCourseViewModel>()
    private val pagerAdapter by lazy {
        TabLayoutAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        setSupportActionBar(toolbarAdmin)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val roleId = intent.getIntExtra(ROLE_ID, -1)
        if (roleId == 1 || roleId == 2) {
            Components.showConfirmation(this, "admin") {
                finish()
            }
        } else {
            initializeUI()
        }
    }

    private fun initializeUI() {
        bottomNavAdmin.setOnNavigationItemSelectedListener {
            return@setOnNavigationItemSelectedListener when (it.itemId) {
                R.id.menu_overview -> {
                    pagerAdmin.currentItem = 0
                    true
                }
                R.id.menu_doubts -> {
                    pagerAdmin.currentItem = 2
                    true
                }
                else -> false

            }
        }
        pagerAdapter.add(AdminOverviewFragment())
        pagerAdapter.add(DoubtsFragment())
        pagerAdmin.apply {
            adapter = pagerAdapter
            currentItem = 0
            offscreenPageLimit = 0
        }
    }
}
