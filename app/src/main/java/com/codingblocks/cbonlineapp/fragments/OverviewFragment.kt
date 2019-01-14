package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.onlineapi.Clients
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

private const val ARG__ATTEMPT_ID = "attempt_id"
private const val ARG__RUN_ID = "run_id"


class OverviewFragment : Fragment(), AnkoLogger {


    lateinit var attemptId: String
    lateinit var runId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            attemptId = it.getString(ARG__ATTEMPT_ID)!!
            runId = it.getString(ARG__RUN_ID)!!

        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?):
            View? = inflater.inflate(R.layout.fragment_overview, container, false).apply {


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Clients.api.leaderboardById(runId).enqueue(retrofitCallback { throwable, response ->

            info { response?.body()?.size }
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
