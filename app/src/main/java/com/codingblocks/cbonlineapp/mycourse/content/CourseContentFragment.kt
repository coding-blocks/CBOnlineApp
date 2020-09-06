package com.codingblocks.cbonlineapp.mycourse.content

import android.app.ActivityManager
import android.content.Context
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.commons.DownloadStarter
import com.codingblocks.cbonlineapp.commons.SectionListClickListener
import com.codingblocks.cbonlineapp.course.checkout.CheckoutActivity
import com.codingblocks.cbonlineapp.database.ListObject
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.database.models.SectionModel
import com.codingblocks.cbonlineapp.mycourse.MyCourseActivity
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.mycourse.content.codechallenge.CodeChallengeActivity
import com.codingblocks.cbonlineapp.mycourse.content.document.PdfActivity
import com.codingblocks.cbonlineapp.mycourse.content.player.VideoPlayerActivity.Companion.createVideoPlayerActivityIntent
import com.codingblocks.cbonlineapp.mycourse.content.quiz.QuizActivity
import com.codingblocks.cbonlineapp.util.CODE
import com.codingblocks.cbonlineapp.util.CONTENT_ID
import com.codingblocks.cbonlineapp.util.DOCUMENT
import com.codingblocks.cbonlineapp.util.LECTURE
import com.codingblocks.cbonlineapp.util.QNA
import com.codingblocks.cbonlineapp.util.SECTION_ID
import com.codingblocks.cbonlineapp.util.VIDEO
import com.codingblocks.cbonlineapp.util.extensions.applyDim
import com.codingblocks.cbonlineapp.util.extensions.clearDim
import com.codingblocks.cbonlineapp.util.extensions.getLoadingDialog
import com.codingblocks.cbonlineapp.util.extensions.showDialog
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.codingblocks.cbonlineapp.workers.DownloadService
import com.codingblocks.cbonlineapp.workers.SectionDownloadService
import kotlinx.android.synthetic.main.activity_my_course.*
import kotlinx.android.synthetic.main.fragment_course_content.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/*
 *   Payment issues
 *   multi select quizzes
 *   pdf not issue
 *   challenge can't be done
 *   webinar - make,
 *   make more category
 *   expired,upgrade and pause-unpause
 *   only course, free course
 */

const val SECTION_DOWNLOAD = "sectionDownload"

class CourseContentFragment : BaseCBFragment(), AnkoLogger, DownloadStarter {

    var popupWindowDogs: PopupWindow? = null
    var sectionitem = ArrayList<SectionModel>()
    var type: String = ""
    private val dialog by lazy {
        requireContext().getLoadingDialog()
    }
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_course_content, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchSections()
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

        swiperefresh.setOnRefreshListener {
            (activity as SwipeRefreshLayout.OnRefreshListener).onRefresh()
        }
        rvExpendableView.apply {
            adapter = sectionItemsAdapter
            layoutManager = mLayoutManager
        }
        attachObservers()
        sectionListAdapter.onSectionListClick = sectionListClickListener
    }

    private fun attachObservers() {

        viewModel.progress.observer(thisLifecycleOwner) {
            swiperefresh.isRefreshing = it
        }

//        viewModel.computedData.observe(thisLifecycleOwner, Observer {
//                    info { it }
//        })

        viewModel.content.observer(thisLifecycleOwner) { sectionWithContentList ->
            sectionitem.clear()
            val consolidatedList = ArrayList<ListObject>()
            sectionWithContentList.forEach { sectionContent ->
                var duration: Long = 0
                var sectionComplete = 0
                var isDownloadEnabled = false
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
                    if (content.contentable == "lecture" && content.contentLecture.lectureUid.isNotEmpty() && !content.contentLecture.isDownloaded) {
                        isDownloadEnabled = true
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
                    isSectionDownloadEnabled = isDownloadEnabled
                }
                if (item.totalContent > 0 || type.isEmpty()) {
                    consolidatedList.add(item)
                    sectionitem.add(sectionContent.section)
                }
                consolidatedList.addAll(list)
                sectionListAdapter.notifyDataSetChanged()

                sectionItemsAdapter.submitList(consolidatedList)

                rvExpendableView.viewTreeObserver.addOnGlobalLayoutListener {
                    if (sectionListAdapter.itemCount == 0) {
                        (activity as MyCourseActivity).hideFab()
                        rvExpendableView.visibility = View.GONE
                        textview4_20.visibility = View.VISIBLE
                    } else {
                        if ((activity as MyCourseActivity).myCourseTabs.selectedTabPosition == 1)
                        (activity as MyCourseActivity).showFab()
                        rvExpendableView.visibility = View.VISIBLE
                        textview4_20.visibility = View.GONE
                    }
                }
            }
            contentShimmer.isVisible = sectionWithContentList.isEmpty()
        }
    }

    override fun startDownload(
        videoId: String,
        contentId: String,
        title: String,
        attemptId: String,
        sectionId: String
    ) {

        DownloadService.startService(requireContext(), sectionId, attemptId, videoId, contentId, title)
    }

    override fun startSectionDownlod(sectionId: String) {
        if (isMyServiceRunning(SectionDownloadService::class.java)) {
            toast("One Section download is in progress")
        } else {
            SectionDownloadService.startService(requireContext(), sectionId, viewModel.attemptId!!)
        }
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
                                        CONTENT_ID to ccid,
                                        SECTION_ID to sectionId
                                    )
                                )
                            } else
                                checkSection(premium)
                        LECTURE ->
                            if (contentLecture.lectureUid.isNotEmpty())
                                startActivity(
                                    createVideoPlayerActivityIntent(requireContext(), ccid, sectionId)
                                )
                            else
                                checkSection(premium)
                        VIDEO ->
                            if (contentVideo.videoUid.isNotEmpty()) {
                                startActivity(
                                    createVideoPlayerActivityIntent(requireContext(), ccid, sectionId)
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
                            if (contentCode.codeUid.isNotEmpty()) {
                                startActivity(
                                    intentFor<CodeChallengeActivity>(
                                        CONTENT_ID to ccid,
                                        SECTION_ID to sectionId
                                    )
                                )
                            } else
                                checkSection(premium)
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
                    primaryButtonText = R.string.buy_extension,
                    cancelable = true
                ) {
                    // Show Extension Dialog
                }
            }
            premium -> {
                requireContext().showDialog(
                    "purchase",
                    secondaryText = R.string.purchase,
                    primaryButtonText = R.string.buy_now,
                    cancelable = true
                ) {
                    if (it) {
                        dialog.show()
                        viewModel.addToCart().observer(thisLifecycleOwner) {
                            dialog.hide()
                            requireContext().startActivity<CheckoutActivity>()
                        }
                    }
                }
            }
            viewModel.runStartEnd.second > System.currentTimeMillis() -> {
                requireContext().showDialog(
                    "Wait",
                    secondaryText = R.string.wait,
                    primaryButtonText = R.string.ok,
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

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}
