package com.codingblocks.cbonlineapp.admin.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.CustomDialog
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.glide.loadImage
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.codingblocks.onlineapi.ErrorStatus
import kotlinx.android.synthetic.main.admin_overview_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminOverviewFragment : BaseCBFragment() {

    private val viewModel by viewModel<AdminOverviewViewModel>()
    private val leaderBoardListAdapter = AdminLeaderBoardListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.admin_overview_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.fetchDoubtStats()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userIv.loadImage(viewModel.prefs.SP_USER_IMAGE)
        usernameTv.text = viewModel.prefs.SP_USER_NAME
        userIdTv.text = "Account Id :${viewModel.prefs.SP_ONEAUTH_ID}"

        adminLeaderboardRv.setRv(requireContext(), leaderBoardListAdapter)

        viewModel.doubtStats.observer(thisLifecycleOwner) {
            doubtResolvedTv.text = it.totalResolvedDoubts.toString()
            userRatingTv.text = it.avgRating.toString()
            cbRatingTv.text = it.cbRating.toString()
            responseTv.text = it.avgFirstResponse.toString()
            badReviewTv.text = it.totalBadReviews.toString()
            resolutionTv.text = it.avgResolution.toString()
        }

        viewModel.listLeaderboard.observer(thisLifecycleOwner) {
            leaderBoardListAdapter.submitList(it)
        }

        viewModel.errorLiveData.observer(thisLifecycleOwner) {
            when (it) {
                ErrorStatus.UNAUTHORIZED -> {
                    CustomDialog.showConfirmation(requireContext(), UNAUTHORIZED) {
                        requireActivity().finish()
                    }
                }
                ErrorStatus.TIMEOUT -> {
//                    adminOverviewRoot.showSnackbar(it, Snackbar.LENGTH_INDEFINITE, bottomNavAdmin) {
//                        viewModel.fetchDoubtStats()
//                    }
                }
            }
        }

        viewModel.nextOffSet.observer(thisLifecycleOwner) { offSet ->
            nextBtn.isEnabled = offSet != -1
            nextBtn.setOnClickListener {
                viewModel.fetchLeaderBoard(offSet)
            }
        }

        viewModel.prevOffSet.observer(thisLifecycleOwner) { offSet ->
            prevBtn.isEnabled = offSet != -1
            prevBtn.setOnClickListener {
                viewModel.fetchLeaderBoard(offSet)
            }
        }
    }
}
