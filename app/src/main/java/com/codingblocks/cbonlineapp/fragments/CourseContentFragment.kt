package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.SectionDetailsAdapter
import com.codingblocks.cbonlineapp.adapters.SectionItemsAdapter
import com.codingblocks.cbonlineapp.database.models.CourseSection
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.services.DownloadService
import com.codingblocks.cbonlineapp.services.SectionDownloadService
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.DownloadStarter
import com.codingblocks.cbonlineapp.util.LECTURE_CONTENT_ID
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.cbonlineapp.viewmodels.MyCourseViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.fragment_course_content.view.rvExpendableView
import kotlinx.android.synthetic.main.fragment_course_content.view.sectionProgressBar
import kotlinx.android.synthetic.main.fragment_course_content.view.swiperefresh
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.startService
import org.jetbrains.anko.yesButton
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CourseContentFragment : Fragment(), AnkoLogger,
    DownloadStarter {
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

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var attemptId: String
    private val viewModel by sharedViewModel<MyCourseViewModel>()
    private val sectionItemsAdapter = SectionItemsAdapter()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        arguments?.let {
            attemptId = it.getString(RUN_ATTEMPT_ID) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_course_content, container, false)
        view.swiperefresh.setOnRefreshListener {
            try {
                (activity as SwipeRefreshLayout.OnRefreshListener).onRefresh()
            } catch (cce: ClassCastException) {
            }
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        val sectionsList = ArrayList<CourseSection>()

        view.rvExpendableView.layoutManager = LinearLayoutManager(context)
        view.rvExpendableView.adapter = sectionItemsAdapter
        view.sectionProgressBar.show()

        viewModel.getAllContent().observer(viewLifecycleOwner) {
            it.forEach {
                info { it }
            }
        }

        viewModel.progress.observer(viewLifecycleOwner) {
            view.swiperefresh.isRefreshing = it
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
        return view
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
}
