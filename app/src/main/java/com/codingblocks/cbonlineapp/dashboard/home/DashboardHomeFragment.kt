package com.codingblocks.cbonlineapp.dashboard.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.DashboardViewModel
import com.codingblocks.cbonlineapp.mycourse.MyCourseActivity
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.util.extensions.loadSvg
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.onlineapi.models.ProgressItem
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard_home.*
import kotlinx.android.synthetic.main.item_performance.*
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.support.v4.intentFor
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DashboardHomeFragment : Fragment() {

    private val viewModel by sharedViewModel<DashboardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_dashboard_home, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.topRun.observer(viewLifecycleOwner) { courseAndRun ->
            viewModel.getStats(courseAndRun.runAttempt.attemptId)
            with(courseAndRun) {
                activity?.toolbarCourseTitleTv?.text = course.title
                activity?.toolbarCourseResumeTv?.setOnClickListener {
                    startActivity(intentFor<MyCourseActivity>(
                        COURSE_ID to course.cid,
                        RUN_ID to run.crUid,
                        RUN_ATTEMPT_ID to runAttempt.attemptId,
                        COURSE_NAME to course.title
                    ).singleTop())
                }
                homeCourseLogoImg.loadSvg(course.logo)
                val progress = if (courseAndRun.runAttempt.completedContents > 0) (courseAndRun.runAttempt.completedContents / courseAndRun.run.totalContents.toDouble()) * 100 else 0.0

                homeProgressTv.text = "${progress.toInt()} %"
                homeProgressView.progress = progress.toFloat()
                if (progress > 90) {
                    homeProgressView.highlightView.colorGradientStart = getColor(requireContext(), R.color.kiwigreen)
                    homeProgressView.highlightView.colorGradientEnd = getColor(requireContext(), R.color.tealgreen)
                } else {
                    homeProgressView.highlightView.colorGradientStart = getColor(requireContext(), R.color.pastel_red)
                    homeProgressView.highlightView.colorGradientEnd = getColor(requireContext(), R.color.dusty_orange)
                }
            }
        }
        viewModel.runPerformance.observer(viewLifecycleOwner) {
            homePerformanceTv.text = it.remarks
            homePercentileTv.text = it.percentile.toString()
            loadData(it.averageProgress, it.userProgress)
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
        values.add(Entry(2f, 100f))
        var set1 = LineDataSet(values, "Average Progress")
        var set2 = LineDataSet(values2, "User Progress")

        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(set1)
        dataSets.add(set2)
        val data = LineData(dataSets)

        chart1.data = data
        chart1.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
