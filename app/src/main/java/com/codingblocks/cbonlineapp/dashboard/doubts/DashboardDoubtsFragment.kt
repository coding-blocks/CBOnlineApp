package com.codingblocks.cbonlineapp.dashboard.doubts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.admin.doubts.ChatClickListener
import com.codingblocks.cbonlineapp.commons.SheetAdapter
import com.codingblocks.cbonlineapp.commons.SheetItem
import com.codingblocks.cbonlineapp.dashboard.ChatActivity
import com.codingblocks.cbonlineapp.dashboard.DoubtCommentActivity
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.util.ALL
import com.codingblocks.cbonlineapp.util.CONVERSATION_ID
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.DOUBT_ID
import com.codingblocks.cbonlineapp.util.LIVE
import com.codingblocks.cbonlineapp.util.REOPENED
import com.codingblocks.cbonlineapp.util.RESOLVED
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.changeViewState
import com.codingblocks.cbonlineapp.util.extensions.observeOnce
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.showDialog
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.onlineapi.ErrorStatus
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.bottom_sheet_mycourses.view.*
import kotlinx.android.synthetic.main.fragment_dashboard_doubts.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.koin.androidx.viewmodel.ext.android.viewModel


class DashboardDoubtsFragment : Fragment() {

    private val viewModel by viewModel<DashboardDoubtsViewModel>()
    private val doubtListAdapter = DashboardDoubtListAdapter()
    private val dialog by lazy { BottomSheetDialog(requireContext()) }

    private val resolveClickListener: ResolveDoubtClickListener by lazy {
        object : ResolveDoubtClickListener {
            override fun onClick(doubt: DoubtsModel) {
                if (doubt.status == RESOLVED) {
                    requireContext().showDialog(RESOLVED) {
                        viewModel.type.value = RESOLVED
                    }
                } else {
                    requireContext().showDialog(REOPENED) {
                        viewModel.type.value = LIVE
                    }
                }
                viewModel.resolveDoubt(doubt)
            }
        }
    }

    private val commentsClickListener: DoubtCommentClickListener by lazy {
        object : DoubtCommentClickListener {
            override fun onClick(doubtId: String) {
                requireContext().startActivity(requireContext().intentFor<DoubtCommentActivity>(DOUBT_ID to doubtId).singleTop())
            }
        }
    }

    private val chatClickListener: ChatClickListener by lazy {
        object : ChatClickListener {
            override fun onClick(convId: String, doubtId: String) {
                requireContext().startActivity(requireContext().intentFor<ChatActivity>(CONVERSATION_ID to convId).singleTop())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dashboard_doubts, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.fetchDoubts()
        dashboardDoubtShimmer.startShimmer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        liveDoubtBtn.setOnClickListener {
            viewModel.type.value = LIVE
        }

        resolvedDoubtBtn.setOnClickListener {
            viewModel.type.value = RESOLVED
        }

        allDoubtBtn.setOnClickListener {
            viewModel.type.value = ALL
        }

        filterTv.setOnClickListener {
            setUpBottomSheet()
            dialog.show()
        }

        viewModel.type.observer(viewLifecycleOwner) {
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

        dashboardDoubtRv.setRv(requireContext(), doubtListAdapter, true, "thick")

        viewModel.doubts.observer(viewLifecycleOwner) {
            doubtListAdapter.submitList(it)
            changeViewState(dashboardDoubtRv, internetll, emptyLl, dashboardDoubtShimmer, it.isEmpty())
        }
        viewModel.errorLiveData.observer(viewLifecycleOwner) {
            when (it) {
                ErrorStatus.NO_CONNECTION -> {
//                    showEmptyView(internetll, emptyLl, dashboardDoubtShimmer)
                }
                ErrorStatus.UNAUTHORIZED -> {
                    Components.showConfirmation(requireContext(), UNAUTHORIZED) {
                        requireActivity().finish()
                    }
                }
                ErrorStatus.TIMEOUT -> {
                    dashboardDoubtRoot.showSnackbar(it, Snackbar.LENGTH_INDEFINITE, dashboardBottomNav) {
                        viewModel.fetchDoubts()
                    }

                }
            }
        }

        viewModel.barMessage.observer(viewLifecycleOwner) {
            dashboardDoubtRoot.showSnackbar(it, Snackbar.LENGTH_INDEFINITE, dashboardBottomNav, false)
        }

        doubtListAdapter.apply {
            onResolveClick = resolveClickListener
            onCommentClick = commentsClickListener
            onChatClick = chatClickListener

        }
    }

    private fun setUpBottomSheet() {
        //TODO( fix list overlapping)
        val sheetDialog = layoutInflater.inflate(R.layout.bottom_sheet_mycourses, null)
        val list = arrayListOf<SheetItem>()
        viewModel.getRunId().observeOnce {
            it.forEach { run ->
                list.add(SheetItem(run.crDescription, R.drawable.ic_course_logo))
            }
            sheetDialog.run {
                sheetLv.adapter = SheetAdapter(list)
                sheetLv.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                    viewModel.courseId.postValue("")
                    dialog.dismiss()
                }
            }
        }


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
