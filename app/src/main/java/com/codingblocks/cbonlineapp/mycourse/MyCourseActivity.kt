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
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.LECTURE
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.VIDEO
import com.codingblocks.cbonlineapp.util.extensions.animateVisibility
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.pageChangeCallback
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.onlineapi.ErrorStatus
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_my_course.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class MyCourseActivity : BaseCBActivity(), AnkoLogger, SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: MyCourseViewModel by stateViewModel()
    private val pagerAdapter by lazy { TabLayoutAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)
        setToolbar(toolbar_mycourse)
        title = intent.getStringExtra(COURSE_NAME)
        viewModel.attemptId = intent.getStringExtra(RUN_ATTEMPT_ID) ?: ""
        viewModel.name = intent.getStringExtra(COURSE_NAME) ?: ""
        if (!MediaUtils.checkPermission(this)) {
            MediaUtils.isStoragePermissionGranted(this)
        }
        viewModel.nextContent.observer(this) { content ->
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
                    myCourseRoot.showSnackbar(it, Snackbar.LENGTH_SHORT)
                }
                ErrorStatus.TIMEOUT -> {
                    myCourseRoot.showSnackbar(it, Snackbar.LENGTH_INDEFINITE) {
                        viewModel.fetchSections()
                        viewModel.getStats()
                    }
                }
                ErrorStatus.UNAUTHORIZED -> {
                    Components.showConfirmation(this, UNAUTHORIZED) {
                    }
                }
                else -> {
                    myCourseRoot.showSnackbar(it, Snackbar.LENGTH_SHORT)
                    AppCrashlyticsWrapper.log(it)
                }
            }
        }
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
        viewModel.fetchSections(true)
    }
}
