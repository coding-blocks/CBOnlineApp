package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.SectionItemsAdapter
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.services.DownloadService
import com.codingblocks.cbonlineapp.services.SectionDownloadService
import com.codingblocks.cbonlineapp.util.*
import com.codingblocks.cbonlineapp.viewmodels.MyCourseViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.fragment_course_content.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startService
import org.jetbrains.anko.yesButton
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class CourseContentFragment : Fragment(), AnkoLogger,
    DownloadStarter {


    override fun startSectionDownlod(sectionId: String) {
        startService<SectionDownloadService>(SECTION_ID to sectionId)
    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var attemptId: String
    private val viewModel by sharedViewModel<MyCourseViewModel>()

    override fun startDownload(
        videoId: String,
        sectionId: String,
        lectureContentId: String,
        title: String,
        attemptId: String,
        contentId: String
    ) {
        startService<DownloadService>(
            SECTION_ID to sectionId,
            VIDEO_ID to videoId,
            LECTURE_CONTENT_ID to lectureContentId,
            "title" to title,
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

        swiperefresh.setOnRefreshListener {
            (activity as SwipeRefreshLayout.OnRefreshListener).onRefresh()

        }
        val sectionItemsAdapter = SectionItemsAdapter()
        sectionItemsAdapter.starter = this
        rvExpendableView.layoutManager = LinearLayoutManager(context)
        rvExpendableView.adapter = sectionItemsAdapter


        viewModel.getAllContent().observe(this, Observer(sectionItemsAdapter::submitList))

        /**
         * Register a new observer to listen for data changes.
         * Otherwise list scrolls to bottom
         **/

        sectionItemsAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    rvExpendableView.scrollToPosition(0)
                }
            }
        })
        viewModel.progress.observer(viewLifecycleOwner) {
            swiperefresh.isRefreshing = it
        }

        viewModel.revoked.observer(viewLifecycleOwner) { value ->
            if (value) {
                alert {
                    title = "Error Fetching Course"
                    message = """
                        There was an error downloading course contents.
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

    override fun updateProgress(contentId: String) {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val progressData = workDataOf(
            CONTENT_ID to contentId,
            RUN_ATTEMPT_ID to attemptId
        )

        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<ProgressWorker>()
                .setConstraints(constraints)
                .setInputData(progressData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance()
            .enqueue(request)
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (view != null) {
                val params = Bundle()
                params.putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs()?.SP_ONEAUTH_ID)
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "CourseContent")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)
            }
        }
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
