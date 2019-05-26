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
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Leaderboard
import kotlinx.android.synthetic.main.fragment_leaderboard.*
import org.jetbrains.anko.doAsync
import kotlin.concurrent.thread

class LeaderboardFragment : Fragment() {

    lateinit var attemptId: String
    lateinit var runId: String
    private lateinit var leaderboardListAdapter: LeaderboardListAdapter
    private var data = ArrayList<Leaderboard>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            attemptId = it.getString(RUN_ATTEMPT_ID)
        }

        getRunId()

        leaderboardListAdapter = LeaderboardListAdapter(data)
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

        leaderboardList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = leaderboardListAdapter
        }

        Clients.api.leaderboardById(runId).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                if (it != null) {
                    data.addAll(it as ArrayList<Leaderboard>)
                    leaderboardListAdapter.notifyDataSetChanged()
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
