package com.codingblocks.cbonlineapp.admin.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.onlineapi.ErrorStatus
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.admin_overview_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AdminOverviewFragment : Fragment() {

    private val viewModel by viewModel<AdminOverviewViewModel>()
    private val leaderBoardListAdapter = AdminLeaderBoardListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.prefs = PreferenceHelper.getPrefs(requireContext())
        return inflater.inflate(R.layout.admin_overview_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.fetchDoubtStats()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Picasso.get().load(viewModel.prefs.SP_USER_IMAGE).placeholder(R.drawable.defaultavatar).into(userIv)
        usernameTv.text = viewModel.prefs.SP_USER_NAME
        userIdTv.text = "Account Id :${viewModel.prefs.SP_ONEAUTH_ID}"

        adminLeaderboardRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = leaderBoardListAdapter
        }

        viewModel.doubtStats.observer(viewLifecycleOwner) {
            doubtResolvedTv.text = it.totalResolvedDoubts.toString()
            userRatingTv.text = it.avgRating.toString()
            cbRatingTv.text = it.cbRating.toString()
            responseTv.text = it.avgFirstResponse.toString()
            badReviewTv.text = it.totalBadReviews.toString()
            resolutionTv.text = it.avgResolution.toString()
        }

        viewModel.listLeaderboard.observer(viewLifecycleOwner) {
            leaderBoardListAdapter.submitList(it)
        }

        viewModel.errorLiveData.observer(viewLifecycleOwner) {
            when (it) {
                ErrorStatus.EMPTY_RESPONSE -> {
                }
                ErrorStatus.NO_CONNECTION -> {
                }
                ErrorStatus.UNAUTHORIZED -> {
                    Components.showConfirmation(requireContext(), UNAUTHORIZED) {
                        requireActivity().finish()
                    }
                }
                ErrorStatus.TIMEOUT -> {
                }
            }
        }

        viewModel.nextOffSet.observer(viewLifecycleOwner) { offSet ->
            nextBtn.isEnabled = offSet != 0
            nextBtn.setOnClickListener {
                viewModel.fetchLeaderBoard(offSet)
            }
        }

        viewModel.prevOffSet.observer(viewLifecycleOwner) { offSet ->
            prevBtn.isEnabled = offSet != 0
            prevBtn.setOnClickListener {
                viewModel.fetchLeaderBoard(offSet)
            }
        }
    }
}
