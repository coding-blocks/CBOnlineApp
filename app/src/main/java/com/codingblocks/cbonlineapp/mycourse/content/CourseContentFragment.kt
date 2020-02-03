package com.codingblocks.cbonlineapp.mycourse.content

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.codingblocks.cbonlineapp.PdfActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.commons.DownloadStarter
import com.codingblocks.cbonlineapp.commons.SectionListClickListener
import com.codingblocks.cbonlineapp.database.ListObject
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.database.models.SectionModel
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.mycourse.player.VideoPlayerActivity
import com.codingblocks.cbonlineapp.mycourse.quiz.QuizActivity
import com.codingblocks.cbonlineapp.util.CODE
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.DOCUMENT
import com.codingblocks.cbonlineapp.util.DownloadWorker
import com.codingblocks.cbonlineapp.util.LECTURE
import com.codingblocks.cbonlineapp.util.PreferenceHelper.Companion.getPrefs
import com.codingblocks.cbonlineapp.util.QNA
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.SectionDownloadService
import com.codingblocks.cbonlineapp.util.VIDEO
import com.codingblocks.cbonlineapp.util.VIDEO_ID
import com.codingblocks.cbonlineapp.util.extensions.applyDim
import com.codingblocks.cbonlineapp.util.extensions.clearDim
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.showDialog
import kotlinx.android.synthetic.main.activity_my_course.*
import kotlinx.android.synthetic.main.fragment_course_content.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startService
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit

class CourseContentFragment : BaseCBFragment(), AnkoLogger, DownloadStarter {

    var popupWindowDogs: PopupWindow? = null
    var sectionitem = ArrayList<SectionModel>()
    var type: String = ""

    private val sectionItemsAdapter = SectionItemsAdapter()
    private val sectionListAdapter = SectionListAdapter(sectionitem)
    private val viewModel by sharedViewModel<MyCourseViewModel>()
    private val mLayoutManager by lazy { LinearLayoutManager(requireContext()) }

    val smoothScroller by lazy {
        object : LinearSmoothScroller(context) {

            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
    }

    private val sectionListClickListener: SectionListClickListener =
        object : SectionListClickListener {
            override fun onClick(pos: Int, adapterPosition: Int) {
                popupWindowDogs?.dismiss()
                val position = adapterPosition - 2
                if (position > 0) {
                    mLayoutManager.scrollToPosition(sectionitem[adapterPosition - 2].pos)
                    smoothScroller.targetPosition = pos
                    mLayoutManager.startSmoothScroll(smoothScroller)
                } else {
                    mLayoutManager.scrollToPosition(sectionitem[adapterPosition + 2].pos)
                    smoothScroller.targetPosition = pos
                    mLayoutManager.startSmoothScroll(smoothScroller)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = inflater.inflate(R.layout.fragment_course_content, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        typeChipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.webinarChip -> viewModel.filters.value = VIDEO.also {
                    type = VIDEO
                }
                R.id.lectureChip -> viewModel.filters.value = LECTURE.also {
                    type = LECTURE
                }
                R.id.quizChip -> viewModel.filters.value = QNA.also {
                    type = QNA
                }
                R.id.codeChip -> viewModel.filters.value = CODE.also {
                    type = CODE
                }
                R.id.documentChip -> viewModel.filters.value = DOCUMENT.also {
                    type = DOCUMENT
                }
                View.NO_ID -> viewModel.filters.value = "".also {
                    type = ""
                }
            }
        }

        popupWindowDogs = popUpWindowSection()

        activity?.fab?.setOnClickListener {
            // Todo Check for different screens
            popupWindowDogs?.showAsDropDown(it, -280, -50, Gravity.BOTTOM)
            view.applyDim(0.5F)
        }

        popupWindowDogs?.setOnDismissListener {
            view.clearDim()
        }

//        swiperefresh.setOnRefreshListener {
//            (activity as SwipeRefreshLayout.OnRefreshListener).onRefresh()
//        }
        rvExpendableView.apply {
            adapter = sectionItemsAdapter
            layoutManager = mLayoutManager
        }
        attachObservers()
        sectionListAdapter.onSectionListClick = sectionListClickListener
    }

    private fun attachObservers() {

        viewModel.progress.observer(viewLifecycleOwner) {
            //            swiperefresh.isRefreshing = it
        }

//        viewModel.revoked.observer(viewLifecycleOwner) { value ->
//            if (value) {
//                alert {
//                    title = "Error Fetching Course"
//                    message = """
//                        There was an error downloading courseRun contents.
//                        Please contact support@codingblocks.com
//                        """.trimIndent()
//                    yesButton {
//                        it.dismiss()
//                        activity?.finish()
//                    }
//                    isCancelable = false
//                }.show()
//            }
//        }

        viewModel.content.observer(viewLifecycleOwner) { SectionContent ->
            sectionitem.clear()
            val consolidatedList = ArrayList<ListObject>()
            SectionContent.forEach { sectionContent ->
                var duration: Long = 0
                var sectionComplete = 0
                val list = if (viewModel.complete.value!!.isEmpty() && type.isEmpty()) {
                    sectionContent.contents.sortedBy { it.order }
                } else if (type.isEmpty()) {
                    sectionContent.contents
                        .filter { it.progress == viewModel.complete.value }
                        .sortedBy { it.order }
                } else if (viewModel.complete.value!!.isEmpty()) {
                    sectionContent.contents
                        .filter { it.contentable == type }
                        .sortedBy { it.order }
                } else {
                    sectionContent.contents
                        .filter { it.contentable == type }
                        .filter { it.progress == viewModel.complete.value!! }
                        .sortedBy { it.order }
                }
                list.forEach { content ->
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
                val item = sectionContent.section.apply {
                    totalContent = list.size
                    totalTime = duration
                    completedContent = sectionComplete
                    pos = consolidatedList.size
                }
                if (item.totalContent > 0 || type.isEmpty()) {
                    consolidatedList.add(item)
                    sectionitem.add(sectionContent.section)
                }
                consolidatedList.addAll(list)
                sectionListAdapter.notifyDataSetChanged()

                sectionItemsAdapter.submitList(consolidatedList)
            }
            contentShimmer.isVisible = SectionContent.isEmpty()
        }
    }

    override fun startDownload(
        videoId: String,
        contentId: String,
        title: String,
        attemptId: String,
        sectionId: String
    ) {
        val constraints = if (getPrefs(requireContext()).SP_WIFI)
            Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
        else
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val videoData = workDataOf(
            VIDEO_ID to videoId,
            "title" to title,
            SECTION_ID to sectionId,
            RUN_ATTEMPT_ID to attemptId,
            CONTENT_ID to contentId
        )

        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<DownloadWorker>()
                .setConstraints(constraints)
                .setInputData(videoData)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 20, TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance()
            .enqueue(request)
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
        popupWindow.setBackgroundDrawable(
            resources.getDrawable(
                R.drawable.background_custom_radio_buttons_unselected_state,
                null
            )
        )
        popupWindow.isOutsideTouchable = true
        popupWindow.height = 1000
        popupWindow.contentView = listViewDogs

        return popupWindow
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.fetchSections()
        sectionItemsAdapter.starter = this
        sectionItemsAdapter.onItemClick = {
            when (it) {
                is ContentModel -> with(it) {
                    when (contentable) {
                        DOCUMENT ->
                            if (contentDocument.documentUid.isNotEmpty()) {
                                viewModel.updateProgress(ccid)
                                startActivity(
                                    intentFor<PdfActivity>(
                                        "fileUrl" to contentDocument.documentPdfLink,
                                        "fileName" to contentDocument.documentName + ".pdf"
                                    )
                                )
                            } else
                                checkSection(premium)
                        LECTURE ->
                            if (contentLecture.lectureUid.isNotEmpty())
                                startActivity(
                                    intentFor<VideoPlayerActivity>(
                                        CONTENT_ID to ccid,
                                        SECTION_ID to sectionId
                                    )
                                )
                            else
                                checkSection(premium)
                        VIDEO ->
                            if (contentVideo.videoUid.isNotEmpty()) {
                                viewModel.updateProgress(ccid)
                                startActivity(
                                    intentFor<VideoPlayerActivity>(
                                        CONTENT_ID to ccid,
                                        SECTION_ID to sectionId
                                    )
                                )
                            } else
                                checkSection(premium)
                        QNA ->
                            if (contentQna.qnaUid.isNotEmpty()) {
                                viewModel.updateProgress(ccid)
                                startActivity(
                                    intentFor<QuizActivity>(
                                        CONTENT_ID to ccid,
                                        SECTION_ID to sectionId
                                    )
                                )
                            } else
                                checkSection(premium)
                        CODE ->
                            requireContext().showDialog(
                                "unavailable",
                                secondaryText = R.string.unavailable,
                                buttonText = R.string.ok,
                                cancelable = true
                            )
                    }
                }
            }
        }
    }

    private fun checkSection(premium: Boolean) {
        when {
            viewModel.runStartEnd.first < System.currentTimeMillis() -> {
                requireContext().showDialog(
                    "expired",
                    secondaryText = R.string.expired,
                    buttonText = R.string.buy_extension,
                    cancelable = true
                ) {
                    // Show Extension Dialog
                }
            }
            premium -> {
                requireContext().showDialog(
                    "purchase",
                    secondaryText = R.string.purchase,
                    buttonText = R.string.buy_now,
                    cancelable = true
                ) {
                    // add to cart
                }
            }
            viewModel.runStartEnd.second > System.currentTimeMillis() -> {
                requireContext().showDialog(
                    "Wait",
                    secondaryText = R.string.wait,
                    buttonText = R.string.ok,
                    cancelable = true
                )
            }

            else -> {
                requireContext().showDialog("revoked") {
                    // open mail
                }
            }
        }
    }
}
