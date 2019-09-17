package com.codingblocks.cbonlineapp.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.TabLayoutAdapter
import com.codingblocks.cbonlineapp.fragments.AboutFragment
import com.codingblocks.cbonlineapp.fragments.CourseContentFragment
import com.codingblocks.cbonlineapp.fragments.LeaderboardFragment
import com.codingblocks.cbonlineapp.fragments.OverviewFragment
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.viewmodels.MyCourseViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_my_course.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.coordinatorlayout.widget.CoordinatorLayout

class MyCourseActivity : AppCompatActivity, AnkoLogger, SwipeRefreshLayout.OnRefreshListener, MyCallbacks {
    override val coordinatorLayoutRoot: CoordinatorLayout
        get() = root_course

    private val viewModel by viewModel<MyCourseViewModel>()
    private val pagerAdapter by lazy {
        TabLayoutAdapter(supportFragmentManager)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_overview -> {
                course_pager.currentItem = 0
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_curriculum -> {
                course_pager.currentItem = 1
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_leaderboard -> {
                course_pager.currentItem = 2
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_about -> {
                course_pager.currentItem = 3
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    constructor()

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

        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (savedInstanceState == null) {
            viewModel.updatehit(viewModel.attemptId)
            viewModel.fetchCourse(viewModel.attemptId)
            bottom_navigation.selectedItemId = R.id.navigation_curriculum
            setupViewPager()
        }

//        resumeBtn.setOnClickListener {
//            viewModel.getResumeCourse().observeOnce {
//                if (it.isNotEmpty())
//                    with(it[0]) {
//                        when (contentable) {
//                            LECTURE -> {
//                                startActivity(intentFor<VideoPlayerActivity>(
//                                    VIDEO_ID to contentLecture.lectureId,
//                                    RUN_ATTEMPT_ID to attempt_id,
//                                    CONTENT_ID to ccid,
//                                    SECTION_ID to section_id,
//                                    DOWNLOADED to contentLecture.isDownloaded
//                                ).singleTop()
//                                )
//                            }
//                            DOCUMENT -> {
//                                startActivity(intentFor<PdfActivity>(
//                                    FILE_URL to contentDocument.documentPdfLink,
//                                    FILE_NAME to contentDocument.documentName + ".pdf"
//                                ).singleTop())
//                            }
//                            VIDEO -> {
//                                startActivity(intentFor<VideoPlayerActivity>(
//                                    VIDEO_URL to contentVideo.videoUrl,
//                                    RUN_ATTEMPT_ID to attempt_id,
//                                    CONTENT_ID to ccid
//                                ).singleTop())
//                            }
//                            else -> return@with
//                        }
//                    }
//                else {
//                    snackbar(rootView, "Nothing to show here")
//                }
//            }
//        }
//
    }

    private fun setupViewPager() {
        pagerAdapter.apply {
            add(OverviewFragment.newInstance(viewModel.attemptId, viewModel.runId))
            add(CourseContentFragment.newInstance(viewModel.attemptId))
            add(LeaderboardFragment.newInstance(viewModel.attemptId))
            add(AboutFragment.newInstance(viewModel.courseId, viewModel.attemptId))
        }
        course_pager.apply {
            adapter = pagerAdapter
            currentItem = 1
            offscreenPageLimit = 4
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onRefresh() {
        viewModel.fetchCourse(viewModel.attemptId)
    }
}

interface MyCallbacks {
    val coordinatorLayoutRoot: CoordinatorLayout
}
