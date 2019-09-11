package com.codingblocks.cbonlineapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.viewmodels.MyCourseViewModel
import kotlinx.android.synthetic.main.activity_my_course.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyCourseActivity : AppCompatActivity(), AnkoLogger {

    private val viewModel by viewModel<MyCourseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)
//        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.courseId = intent.getStringExtra(COURSE_ID) ?: ""
        title = intent.getStringExtra(COURSE_NAME)
        viewModel.attemptId = intent.getStringExtra(RUN_ATTEMPT_ID) ?: ""
        viewModel.runId = intent.getStringExtra(RUN_ID) ?: ""

        // Intent in = new  Intent(this,MainActi.class)
        //i.putSTRING("KEY",user)
//
//        if (viewModel.attemptId.isEmpty()) {
//            viewModel.attemptId = viewModel.getRunAttempt(viewModel.runId)
//        }
//        if (savedInstanceState == null) {
//
//            viewModel.updatehit(viewModel.attemptId)
//            viewModel.fetchCourse(viewModel.attemptId)
//            setupViewPager(viewModel.attemptId, viewModel.courseId)
//        }
//
//
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
//        resetBtn.setOnClickListener {
//            confirmReset()
//        }
//
//        viewModel.resetProgress.observe(this, Observer {
//            finish()
//        })
    }

//    private fun confirmReset() {
//        val builder = android.app.AlertDialog.Builder(this)
//        val inflater = layoutInflater
//        val customView = inflater.inflate(R.layout.custom_dialog, null)
//        customView.okBtn.text = "Yes"
//        customView.cancelBtn.text = "No"
//        customView.description.text = "Are you sure you want to reset progress?"
//        builder.setCancelable(false)
//        builder.setView(customView)
//        val dialog = builder.create()
//        customView.cancelBtn.setOnClickListener {
//            dialog.dismiss()
//        }
//        customView.okBtn.setOnClickListener {
//            viewModel.resetProgress()
//        }
//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//        dialog.show()
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        viewModel.getRunByAtemptId(viewModel.attemptId).observer(this) {
//            courseProgress.progress = it.progress.toInt()
//            contentCompletedTv.text = "${it.completedContents} of ${it.totalContents} Contents Completed"
//            batchEndTv.text = "Batch Ends ${getDateForTime(it.crRunEnd)}"
//        }
//    }
//
//    private fun setupViewPager(crUid: String, crCourseId: String) {
//        val adapter = TabLayoutAdapter(supportFragmentManager)
//        adapter.add(OverviewFragment.newInstance(viewModel.attemptId, crUid), "Dashboard")
//        adapter.add(CourseContentFragment.newInstance(viewModel.attemptId), "Course Content")
//        adapter.add(LeaderboardFragment.newInstance(viewModel.runId), "Leaderboard")
//        adapter.add(DoubtsFragment.newInstance(viewModel.attemptId, crCourseId), "Doubts")
//        adapter.add(AnnouncementsFragment.newInstance(viewModel.courseId, viewModel.attemptId), "About")
//
//        htab_viewpager.adapter = adapter
//        htab_tabs.setupWithViewPager(htab_viewpager)
//        htab_tabs.getTabAt(0)?.setIcon(R.drawable.ic_chart_line)
//        htab_tabs.getTabAt(1)?.setIcon(R.drawable.ic_docs)
//        htab_tabs.getTabAt(2)?.setIcon(R.drawable.ic_leaderboard)
//        htab_tabs.getTabAt(3)?.setIcon(R.drawable.ic_announcement)
//        htab_tabs.getTabAt(4)?.setIcon(R.drawable.ic_menu)
//        htab_tabs.getTabAt(1)?.select()
//        htab_viewpager.offscreenPageLimit = 4
//    }
//
//    override fun onRefresh() {
//        viewModel.fetchCourse(viewModel.attemptId)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//        super.onRestoreInstanceState(savedInstanceState)
//        viewModel.updatehit(viewModel.attemptId)
//        viewModel.fetchCourse(viewModel.attemptId)
//        setupViewPager(viewModel.attemptId, viewModel.courseId)
//    }
}
