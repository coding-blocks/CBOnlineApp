package com.codingblocks.cbonlineapp.mycourse

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.mycourse.content.CourseContentFragment
import com.codingblocks.cbonlineapp.mycourse.library.CourseLibraryFragment
import com.codingblocks.cbonlineapp.mycourse.overview.OverviewFragment
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.util.extensions.animateVisibility
import com.codingblocks.cbonlineapp.util.extensions.pageChangeCallback
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import kotlinx.android.synthetic.main.activity_my_course.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyCourseActivity : AppCompatActivity(), AnkoLogger, SwipeRefreshLayout.OnRefreshListener {

    private val viewModel by viewModel<MyCourseViewModel>()
    private val pagerAdapter by lazy { TabLayoutAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)
        setToolbar(toolbar_mycourse)

        viewModel.courseId = intent.getStringExtra(COURSE_ID) ?: ""
        title = intent.getStringExtra(COURSE_NAME)
        viewModel.attemptId = intent.getStringExtra(RUN_ATTEMPT_ID) ?: ""
        viewModel.name = title as String? ?: ""
        viewModel.runId = intent.getStringExtra(RUN_ID) ?: ""

        initUI()
        if (!MediaUtils.checkPermission(this)) {
            MediaUtils.isStoragePermissionGranted(this)
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
            offscreenPageLimit = 3
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
