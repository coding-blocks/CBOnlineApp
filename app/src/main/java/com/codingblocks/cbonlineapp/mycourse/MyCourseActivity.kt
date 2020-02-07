package com.codingblocks.cbonlineapp.mycourse

import android.os.Bundle
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.analytics.AppCrashlyticsWrapper
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.mycourse.content.CourseContentFragment
import com.codingblocks.cbonlineapp.mycourse.library.CourseLibraryFragment
import com.codingblocks.cbonlineapp.mycourse.overview.OverviewFragment
import com.codingblocks.cbonlineapp.mycourse.player.VideoPlayerActivity
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.LECTURE
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.VIDEO
import com.codingblocks.cbonlineapp.util.extensions.animateVisibility
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.pageChangeCallback
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ErrorStatus
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_my_course.*
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyCourseActivity : BaseCBActivity(), AnkoLogger, SwipeRefreshLayout.OnRefreshListener {

    private val viewModel by viewModel<MyCourseViewModel>()
    private val pagerAdapter by lazy { TabLayoutAdapter(supportFragmentManager) }
    private val prefs by inject<PreferenceHelper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)
        setToolbar(toolbar_mycourse)
        Clients.authJwt = prefs.SP_JWT_TOKEN_KEY
        Clients.refreshToken = prefs.SP_JWT_REFRESH_TOKEN

        viewModel.courseId = intent.getStringExtra(COURSE_ID) ?: ""
        title = intent.getStringExtra(COURSE_NAME)
        viewModel.attemptId = intent.getStringExtra(RUN_ATTEMPT_ID) ?: ""
        viewModel.name = title as String? ?: ""
        viewModel.runId = intent.getStringExtra(RUN_ID) ?: ""

        initUI()
        if (!MediaUtils.checkPermission(this)) {
            MediaUtils.isStoragePermissionGranted(this)
        }
        viewModel.getNextContent().observer(this) { content ->
            courseResumeBtn.setOnClickListener {
                when (content.contentable) {
                    LECTURE, VIDEO -> startActivity(
                        intentFor<VideoPlayerActivity>(
                            CONTENT_ID to content.contentId,
                            SECTION_ID to content.sectionId
                        ).singleTop()
                    )
                }
            }
        }

        viewModel.errorLiveData.observer(this) {
            when (it) {
                ErrorStatus.NO_CONNECTION -> {
                    myCourseRoot.showSnackbar(it, Snackbar.LENGTH_SHORT, dashboardBottomNav)
                }
                ErrorStatus.TIMEOUT -> {
                    myCourseRoot.showSnackbar(it, Snackbar.LENGTH_INDEFINITE, dashboardBottomNav) {
                        viewModel.fetchSections()
                        viewModel.getStats()
                    }
                }
                ErrorStatus.UNAUTHORIZED -> {
                    Components.showConfirmation(this, UNAUTHORIZED) {
                    }
                }
                else -> {
                    myCourseRoot.showSnackbar(it, Snackbar.LENGTH_SHORT, dashboardBottomNav)
                    AppCrashlyticsWrapper.log(it)
                }
            }
        }
    }

    private fun initUI() {
        setupViewPager()
    }

    private fun setupViewPager() {
        myCourseTabs.setupWithViewPager(course_pager)
        pagerAdapter.apply {
            add(OverviewFragment(), getString(R.string.dashboard))
            add(CourseContentFragment(), getString(R.string.curriculum))
            add(CourseLibraryFragment(), getString(R.string.library))
        }

        course_pager.apply {
            setPagingEnabled(true)
            adapter = pagerAdapter
            currentItem = 0
            offscreenPageLimit = 2
            addOnPageChangeListener(
                pageChangeCallback { pos, fl, i2 ->
                    if (pos == 1) {
                        fab.animateVisibility(View.VISIBLE)
                    } else {
                        if (fab.visibility == View.VISIBLE) {
                            fab.animateVisibility(View.GONE)
                        }
                    }
                }
            )
        }
    }

    override fun onRefresh() {
//        viewModel.fetchCourse(viewModel.attemptId)
    }
}
