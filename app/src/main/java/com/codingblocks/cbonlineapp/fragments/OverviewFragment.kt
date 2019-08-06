package com.codingblocks.cbonlineapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.extensions.withColor
import com.codingblocks.cbonlineapp.viewmodels.MyCourseViewModel
import com.codingblocks.onlineapi.Clients
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.fragment_overview.completetionBtn
import kotlinx.android.synthetic.main.fragment_overview.requestBtn
import kotlinx.android.synthetic.main.fragment_overview.view.overviewFragment
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

private const val ARG_ATTEMPT_ID = "attempt_id"
private const val ARG__RUN_ID = "run_id"

class OverviewFragment : Fragment(), AnkoLogger {

    private val viewModel by sharedViewModel<MyCourseViewModel>()

    lateinit var attemptId: String
    lateinit var runId: String
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_overview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCourseProgress().observer(viewLifecycleOwner) { progress ->
            if (progress > 90.0) {
                completetionBtn.setImageResource(R.drawable.ic_status_white)
                requestBtn.apply {
                    background = resources.getDrawable(R.drawable.button_background)
                    isEnabled = true
                    setOnClickListener {
                        Clients.api.requestApproval(viewModel.attemptId).enqueue(retrofitCallback { throwable, response ->
                            response.let {
                                if (it?.code() == 500) {
                                    Snackbar.make(overviewFragment, "Could not send the request", Snackbar.LENGTH_LONG).show()
                                } else if (it?.code() == 200) {
                                    Snackbar.make(overviewFragment, "Request Successful", Snackbar.LENGTH_LONG).show()
                                }
                            }
                            throwable.let {
                                Snackbar.make(view.rootView, it?.message.toString(), Snackbar.LENGTH_LONG).withColor(Color.WHITE, Color.BLACK).show()
                            }
                        })
                    }
                }
            } else {
                completetionBtn.setImageResource(R.drawable.ic_circle_white)
                requestBtn.apply {
                    background = resources.getDrawable(R.drawable.button_disable)
                    isEnabled = false
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        arguments?.let {
            attemptId = it.getString(ARG_ATTEMPT_ID)!!
            runId = it.getString(ARG__RUN_ID)!!
        }
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
