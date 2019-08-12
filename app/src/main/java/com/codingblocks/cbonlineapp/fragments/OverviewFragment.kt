package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.ExtensionsAdapter
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.viewmodels.MyCourseViewModel
import com.codingblocks.onlineapi.models.ProductExtensionsItem
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.fragment_overview.completetionBtn
import kotlinx.android.synthetic.main.fragment_overview.requestBtn
import kotlinx.android.synthetic.main.fragment_overview.view.extensionsCard
import kotlinx.android.synthetic.main.fragment_overview.view.extensionsRv
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

private const val ARG_ATTEMPT_ID = "attempt_id"
private const val ARG__RUN_ID = "run_id"

class OverviewFragment : Fragment(), AnkoLogger {

    private val viewModel by sharedViewModel<MyCourseViewModel>()

    lateinit var attemptId: String
    lateinit var runId: String
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var extensionsAdapter: ExtensionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_overview, container, false)

        extensionsAdapter = ExtensionsAdapter(ArrayList())
        view.extensionsRv.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            adapter = extensionsAdapter
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCourseProgress().observer(viewLifecycleOwner) { progress ->
            if (progress > 90.0) {
                completetionBtn.setImageResource(R.drawable.ic_status_white)
                requestBtn.apply {
                    background = resources.getDrawable(R.drawable.button_background)
                    isEnabled = true
                    setOnClickListener {
                        viewModel.requestApproval()
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

        viewModel.fetchExtensions().observer(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                view.extensionsCard.isVisible = true
                extensionsAdapter.setData(it as ArrayList<ProductExtensionsItem>)
            } else {
                view.extensionsCard.isVisible = false
            }
        }

        viewModel

        viewModel.popMessage.observer(viewLifecycleOwner) { message ->
            Snackbar.make(view.rootView, message, Snackbar.LENGTH_LONG).show()
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
