package com.codingblocks.cbonlineapp.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.onlineapi.Clients

private const val ARG__ATTEMPT_ID = "attempt_id"
private const val ARG__COURSE_ID = "course_id"


class DoubtsFragment : Fragment() {


    private val attemptId: String by lazy {
        arguments?.getString(ARG__ATTEMPT_ID) ?: ""
    }
    private val courseId: String by lazy {
        arguments?.getString(ARG__COURSE_ID) ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?):
            View? = inflater.inflate(R.layout.fragment_doubts, container, false).apply {
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Clients.api.getDoubts(courseId).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {

            }
        })

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, crUid: String) =
                DoubtsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG__ATTEMPT_ID, param1)
                        putString(ARG__COURSE_ID, crUid)

                    }
                }
    }

}
