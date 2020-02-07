package com.codingblocks.cbonlineapp.mycourse.overview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.mycourse.MyCourseViewModel
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.onlineapi.models.ProgressItem
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.item_performance.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class OverviewFragment : BaseCBFragment(), AnkoLogger {

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
        viewModel.getRun().observer(viewLifecycleOwner) { courseAndRun ->
            viewModel.runStartEnd = Pair(courseAndRun.runAttempt.end.toLong() * 1000, courseAndRun.run.crStart.toLong())
            viewModel.getStats(courseAndRun.runAttempt.attemptId)
            val progressValue = if (courseAndRun.runAttempt.completedContents > 0) (courseAndRun.runAttempt.completedContents / courseAndRun.run.totalContents.toDouble()) * 100 else 0.0
            homeProgressTv.text = "${progressValue.toInt()} %"
            homeProgressView.apply {
                progress = progressValue.toFloat()
                if (progressValue > 90) {
                    highlightView.colorGradientStart = androidx.core.content.ContextCompat.getColor(requireContext(), com.codingblocks.cbonlineapp.R.color.kiwigreen)
                    highlightView.colorGradientEnd = androidx.core.content.ContextCompat.getColor(requireContext(), com.codingblocks.cbonlineapp.R.color.tealgreen)
                } else {
                    highlightView.colorGradientStart = androidx.core.content.ContextCompat.getColor(requireContext(), com.codingblocks.cbonlineapp.R.color.pastel_red)
                    highlightView.colorGradientEnd = androidx.core.content.ContextCompat.getColor(requireContext(), com.codingblocks.cbonlineapp.R.color.dusty_orange)
                }
            }
            courseAndRun.run.whatsappLink.let { link ->
                whatsappContainer.apply {
                    isVisible = !link.isNullOrEmpty()
                    setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setPackage("com.whatsapp")
                        intent.data = Uri.parse(link.toString())
                        if (requireContext().packageManager.resolveActivity(intent, 0) != null) {
                            startActivity(intent)
                        } else {
                            Toast.makeText(requireContext(), "Please install whatsApp", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        viewModel.getPerformance().observer(viewLifecycleOwner) {
            homePerformanceTv.text = it.remarks
            homePercentileTv.text = it.percentile.toString()
            loadData(it.averageProgress, it.userProgress)
        }

        confirmReset.setOnClickListener {
            Components.showConfirmation(requireContext(), "reset") {
                if (it) {
                    viewModel.resetProgress().observer(viewLifecycleOwner) {
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    private fun loadData(averageProgress: ArrayList<ProgressItem>, userProgress: ArrayList<ProgressItem>) {
        val values: ArrayList<Entry> = ArrayList()
        averageProgress.forEachIndexed { index, progressItem ->
            values.add(Entry(index.toFloat(), progressItem.progress.toFloat()))
        }

        val values2: ArrayList<Entry> = ArrayList()
        userProgress.forEachIndexed { index, progressItem ->
            values2.add(Entry(index.toFloat(), progressItem.progress.toFloat()))
        }
        val set1 = LineDataSet(values, "Average Progress")
        set1.apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(requireContext(), R.color.pastel_red)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            lineWidth = 3f
        }

        val set2 = LineDataSet(values2, "User Progress")
        set2.apply {
            setDrawCircles(false)
            color = ContextCompat.getColor(requireContext(), R.color.kiwigreen)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawValues(false)
            lineWidth = 3f
        }
        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(set1)
        dataSets.add(set2)
        val data = LineData(dataSets)

        chart1.apply {
            this.data = data
            setTouchEnabled(false)
            axisRight.setDrawGridLines(false)
            axisLeft.setDrawGridLines(true)
            xAxis.setDrawGridLines(true)
            notifyDataSetChanged()
            xAxis.labelCount = 10
            invalidate()
        }
    }

    private fun setUpObservers(view: View) {

//        extensionsAdapter.checkedPosition.observer(viewLifecycleOwner) {
//            buyBtn.isEnabled = it != -1
//        }

        viewModel.popMessage.observer(viewLifecycleOwner) { message ->
            Snackbar.make(view.rootView, message, Snackbar.LENGTH_LONG).show()
        }
    }
}
