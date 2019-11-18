package com.codingblocks.cbonlineapp.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminActivity : AppCompatActivity() {

    private val viewModel by viewModel<MyCourseViewModel>()
    private val pagerAdapter by lazy {
        TabLayoutAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
    }
}
