package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
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
import com.codingblocks.cbonlineapp.adapters.SectionItemsAdapter
import com.codingblocks.cbonlineapp.database.ListObject
import com.codingblocks.cbonlineapp.database.models.SectionContentHolder
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.services.DownloadService
import com.codingblocks.cbonlineapp.services.SectionDownloadService
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.DownloadStarter
import com.codingblocks.cbonlineapp.util.PROGRESS_ID
import com.codingblocks.cbonlineapp.util.ProgressWorker
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.cbonlineapp.viewmodels.MyCourseViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_my_course.*
import kotlinx.android.synthetic.main.fragment_course_content.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startService
import org.jetbrains.anko.yesButton
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class CourseContentFragment : Fragment(), AnkoLogger, DownloadStarter {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var attemptId: String
    private val sectionItemsAdapter = SectionItemsAdapter()

    private val viewModel by sharedViewModel<MyCourseViewModel>()

    override fun startDownload(
        videoId: String,
        contentId: String,
        title: String,
        attemptId: String,
        sectionId: String
    ) {
        startService<DownloadService>(
            VIDEO_ID to videoId,
            "title" to title,
            SECTION_ID to sectionId,
            RUN_ATTEMPT_ID to attemptId,
            CONTENT_ID to contentId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        arguments?.let {
            attemptId = it.getString(RUN_ATTEMPT_ID) ?: ""
        }
        return inflater.inflate(R.layout.fragment_course_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val smoothScroller = object : LinearSmoothScroller(requireContext()) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        swiperefresh.setOnRefreshListener {
            (activity as SwipeRefreshLayout.OnRefreshListener).onRefresh()
        }
        sectionItemsAdapter.starter = this
        val layoutManager = LinearLayoutManager(context)
        rvExpendableView.layoutManager = layoutManager
        rvExpendableView.adapter = sectionItemsAdapter
        val consolidatedList = ArrayList<ListObject>()
        viewModel.getAllContent().observer(this) { SectionContent ->
            val response = SectionContentHolder.groupContentBySection(SectionContent)
            response.forEach { sectionContent ->
                var duration: Long = 0
                var sectionComplete = 0
                sectionContent.contents.forEach { content ->
                    if (content.progress == "DONE") {
                        sectionComplete++
                    }

                    if (content.contentable == "lecture")
                        duration += content.contentLecture.lectureDuration
                    else if (content.contentable == "video") {
                        duration += content.contentVideo.videoDuration
                    }
                    // Map SectionId to ContentModel
                    content.sectionId = sectionContent.section.csid
                }
                consolidatedList.add(sectionContent.section.apply {
                    totalContent = sectionContent.contents.size
                    totalTime = duration
                    completedContent = sectionComplete
                })
                val pos = consolidatedList.size
                consolidatedList.addAll(sectionContent.contents)

                sectionItemsAdapter.notifyDataSetChanged()
            }
            sectionItemsAdapter.submitList(consolidatedList)
        }

        /**
         * Register a new observer to listen for data changes.
         * Otherwise list scrolls to bottom
         **/

//        sectionItemsAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                if (positionStart == 0) {
//                    rvExpendableView.scrollToPosition(0)
//                }
//            }
//        })

        viewModel.progress.observer(viewLifecycleOwner) {
            swiperefresh.isRefreshing = it
        }

        viewModel.revoked.observer(viewLifecycleOwner) { value ->
            if (value) {
                alert {
                    title = "Error Fetching Course"
                    message = """
                        There was an error downloading courseRun contents.
                        Please contact support@codingblocks.com
                        """.trimIndent()
                    yesButton {
                        it.dismiss()
                        activity?.finish()
                    }
                    isCancelable = false
                }.show()
            }
        }
    }

    override fun updateProgress(contentId: String, progressId: String) {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val progressData: Data = if (progressId.isNotEmpty()) {
            workDataOf(CONTENT_ID to contentId, RUN_ATTEMPT_ID to attemptId, PROGRESS_ID to progressId)
        } else {
            workDataOf(CONTENT_ID to contentId, RUN_ATTEMPT_ID to attemptId)
        }

        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<ProgressWorker>()
                .setConstraints(constraints)
                .setInputData(progressData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()
        Snackbar.make(contentRoot, "Progress Will Be Synced Once Your Device Get Online", Snackbar.LENGTH_SHORT)
            .setAnchorView(bottom_navigation).show()

        WorkManager.getInstance()
            .enqueue(request)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (view != null) {
                val params = Bundle()
                params.putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs()?.SP_ONEAUTH_ID)
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "ContentModel")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)
            }
        }
    }

    override fun startSectionDownlod(sectionId: String) {
        startService<SectionDownloadService>(SECTION_ID to sectionId)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            CourseContentFragment().apply {
                arguments = Bundle().apply {
                    putString(RUN_ATTEMPT_ID, param1)
                }
            }
    }
}
