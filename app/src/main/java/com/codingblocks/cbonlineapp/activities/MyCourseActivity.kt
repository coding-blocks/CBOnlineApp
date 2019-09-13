package com.codingblocks.cbonlineapp.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingblocks.cbonlineapp.R
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

class MyCourseActivity : AppCompatActivity(), AnkoLogger, SwipeRefreshLayout.OnRefreshListener {

    private val viewModel by viewModel<MyCourseViewModel>()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.navigation_overview -> {
                val fragment = OverviewFragment.newInstance(viewModel.attemptId, viewModel.runId)
                supportFragmentManager.commit {
                    replace(R.id.container_course, fragment)
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_curriculum -> {
                val fragment = CourseContentFragment.newInstance(viewModel.attemptId)
                supportFragmentManager.commit {
                    replace(R.id.container_course, fragment)
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_leaderboard -> {
                val fragment = LeaderboardFragment.newInstance(viewModel.attemptId)
                supportFragmentManager.commit {
                    replace(R.id.container_course, fragment)
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_about -> {
                val fragment = AboutFragment.newInstance(viewModel.courseId, viewModel.attemptId)
                supportFragmentManager.commit {
                    replace(R.id.container_course, fragment)
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)

        viewModel.courseId = intent.getStringExtra(COURSE_ID) ?: ""
        title = intent.getStringExtra(COURSE_NAME)
        viewModel.attemptId = intent.getStringExtra(RUN_ATTEMPT_ID) ?: ""
        viewModel.runId = intent.getStringExtra(RUN_ID) ?: ""

        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if (savedInstanceState == null) {
            viewModel.updatehit(viewModel.attemptId)
            viewModel.fetchCourse(viewModel.attemptId)
            bottom_navigation.selectedItemId = R.id.navigation_curriculum
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

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onRefresh() {
        viewModel.fetchCourse(viewModel.attemptId)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bottom_navigation.selectedItemId = R.id.navigation_curriculum
    }
}
