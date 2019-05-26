package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.LeaderboardListAdapter
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Leaderboard
import kotlinx.android.synthetic.main.fragment_leaderboard.*

class LeaderboardFragment : Fragment() {

    lateinit var runId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            runId = it.getString(RUN_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_leaderboard, container, false).apply {

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var leaderboardArray = ArrayList<Leaderboard>()
        val adapter = LeaderboardListAdapter(context!!, leaderboardArray)
        leaderboardList.adapter = adapter
        Clients.api.leaderboardById(runId).enqueue(retrofitCallback { throwable, response ->
            Log.v("RESPONSE", response.toString())
            adapter.notifyDataSetChanged()
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            LeaderboardFragment().apply {
                arguments = Bundle().apply {
                    putString(RUN_ID, param1)
                }
            }
    }

}
