package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.LeaderboardListAdapter
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Leaderboard
import kotlinx.android.synthetic.main.fragment_about_quiz.*
import kotlinx.android.synthetic.main.fragment_overview.*
import org.jetbrains.anko.AnkoLogger

private const val ARG__ATTEMPT_ID = "attempt_id"
private const val ARG__RUN_ID = "run_id"


class OverviewFragment : Fragment(), AnkoLogger {


    lateinit var attemptId: String
    lateinit var runId: String
    private lateinit var leaderboardListAdapter: LeaderboardListAdapter
    private var list = ArrayList<Leaderboard>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?):
            View? = inflater.inflate(R.layout.fragment_overview, container, false).apply {
        leaderboardListAdapter = LeaderboardListAdapter(context, list)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            attemptId = it.getString(ARG__ATTEMPT_ID)!!
            runId = it.getString(ARG__RUN_ID)!!

        }

        val header = layoutInflater.inflate(R.layout.leaderboard_header, quizAttemptLv, false) as ViewGroup
        leaderboardLv.addHeaderView(header, null, false)
        leaderboardLv.adapter = leaderboardListAdapter

        Clients.api.leaderboardById(runId).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                list.addAll(it as ArrayList<Leaderboard>)
                leaderboardListAdapter.notifyDataSetChanged()
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, crUid: String) =
                OverviewFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG__ATTEMPT_ID, param1)
                        putString(ARG__RUN_ID, crUid)

                    }
                }
    }


}
