package com.codingblocks.cbonlineapp.mycourse

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.mycourse.content.CourseContentFragment
import com.codingblocks.cbonlineapp.mycourse.library.CourseLibraryFragment
import com.codingblocks.cbonlineapp.mycourse.overview.OverviewFragment
import com.codingblocks.cbonlineapp.mycourse.player.VideoPlayerActivity
import com.codingblocks.cbonlineapp.mycourse.quiz.QuizActivity
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.LECTURE
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.ProgressWorker
import com.codingblocks.cbonlineapp.util.QNA
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.VIDEO
import com.codingblocks.cbonlineapp.util.extensions.animateVisibility
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.pageChangeCallback
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import kotlinx.android.synthetic.main.activity_my_course.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

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
        viewModel.getNextContent().observer(this) { content ->
            courseResumeBtn.setOnClickListener {
                when (content.contentable) {
                    LECTURE, VIDEO -> startActivity(intentFor<VideoPlayerActivity>(CONTENT_ID to content.contentId, SECTION_ID to content.sectionId).singleTop())
                    QNA -> startActivity(intentFor<QuizActivity>(CONTENT_ID to content.contentId, SECTION_ID to content.sectionId).singleTop())
                }
                updateProgress(content.contentId)
            }
        }
    }

    private fun updateProgress(contentId: String) {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val progressData: Data = workDataOf(CONTENT_ID to contentId, RUN_ATTEMPT_ID to viewModel.attemptId)
        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<ProgressWorker>()
                .setConstraints(constraints)
                .setInputData(progressData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance()
            .enqueue(request)
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
