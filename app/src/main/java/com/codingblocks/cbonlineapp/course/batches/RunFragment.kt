package com.codingblocks.cbonlineapp.course.batches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.course.CourseViewModel
import com.codingblocks.cbonlineapp.library.MyItemDetailsLookup
import com.codingblocks.cbonlineapp.library.MyItemKeyProvider
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import kotlinx.android.synthetic.main.fragment_course_run.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RunFragment : BaseCBFragment() {

    private val viewModel by sharedViewModel<CourseViewModel>()
    private val runListAdapter = RunListAdapter()
    private var selectionTracker: SelectionTracker<String>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_course_run, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runsRv.setRv(requireContext(), runListAdapter, true)
        selectionTracker = SelectionTracker.Builder<String>(
            "mySelection",
            runsRv,
            MyItemKeyProvider(runListAdapter),
            MyItemDetailsLookup(runsRv),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectSingleAnything()
        )
            .build()

        runListAdapter.apply {
            this.tracker = selectionTracker
        }

        selectionTracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val items = selectionTracker?.selection!!.size()
                    buyBtn.isVisible = items > 0
                    buyBtn.setOnClickListener {
                        selectionTracker?.selection?.let {
                            viewModel.clearCart(it.first())
                        }
                    }
                }
            })

        val runKey: String? = arguments?.getString("run")
        if (!runKey.isNullOrEmpty()) {
            viewModel.course.distinctUntilChanged().observer(viewLifecycleOwner) { course ->
                val list = course.activeRuns?.groupBy { it.start }?.get(runKey)?.sortedBy { it.price }
                runListAdapter.submitList(list)
            }
        } else {
            // Error Handling
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(run: String) =
            RunFragment().apply {
                arguments = Bundle().apply {
                    putString("run", run)
                }
            }
    }
}
