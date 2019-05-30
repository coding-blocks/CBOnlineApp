package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.LeaderboardListAdapter
import com.codingblocks.cbonlineapp.commons.LeaderboardDiffCallback
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.viewmodels.LeaderboardViewModel
import kotlinx.android.synthetic.main.fragment_leaderboard.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LeaderboardFragment : Fragment() {

    lateinit var runId: String
    private val leaderboardAdapter = LeaderboardListAdapter(LeaderboardDiffCallback())
    private val leaderboardViewModel: LeaderboardViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            runId = it.getString(RUN_ATTEMPT_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_leaderboard, container, false).apply {
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mLayoutManager = LinearLayoutManager(context)
        leaderboardList.layoutManager = mLayoutManager
        leaderboardList.adapter = leaderboardAdapter
        leaderboardViewModel.getLeaderboard(runId)
        leaderboardViewModel.leaderboard.observe(this, Observer {
            if (it != null) {
                leaderboardProgressBar.visibility = View.GONE
                leaderboardList.visibility = View.VISIBLE
                emptyLeaderboard.visibility = View.GONE
                leaderboardAdapter.submitList(it)
            } else {
                leaderboardProgressBar.visibility = View.GONE
                leaderboardList.visibility = View.GONE
                emptyLeaderboard.visibility = View.VISIBLE
                // hiding leader board header in case of no leader board available
                header_view_leaderboard.visibility = View.GONE
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            LeaderboardFragment().apply {
                arguments = Bundle().apply {
                    putString(RUN_ATTEMPT_ID, param1)
                }
            }
    }
}
