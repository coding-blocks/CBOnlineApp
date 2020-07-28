package com.codingblocks.cbonlineapp.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.livedata.observer
import kotlinx.android.synthetic.main.fragment_campaign_leaderboard.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LeaderBoardFragment : BaseCBFragment() {

    private val vm by sharedViewModel<CampaignViewModel>()
    private val leaderBoardListAdapter = LeaderBoardPagedListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_campaign_leaderboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        leaderboardRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = leaderBoardListAdapter
        }
        vm.getLeaderBoard().observer(thisLifecycleOwner) {
            leaderBoardListAdapter.submitList(it)
        }
    }
}
