package com.codingblocks.cbonlineapp.activities

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
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
import com.codingblocks.fabnavigation.FabNavigation
import com.codingblocks.fabnavigation.FabNavigationAdapter
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_my_course.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyCourseActivity : AppCompatActivity(), AnkoLogger, SwipeRefreshLayout.OnRefreshListener, MyCallbacks {
    override val coordinatorLayoutRoot: CoordinatorLayout
        get() = root_course

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

        if (savedInstanceState == null) {
            viewModel.updatehit(viewModel.attemptId)
            viewModel.fetchCourse(viewModel.attemptId)
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

    private fun initUI() {
        navigationAdapter = FabNavigationAdapter(this, R.menu.bottom_navigation_menu)
        navigationAdapter.setupWithBottomNavigation(bottom_navigation)
        setupViewPager()

        bottom_navigation.manageFloatingActionButtonBehavior(fab)
        bottom_navigation.accentColor = R.color.salmon
        bottom_navigation.titleState = FabNavigation.TitleState.ALWAYS_SHOW
        bottom_navigation.isTranslucentNavigationEnabled = true
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
            add(LeaderboardFragment.newInstance(viewModel.attemptId))
            add(AboutFragment.newInstance(viewModel.courseId, viewModel.attemptId))
        }
        course_pager.apply {
            adapter = pagerAdapter
            offscreenPageLimit = 3
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onRefresh() {
        viewModel.fetchCourse(viewModel.attemptId)
    }
}

private fun View.animateVisibility(visible: Int) {
    if (visible == View.VISIBLE) {
        visibility = View.VISIBLE
        alpha = 0f
        scaleX = 0f
        scaleY = 0f
    }
    val value = if (visible == View.VISIBLE) 1f else 0f
    animate()
        .alpha(value)
        .scaleX(value)
        .scaleY(value)
        .setDuration(300)
        .setInterpolator(OvershootInterpolator())
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                if (visible == View.GONE)
                    visibility = View.GONE
                else
                    animate()
                        .setInterpolator(LinearOutSlowInInterpolator())
                        .start()
            }

            override fun onAnimationCancel(animation: Animator) {
                if (visible == View.GONE) {
                    visibility = View.GONE
                }
            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        .start()
}

interface MyCallbacks {
    val coordinatorLayoutRoot: CoordinatorLayout
}
