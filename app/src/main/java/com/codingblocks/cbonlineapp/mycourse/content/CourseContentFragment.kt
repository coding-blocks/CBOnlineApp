package com.codingblocks.cbonlineapp.mycourse.content

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
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
import com.codingblocks.cbonlineapp.commons.DownloadStarter
import com.codingblocks.cbonlineapp.commons.SectionListClickListener
import com.codingblocks.cbonlineapp.database.ListObject
import com.codingblocks.cbonlineapp.database.models.SectionModel
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.util.CODE
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.DOCUMENT
import com.codingblocks.cbonlineapp.util.DownloadWorker
import com.codingblocks.cbonlineapp.util.LECTURE
import com.codingblocks.cbonlineapp.util.PROGRESS_ID
import com.codingblocks.cbonlineapp.util.ProgressWorker
import com.codingblocks.cbonlineapp.util.QNA
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.SectionDownloadService
import com.codingblocks.cbonlineapp.util.VIDEO
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.cbonlineapp.util.extensions.applyDim
import com.codingblocks.cbonlineapp.util.extensions.clearDim
import com.codingblocks.cbonlineapp.util.extensions.getPrefs
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
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
    private var areLecturesLoaded: Boolean = false
    var popupWindowDogs: PopupWindow? = null
    val mLayoutManager by lazy { LinearLayoutManager(requireContext()) }
    private var filters: MutableLiveData<String> = MutableLiveData("")
    private var complete: MutableLiveData<String> = MutableLiveData("")

    var sectionitem = ArrayList<SectionModel>()

    private val sectionListAdapter = SectionListAdapter(sectionitem)

    private val viewModel by sharedViewModel<MyCourseViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        arguments?.let {
            attemptId = it.getString(RUN_ATTEMPT_ID) ?: ""
        }
        sectionItemsAdapter.starter = this
        viewModel.expired.observer(viewLifecycleOwner) {
            sectionItemsAdapter.setExpiry(it)
        }

        return inflater.inflate(R.layout.fragment_course_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

        completeSwitch.setOnClickListener {
            complete.value = (if (completeSwitch.isChecked) "UNDONE" else "")
        }

        typeChipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.webinarChip -> filters.value = (VIDEO)
                R.id.lectureChip -> filters.value = (LECTURE)
                R.id.quizChip -> filters.value = (QNA)
                R.id.codeChip -> filters.value = (CODE)
                R.id.documentChip -> filters.value = (DOCUMENT)
                View.NO_ID -> filters.value = ("")
            }
        }

        popupWindowDogs = popUpWindowSection()
        activity?.fab?.setOnClickListener {
            it as ExtendedFloatingActionButton
            if (it.isExtended) {
                it.shrink()
                popupWindowDogs?.showAsDropDown(it, 0, 0, Gravity.BOTTOM)
                view.applyDim(0.5F)
            } else {
                it.extend()
            }
        }

        popupWindowDogs?.setOnDismissListener {
            activity?.fab?.extend()
            view.clearDim()
        }
        swiperefresh.setOnRefreshListener {
            (activity as SwipeRefreshLayout.OnRefreshListener).onRefresh()
        }
        rvExpendableView.layoutManager = mLayoutManager
        rvExpendableView.adapter = sectionItemsAdapter

        filters.observer(this) {
            getContent(type = it)
        }
        complete.observer(this) {
            getContent(done = it)
        }

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

        val sectionListClickListener: SectionListClickListener = object : SectionListClickListener {
            override fun onClick(pos: Int) {
                popupWindowDogs?.dismiss()
                // Todo - Improvise the scroll
                mLayoutManager.scrollToPosition(pos - 10)
                smoothScroller.targetPosition = pos
                mLayoutManager.startSmoothScroll(smoothScroller)
            }
        }
        sectionListAdapter.onSectionListClick = sectionListClickListener
    }

    private fun getContent(
        done: String = complete.value ?: "",
        type: String = filters.value ?: ""
    ) {

        viewModel.getAllContent().observer(this) { SectionContent ->
            sectionitem.clear()
            val consolidatedList = ArrayList<ListObject>()
            SectionContent.forEach { sectionContent ->
                var duration: Long = 0
                var sectionComplete = 0
                sectionContent.contents.forEach { content ->
                    content.premium = sectionContent.section.premium
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
                    pos = consolidatedList.size
                })
                sectionitem.add(sectionContent.section)
                sectionListAdapter.notifyDataSetChanged()
                if (done.isEmpty() && type.isEmpty()) {
                    consolidatedList.addAll(sectionContent.contents.sortedBy { it.order })
                } else if (type.isEmpty()) {
                    consolidatedList.addAll(sectionContent.contents
                        .filter { it.progress == done }
                        .sortedBy { it.order })
                } else if (done.isEmpty()) {
                    consolidatedList.addAll(sectionContent.contents
                        .filter { it.contentable == type }
                        .sortedBy { it.order })
                } else {
                    consolidatedList.addAll(sectionContent.contents
                        .filter { it.contentable == type }
                        .filter { it.progress == done }
                        .sortedBy { it.order })
                }
                sectionItemsAdapter.submitList(consolidatedList)
            }
            contentShimmer.isVisible = SectionContent.isEmpty()
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
//        Snackbar.make(contentRoot, "Progress Will Be Synced Once Your Device Get Online", Snackbar.LENGTH_SHORT)
//            .setAnchorView(bottom_navigation).show()

        WorkManager.getInstance()
            .enqueue(request)
    }

    override fun startDownload(
        videoId: String,
        contentId: String,
        title: String,
        attemptId: String,
        sectionId: String
    ) {
        val constraints = if (getPrefs()?.SP_WIFI == true)
            Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
        else
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val videoData = workDataOf(VIDEO_ID to videoId,
            "title" to title,
            SECTION_ID to sectionId,
            RUN_ATTEMPT_ID to attemptId,
            CONTENT_ID to contentId)

        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<DownloadWorker>()
                .setConstraints(constraints)
                .setInputData(videoData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance()
            .enqueue(request)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && !areLecturesLoaded) {
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

    private fun popUpWindowSection(): PopupWindow {

        val popupWindow = PopupWindow(requireContext())
        val listViewDogs = RecyclerView(requireContext())
        listViewDogs.adapter = sectionListAdapter
        listViewDogs.layoutManager = LinearLayoutManager(requireContext())
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(resources.getDrawable(R.drawable.background_custom_radio_buttons_unselected_state, null))
        popupWindow.isOutsideTouchable = true
        popupWindow.height = 1000
        popupWindow.contentView = listViewDogs

        return popupWindow
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
