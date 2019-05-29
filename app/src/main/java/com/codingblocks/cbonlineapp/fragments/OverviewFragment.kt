package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.LeaderboardListAdapter
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.onlineapi.models.Leaderboard
import com.google.firebase.analytics.FirebaseAnalytics
import org.jetbrains.anko.AnkoLogger

private const val ARG_ATTEMPT_ID = "attempt_id"
private const val ARG__RUN_ID = "run_id"

class OverviewFragment : Fragment(), AnkoLogger {

    lateinit var attemptId: String
    lateinit var runId: String
    private lateinit var leaderboardListAdapter: LeaderboardListAdapter
    private var list = ArrayList<Leaderboard>()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? = inflater.inflate(R.layout.fragment_overview, container, false).apply {
        leaderboardListAdapter = LeaderboardListAdapter(context, list)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        arguments?.let {
            attemptId = it.getString(ARG_ATTEMPT_ID)!!
            runId = it.getString(ARG__RUN_ID)!!
        }

//        val header = layoutInflater.inflate(R.layout.leaderboard_header, leaderboardLv, false) as ViewGroup
//        leaderboardLv.addHeaderView(header, null, false)
//        leaderboardLv.adapter = leaderboardListAdapter
//
//        Clients.api.leaderboardById(runId).enqueue(retrofitCallback { throwable, response ->
//            response?.body().let {
//                if (it != null) {
//                    leaderboardLv.visibility = View.VISIBLE
//                    leaderBoardTv.visibility = View.VISIBLE
//                    view1.visibility = View.VISIBLE
//                    list.addAll(it as ArrayList<Leaderboard>)
//                    leaderboardListAdapter.notifyDataSetChanged()
//                }
//            }
//        })
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, crUid: String) =
                OverviewFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_ATTEMPT_ID, param1)
                        putString(ARG__RUN_ID, crUid)
                    }
                }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (view != null) {
                val params = Bundle()
                params.putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs()?.SP_ONEAUTH_ID)
                params.putString(FirebaseAnalytics.Param.ITEM_NAME, "CourseOverview")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)
            }
        }
    }
}
