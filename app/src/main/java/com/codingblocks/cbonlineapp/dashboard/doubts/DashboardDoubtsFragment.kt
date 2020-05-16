package com.codingblocks.cbonlineapp.dashboard.doubts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.admin.doubts.ChatClickListener
import com.codingblocks.cbonlineapp.auth.LoginActivity
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.commons.SheetAdapter
import com.codingblocks.cbonlineapp.commons.SheetItem
import com.codingblocks.cbonlineapp.dashboard.ChatActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardViewModel
import com.codingblocks.cbonlineapp.dashboard.DoubtCommentActivity
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.util.ALL
import com.codingblocks.cbonlineapp.util.CONVERSATION_ID
import com.codingblocks.cbonlineapp.util.DOUBT_ID
import com.codingblocks.cbonlineapp.util.LIVE
import com.codingblocks.cbonlineapp.util.REOPENED
import com.codingblocks.cbonlineapp.util.RESOLVED
import com.codingblocks.cbonlineapp.util.extensions.changeViewState
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.showDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.bottom_sheet_mycourses.view.*
import kotlinx.android.synthetic.main.fragment_dashboard_doubts.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.support.v4.intentFor
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DashboardDoubtsFragment : BaseCBFragment(), AnkoLogger {

    private val vm: DashboardViewModel by sharedViewModel()
    private val doubtListAdapter = DashboardDoubtListAdapter()
    private val dialog by lazy { BottomSheetDialog(requireContext()) }
    val list = arrayListOf<SheetItem>()
    val adapter = SheetAdapter(list)

    private val resolveClickListener: ResolveDoubtClickListener by lazy {
        object : ResolveDoubtClickListener {
            override fun onClick(doubt: DoubtsModel) {
                if (doubt.status == RESOLVED) {
                    requireContext().showDialog(RESOLVED, cancelable = true) {
                        vm.type.value = RESOLVED
                    }
                } else {
                    requireContext().showDialog(REOPENED, cancelable = true) {
                        vm.type.value = LIVE
                    }
                }
                vm.resolveDoubt(doubt)
            }
        }
    }

    private val commentsClickListener: DoubtCommentClickListener by lazy {
        object : DoubtCommentClickListener {
            override fun onClick(doubtId: String) {
                requireContext().startActivity(
                    requireContext().intentFor<DoubtCommentActivity>(
                        DOUBT_ID to doubtId
                    ).singleTop()
                )
            }
        }
    }

    private val chatClickListener: ChatClickListener by lazy {
        object : ChatClickListener {
            override fun onClick(convId: String, doubtId: String) {
                requireContext().startActivity(
                    requireContext().intentFor<ChatActivity>(
                        CONVERSATION_ID to convId
                    ).singleTop()
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_dashboard_doubts, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dashboardDoubtShimmer.startShimmer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBottomSheet()

        doubtEmptyBtn.setOnClickListener { requireActivity().dashboardBottomNav.selectedItemId = R.id.dashboard_home }
        liveDoubtBtn.setOnClickListener {
            vm.type.value = LIVE
        }

        resolvedDoubtBtn.setOnClickListener {
            vm.type.value = RESOLVED
        }

        allDoubtBtn.setOnClickListener {
            vm.type.value = ALL
        }

        filterTv.setOnClickListener {
            adapter.notifyDataSetChanged()
            dialog.show()
        }

        vm.type.observer(viewLifecycleOwner) {
            when (it) {
                LIVE -> {
                    liveDoubtBtn.isActivated = true
                    resolvedDoubtBtn.isActivated = false
                    allDoubtBtn.isActivated = false
                }
                RESOLVED -> {
                    liveDoubtBtn.isActivated = false
                    resolvedDoubtBtn.isActivated = true
                    allDoubtBtn.isActivated = false
                }
                ALL -> {
                    liveDoubtBtn.isActivated = false
                    resolvedDoubtBtn.isActivated = false
                    allDoubtBtn.isActivated = true
                }
                else -> {
                    liveDoubtBtn.isActivated = false
                    resolvedDoubtBtn.isActivated = false
                    allDoubtBtn.isActivated = false
                }
            }
        }

        if (vm.isLoggedIn == true) {
            vm.allRuns.observer(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    vm.attemptId.value = it.first().courseRun.runAttempt.attemptId
                    list.clear()
                    it.forEach {
                        list.add(
                            SheetItem(
                                it.courseRun.course.title,
                                image = it.courseRun.course.logo,
                                courseId = it.courseRun.runAttempt.attemptId
                            )
                        )
                    }
                }
            }
            vm.doubts.observe(viewLifecycleOwner, Observer {
                doubtListAdapter.submitList(it)
                changeViewState(
                    dashboardDoubtRv,
                    internetll,
                    emptyLl,
                    dashboardDoubtShimmer,
                    it.isEmpty()
                )
            })
        } else {
            dashboardDoubts.isVisible = false
            dashboardDoubtsLoggedOut.isVisible = true
        }

        dashboardDoubtRv.setRv(requireContext(), doubtListAdapter, true, "thick")
//        vm.errorLiveData.observer(viewLifecycleOwner) {
//            when (it) {
//                ErrorStatus.NO_CONNECTION -> {
// //                    dashboardDoubtRoot.showSnackbar(it, Snackbar.LENGTH_SHORT, dashboardBottomNav)
//                }
//                ErrorStatus.TIMEOUT -> {
//                    dashboardDoubtRoot.showSnackbar(
//                        it,
//                        Snackbar.LENGTH_INDEFINITE,
//                        dashboardBottomNav
//                    ) {
//                        vm.fetchDoubts()
//                    }
//                }
//                else -> {
//                    dashboardDoubtRoot.showSnackbar(it, Snackbar.LENGTH_SHORT, dashboardBottomNav)
//                    AppCrashlyticsWrapper.log(it)
//                }
//            }
//            if (doubtListAdapter.currentList.isEmpty())
//                showEmptyView(emptyView = emptyLl, shimmerView = dashboardDoubtShimmer)
//        }

//        viewModel.barMessage.observer(viewLifecycleOwner) {
//            dashboardDoubtRoot.showSnackbar(it, Snackbar.LENGTH_SHORT, dashboardBottomNav, false)
//        }

        doubtListAdapter.apply {
            onResolveClick = resolveClickListener
            onCommentClick = commentsClickListener
            onChatClick = chatClickListener
        }
        loginBtn.setOnClickListener {
            startActivity(intentFor<LoginActivity>())
            requireActivity().finish()
        }
    }

    private fun setUpBottomSheet() {
        val sheetDialog = layoutInflater.inflate(R.layout.bottom_sheet_mycourses, null)

        sheetDialog.run {
            sheetLv.adapter = adapter
            sheetLv.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                vm.attemptId.postValue(list[position].courseId)
                adapter.selectedItem = position
                dialog.dismiss()
            }
        }

        dialog.dismissWithAnimation = true
        dialog.setContentView(sheetDialog)
    }

    override fun onDestroyView() {
        doubtListAdapter.apply {
            onResolveClick = null
            onCommentClick = null
            onChatClick = null
        }
        super.onDestroyView()
    }
}
