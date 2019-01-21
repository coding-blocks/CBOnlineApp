package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R

private const val ARG__ATTEMPT_ID = "attempt_id"
private const val ARG__RUN_ID = "run_id"


class DoubtsFragment : Fragment() {


    private val attemptId: String by lazy {
        arguments?.getString(ARG__ATTEMPT_ID) ?: ""
    }
    private val runId: String by lazy {
        arguments?.getString(ARG__ATTEMPT_ID) ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?):
            View? = inflater.inflate(R.layout.fragment_doubts, container, false).apply {
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
