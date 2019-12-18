package com.codingblocks.cbonlineapp.dashboard.doubts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.changeViewState
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.showEmptyView
import com.codingblocks.onlineapi.ErrorStatus
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard_doubts.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class DashboardDoubtsFragment : Fragment() {

    private val viewModel by viewModel<DashboardDoubtsViewModel>()

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

        dashboardDoubtRv.apply {
            setRv(requireContext(), true)
//            adapter = doubtsAdapter
        }

        viewModel.listDoubtsResponse.observer(viewLifecycleOwner) {

            //                doubtsAdapter.submitList(it)
            changeViewState(dashboardDoubtRv, internetll, emptyLl, dashboardDoubtShimmer, it.isEmpty())
        }
        viewModel.errorLiveData.observer(viewLifecycleOwner) {
            when (it) {
                ErrorStatus.NO_CONNECTION -> {
                    showEmptyView(internetll, emptyLl, dashboardDoubtShimmer)
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
                            //                            fetchDoubts(adminTabLayout.selectedTabPosition)
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
    }

}
