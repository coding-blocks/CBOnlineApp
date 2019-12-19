package com.codingblocks.cbonlineapp.mycourse

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.mycourse.content.CourseContentFragment
import com.codingblocks.cbonlineapp.mycourse.leaderboard.LeaderboardFragment
import com.codingblocks.cbonlineapp.mycourse.overview.OverviewFragment
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.util.extensions.animateVisibility
import com.codingblocks.fabnavigation.FabNavigation
import com.codingblocks.fabnavigation.FabNavigationAdapter
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_my_course.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyCourseActivity : AppCompatActivity(), AnkoLogger, SwipeRefreshLayout.OnRefreshListener {

    private val viewModel by viewModel<MyCourseViewModel>()
    private val pagerAdapter by lazy {
        TabLayoutAdapter(supportFragmentManager)
    }
    private lateinit var navigationAdapter: FabNavigationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)

        setSupportActionBar(toolbar_mycourse)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.courseId = intent.getStringExtra(COURSE_ID) ?: ""
        title = intent.getStringExtra(COURSE_NAME)
        viewModel.attemptId = intent.getStringExtra(RUN_ATTEMPT_ID) ?: ""
        viewModel.runId = intent.getStringExtra(RUN_ID) ?: ""

        initUI()
        if (!MediaUtils.checkPermission(this)) {
            MediaUtils.isStoragePermissionGranted(this)
        }
        if (savedInstanceState == null) {
            viewModel.updateHit(viewModel.attemptId)
//            viewModel.fetchCourse(viewModel.attemptId)
        }
    }

    private fun initUI() {
        navigationAdapter = FabNavigationAdapter(this, R.menu.bottom_navigation_menu)
        navigationAdapter.setupWithBottomNavigation(bottom_navigation)
        setupViewPager()

        bottom_navigation.manageFloatingActionButtonBehavior(fab)
//        bottom_navigation.accentColor = R.color.salmon
        bottom_navigation.titleState = FabNavigation.TitleState.ALWAYS_SHOW
        bottom_navigation.isTranslucentNavigationEnabled = true
        bottom_navigation.setCurrentItem(1)
        bottom_navigation.setOnTabSelectedListener(object : FabNavigation.OnTabSelectedListener {
            override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {
                course_pager.setCurrentItem(position, false)
                if (position == 1) {
                    fab.animateVisibility(View.VISIBLE)
                } else {
                    if (fab.visibility == View.VISIBLE) {
                        fab.animateVisibility(View.GONE)
                    }
                }
                return true
            }
        })
    }

    private fun setupViewPager() {
        pagerAdapter.apply {
            add(OverviewFragment.newInstance(viewModel.attemptId, viewModel.runId))
            add(CourseContentFragment.newInstance(viewModel.attemptId))
            add(LeaderboardFragment.newInstance(viewModel.runId))
            add(AboutFragment.newInstance(viewModel.courseId, viewModel.attemptId))
        }
        course_pager.apply {
            adapter = pagerAdapter
            currentItem = 1
            offscreenPageLimit = 3
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onRefresh() {
//        viewModel.fetchCourse(viewModel.attemptId)
    }
}
