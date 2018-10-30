package com.codingblocks.cbonlineapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ahmadrosid.svgloader.SvgLoader
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
import com.codingblocks.cbonlineapp.adapters.TabLayoutAdapter
import com.codingblocks.cbonlineapp.fragments.AnnouncementsFragment
import com.codingblocks.cbonlineapp.fragments.CourseContentFragment
import com.codingblocks.cbonlineapp.fragments.DoubtsFragment
import com.codingblocks.cbonlineapp.fragments.OverviewFragment
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.activity_my_course.*


class MyCourseActivity : AppCompatActivity() {

    lateinit var courseId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupViewPager()
        title = intent.getStringExtra("courseName")
        courseId = intent.getStringExtra("run_id")

        Clients.onlineV2PublicClient.enrolledCourseById("JWT eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Ijk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJyb2xlSWQiOjIsImNyZWF0ZWRBdCI6IjIwMTgtMDktMjdUMTM6MTA6NTkuMzk2WiIsInVwZGF0ZWRBdCI6IjIwMTgtMTAtMTJUMTA6MDY6NTMuNjE0WiIsImNsaWVudElkIjoiYjI4NDFlNGQtZmY0Yi00OTI5LWJhOGUtMmQxZmM0ZTYwMTFmIiwiaXNUb2tlbkZvckFkbWluIjpmYWxzZSwiaWF0IjoxNTM5NTY1NzE4LCJleHAiOjE1Mzk1NjcyMTh9.U7JmDSg4L_5bBmMBcFSkpQN_t3lYb_himb88eBJqqUBD2e2xS9PGcB6dFTHbiwHj7qhzcOC85x7Lklbi7oWdHrW7fL25LOxg52JT10GnDX41hxamo1fnvvnJ3HI0hx1gvUElaAmia4Kyg1VVgLp7EiH9rphMRV_lhTLz0nF2usz92eGh01P0V9XYqYiiVWH3H_1-vqktHA0yLWHw27taKqruZPdGAWjBnN7aO7lmk3IhfU0fvQkgumFxtS_Jmy_cPL-kJglDq3sEoDUtuOjpt4H25loy_GMufBQeogevpZfWPkcNqYpSzEAqWb5Rh6oMXd84SnAyUkbr4ytqoE4ZhA", courseId).enqueue(retrofitcallback { throwable, response ->
            response?.body().let {
//


            }
        })

    }


    private fun setupViewPager() {
        val adapter = TabLayoutAdapter(supportFragmentManager)
        adapter.add(OverviewFragment(), "")
        adapter.add(AnnouncementsFragment(), "")
        adapter.add(CourseContentFragment(), "")
        adapter.add(DoubtsFragment(), "")
        htab_viewpager.adapter = adapter
        htab_tabs.setupWithViewPager(htab_viewpager)
        htab_tabs.getTabAt(0)?.setIcon(R.drawable.ic_menu)
        htab_tabs.getTabAt(1)?.setIcon(R.drawable.ic_announcement)
        htab_tabs.getTabAt(2)?.setIcon(R.drawable.ic_docs)
        htab_tabs.getTabAt(3)?.setIcon(R.drawable.ic_support)

    }

}
