package com.codingblocks.cbonlineapp.mycourse.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.util.ARG_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.PreferenceHelper.Companion.getPrefs
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.util.extensions.getDateForTime
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.ProductExtensionsItem
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.fragment_overview.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.longToast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class OverviewFragment : Fragment(), AnkoLogger {

    private val viewModel by sharedViewModel<MyCourseViewModel>()

    lateinit var attemptId: String
    lateinit var runId: String
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var extensionsAdapter: ExtensionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_overview, container, false)

        extensionsAdapter = ExtensionsAdapter(ArrayList())
        view.extensionsRv.apply {
            isNestedScrollingEnabled = false
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            adapter = extensionsAdapter
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpObservers(view)

        buyBtn.setOnClickListener {
            extensionsAdapter.getSelected()?.id?.let { it1 ->
                Clients.api.buyExtension(it1).enqueue(retrofitCallback { throwable, response ->
                    response.let {
                    }
                    Components.openChrome(requireContext(), "https://dukaan.codingblocks.com/mycart")
                })
            } ?: run {
                longToast("Atleast Select One Before Proceding !!")
            }
        }

        resetBtn.setOnClickListener {
            confirmReset()
        }
//        resumeBtn.setOnClickListener {
//            viewModel.getResumeCourse().observeOnce {
//                if (it.isNotEmpty())
//                    with(it[0]) {
//                        when (content.contentable) {
//                            LECTURE -> {
//                                startActivity(intentFor<VideoPlayerActivity>(
//                                    VIDEO_ID to content.contentLecture.lectureId,
//                                    RUN_ATTEMPT_ID to content.attempt_id,
//                                    CONTENT_ID to content.ccid,
//                                    SECTION_ID to section.csid,
//                                    DOWNLOADED to content.contentLecture.isDownloaded
//                                ).singleTop()
//                                )
//                            }
//                            DOCUMENT -> {
//                                startActivity(intentFor<PdfActivity>(
//                                    FILE_URL to content.contentDocument.documentPdfLink,
//                                    FILE_NAME to content.contentDocument.documentName + ".pdf"
//                                ).singleTop())
//                            }
//                            VIDEO -> {
//                                startActivity(intentFor<VideoPlayerActivity>(
//                                    VIDEO_URL to content.contentVideo.videoUrl,
//                                    RUN_ATTEMPT_ID to content.attempt_id,
//                                    CONTENT_ID to content.ccid
//                                ).singleTop())
//                            }
//                            else -> return@with
//                        }
//                    }
//            }
//        }
    }

    private fun setUpObservers(view: View) {
        viewModel.getRun().observer(viewLifecycleOwner) { courseRun ->
            courseProgress.progress = courseRun.progress.toInt()
            contentCompletedTv.text = String.format("%d of %d", courseRun.completedContents, courseRun.totalContents)
            batchEndTv.text = String.format("Batch Ends %s", getDateForTime(courseRun.crRunEnd))
            if (courseRun.progress > 90.0) {
                completetionBtn.setImageResource(R.drawable.ic_status_white)
                requestBtn.apply {
                    isEnabled = true
                    setOnClickListener {
                        viewModel.requestApproval()
                    }
                }
            } else {
                completetionBtn.setImageResource(R.drawable.ic_circle_white)
                requestBtn.isEnabled = false
            }
            if (courseRun.crRunEnd.toLong() * 1000 < System.currentTimeMillis() || courseRun.crRunEnd.toLong() * 1000 - System.currentTimeMillis() <= 2592000000)
                viewModel.fetchExtensions(courseRun.productId).observer(viewLifecycleOwner) {
                    if (it.isNotEmpty()) {
                        view.extensionsCard.isVisible = true
                        extensionsAdapter.setData(it as ArrayList<ProductExtensionsItem>)
                    } else {
                        view.extensionsCard.isVisible = false
                    }
                }
        }

        extensionsAdapter.checkedPosition.observer(viewLifecycleOwner) {
            buyBtn.isEnabled = it != -1
        }

        viewModel.resetProgress.observer(viewLifecycleOwner) {
            requireActivity().finish()
        }

        viewModel.popMessage.observer(viewLifecycleOwner) { message ->
            Snackbar.make(view.rootView, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun confirmReset() {
        Components.showConfirmation(requireContext(), "reset") {
            if (it) {
                viewModel.resetProgress()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        arguments?.let {
            attemptId = it.getString(ARG_ATTEMPT_ID)!!
            runId = it.getString(RUN_ID)!!
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, crUid: String) =
            OverviewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ATTEMPT_ID, param1)
                    putString(RUN_ID, crUid)
                }
            }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (view != null) {
                val params = Bundle()
                params.putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs(requireContext()).SP_ONEAUTH_ID)
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "CourseOverview")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)
            }
        }
    }
}
