package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.LeaderboardListAdapter
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import org.jetbrains.anko.doAsync
import com.codingblocks.cbonlineapp.commons.LeaderboardDiffCallback
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients
import kotlinx.android.synthetic.main.fragment_leaderboard.*
import kotlin.concurrent.thread

class LeaderboardFragment : Fragment() {

    lateinit var attemptId: String
    lateinit var runId: String
    private val leaderboardAdapter = LeaderboardListAdapter(LeaderboardDiffCallback())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            attemptId = it.getString(RUN_ATTEMPT_ID)
        }

        getRunId()

        // leaderboardListAdapter = LeaderboardListAdapter(data)
    }

    private fun getRunId() {
        val database = AppDatabase.getInstance(context!!)
        val courseDao = database.courseRunDao()
        thread {
            doAsync {
                val courseRun = courseDao.getRunById(attemptId)
                runId = courseRun.crUid
            }
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
        Clients.api.leaderboardById(runId).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                if (it != null) {
                    leaderboardProgressBar.visibility = View.GONE
                    leaderboardList.visibility = View.VISIBLE
                    emptyLeaderboard.visibility = View.GONE
                    leaderboardAdapter.submitList(it)
                } else {
                    leaderboardProgressBar.visibility = View.GONE
                    leaderboardList.visibility = View.GONE
                    emptyLeaderboard.visibility = View.VISIBLE
                }
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
