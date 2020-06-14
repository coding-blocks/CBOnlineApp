package com.codingblocks.cbonlineapp.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.util.livedata.observer
import kotlinx.android.synthetic.main.fragment_campaign_winnigs.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WinningsFragment : BaseCBFragment() {

    private val vm by sharedViewModel<CampaignViewModel>()
    private val leaderBoardListAdapter = WinningsListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_campaign_winnigs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        winningsRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = leaderBoardListAdapter
        }
        vm.fetchWinnings()
        vm.myWinnings.observer(thisLifecycleOwner) {
            emptyView.isVisible = it.isEmpty()
            winningsRv.isVisible = it.isNotEmpty()
            leaderBoardListAdapter.submitList(it)
        }
    }
}
