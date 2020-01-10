package com.codingblocks.cbonlineapp.mycourse.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.fragment_overview.view.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class OverviewFragment : Fragment(), AnkoLogger {

    private val viewModel by sharedViewModel<MyCourseViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_overview, container, false)

//        extensionsAdapter = ExtensionsAdapter(ArrayList())
//        view.extensionsRv.apply {
//            isNestedScrollingEnabled = false
//            layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//
//            adapter = extensionsAdapter
//        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUpObservers(view: View) {

//        extensionsAdapter.checkedPosition.observer(viewLifecycleOwner) {
//            buyBtn.isEnabled = it != -1
//        }

        viewModel.resetProgress.observer(viewLifecycleOwner) {
            requireActivity().finish()
        }

        viewModel.popMessage.observer(viewLifecycleOwner) { message ->
            Snackbar.make(view.rootView, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun confirmReset() {
        Components.showConfirmation(requireContext(), "reset") {
            if (it) {
                viewModel.resetProgress()
            }
        }
    }
}
