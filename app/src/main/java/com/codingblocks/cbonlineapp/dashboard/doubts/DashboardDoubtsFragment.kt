package com.codingblocks.cbonlineapp.dashboard.doubts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.util.ALL
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.LIVE
import com.codingblocks.cbonlineapp.util.RESOLVED
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.changeViewState
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.onlineapi.ErrorStatus
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard_doubts.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class DashboardDoubtsFragment : Fragment() {

    private val viewModel by viewModel<DashboardDoubtsViewModel>()
    private val doubtListAdapter = DashboardDoubtListAdapter()

    private val resolveClickListener: ResolveDoubtClickListener by lazy {
        object : ResolveDoubtClickListener {
            override fun onClick(doubt: DoubtsModel) {
                viewModel.resolveDoubt(doubt)
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
            viewModel.courseId.value = LIVE
        }

        resolvedDoubtBtn.setOnClickListener {
            viewModel.courseId.value = RESOLVED
        }

        allDoubtBtn.setOnClickListener {
            viewModel.courseId.value = ALL
        }

        viewModel.courseId.observer(viewLifecycleOwner) {
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

        dashboardDoubtRv.apply {
            setRv(requireContext(), true, "thick")
            adapter = doubtListAdapter
        }

        viewModel.listDoubtsResponse.observer(viewLifecycleOwner) {
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
                    Snackbar.make(dashboardDoubtRoot, it, Snackbar.LENGTH_INDEFINITE)
                        .setAnchorView(dashboardBottomNav)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                        .setAction("Retry") {
                            viewModel.fetchDoubts()
                        }
                        .show()
                }
            }
        }

        viewModel.barMessage.observer(viewLifecycleOwner) {
            Snackbar.make(dashboardDoubtRoot, it, Snackbar.LENGTH_SHORT)
                .setAnchorView(dashboardBottomNav)
                .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                .show()
        }

        doubtListAdapter.apply {
            onResolveClick = resolveClickListener
        }
    }

    override fun onDestroyView() {
        doubtListAdapter.apply {
            onResolveClick = null
        }
        super.onDestroyView()
    }

}
