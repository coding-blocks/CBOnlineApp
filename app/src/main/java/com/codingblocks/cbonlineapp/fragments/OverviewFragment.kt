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
import com.codingblocks.cbonlineapp.extensions.getDateForTime
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.ARG_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.viewmodels.MyCourseViewModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.ProductExtensionsItem
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.fragment_overview.view.*
import kotlinx.android.synthetic.main.fragment_overview.view.description
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.longToast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


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

        setUpObservers(view)

        buyBtn.setOnClickListener {
            extensionsAdapter.getSelected()?.id?.let { it1 ->
                Clients.api.buyExtension(it1).enqueue(retrofitCallback { throwable, response ->
                    response.let {
                    }
                    Components.openChrome(requireContext(), "https://dukaan.codingblocks.com/mycart")
                })
            } ?: run {
                longToast("Atleast Select One Before Proceding !!")
            }
        }

        resetBtn.setOnClickListener {
            confirmReset()
        }
    }

    private fun setUpObservers(view: View) {
        viewModel.getRun().observer(viewLifecycleOwner) { courseRun ->
            if (courseRun.progress > 90.0) {
                completetionBtn.setImageResource(R.drawable.ic_status_white)
                requestBtn.apply {
                    isEnabled = true
                    setOnClickListener {
                        viewModel.requestApproval()
                    }
                }
            } else {
                completetionBtn.setImageResource(R.drawable.ic_circle_white)
                requestBtn.isEnabled = false
            }
            if (courseRun.crRunEnd.toLong() * 1000 < System.currentTimeMillis() || courseRun.crRunEnd.toLong() * 1000 - System.currentTimeMillis() <= 2592000000)
                viewModel.fetchExtensions(courseRun.productId).observer(viewLifecycleOwner) {
                    if (it.isNotEmpty()) {
                        view.extensionsCard.isVisible = true
                        extensionsAdapter.setData(it as ArrayList<ProductExtensionsItem>)
                    } else {
                        view.extensionsCard.isVisible = false
                    }
                }
        }

        extensionsAdapter.checkedPosition.observer(viewLifecycleOwner) {
            buyBtn.isEnabled = it != -1
        }


        viewModel.resetProgress.observer(viewLifecycleOwner) {
            requireActivity().finish()
        }

        viewModel.popMessage.observer(viewLifecycleOwner) { message ->
            Snackbar.make(view.rootView, message, Snackbar.LENGTH_LONG).show()
        }

        viewModel.getRun().observer(this) {
            courseProgress.progress = it.progress.toInt()
            contentCompletedTv.text = String.format("%d of %d", it.completedContents, it.totalContents)
            batchEndTv.text = String.format("Batch Ends %s", getDateForTime(it.crRunEnd))
        }
    }

    private fun confirmReset() {
        val builder = android.app.AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val customView = inflater.inflate(R.layout.custom_dialog, null)
        customView.okBtn.text = "Yes"
        customView.cancelBtn.text = "No"
        customView.description.text = "Are you sure you want to reset progress?"
        builder.setCancelable(false)
        builder.setView(customView)
        val dialog = builder.create()
        customView.cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        customView.okBtn.setOnClickListener {
            viewModel.resetProgress()
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        arguments?.let {
            attemptId = it.getString(ARG_ATTEMPT_ID)!!
            runId = it.getString(RUN_ID)!!
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, crUid: String) =
            OverviewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ATTEMPT_ID, param1)
                    putString(RUN_ID, crUid)
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
