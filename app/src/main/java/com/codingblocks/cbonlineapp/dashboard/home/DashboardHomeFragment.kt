package com.codingblocks.cbonlineapp.dashboard.home

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard_home.*
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.support.v4.intentFor
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class DashboardHomeFragment : Fragment() {

    private val viewModel by sharedViewModel<DashboardViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_dashboard_home, container, false)


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.topRun.observer(viewLifecycleOwner) { courseAndRun ->
            with(courseAndRun) {
                activity?.toolbarCourseTitleTv?.text = course.title
                activity?.toolbarCourseResumeTv?.setOnClickListener {
                    startActivity(intentFor<MyCourseActivity>(
                        COURSE_ID to course.cid,
                        RUN_ID to crUid,
                        RUN_ATTEMPT_ID to crAttemptId,
                        COURSE_NAME to course.title
                    ).singleTop())
                }
                homeCourseLogoImg.loadSvg(course.logo)
                val progress = (0..100).random()
                homeProgressTv.text = "$progress %"
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
        setData()
    }

    private fun setData(count: Int = 45, range: Float = 180f) {
        val values: ArrayList<Entry> = ArrayList()

        for (i in 0 until count) {
            val item = (Math.random() * range) - 30
            values.add(Entry(i.toFloat(), item.toFloat()))

        }
        val set1 = LineDataSet(values, "DataSet 1")

        set1.setDrawIcons(false)

        // draw dashed line
        // draw dashed line
        set1.enableDashedLine(10f, 5f, 0f)

        // black lines and points
        // black lines and points
        set1.color = Color.BLACK
        set1.setCircleColor(Color.BLACK)

        // line thickness and point size
        // line thickness and point size
        set1.lineWidth = 1f
        set1.circleRadius = 3f

        // draw points as solid circles
        // draw points as solid circles
        set1.setDrawCircleHole(false)

        // customize legend entry
        // customize legend entry
        set1.formLineWidth = 1f
        set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        set1.formSize = 15f

        // text size of values
        // text size of values
        set1.valueTextSize = 9f

        // draw selection line as dashed
        // draw selection line as dashed
        set1.enableDashedHighlightLine(10f, 5f, 0f)

        // set the filled area
        // set the filled area
        set1.setDrawFilled(true)
        set1.fillFormatter = IFillFormatter { _, _ -> chart1.axisLeft.axisMinimum }

        // set color of filled area
        // set color of filled area
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_yellow)

        set1.fillDrawable = drawable

        val dataSets: ArrayList<ILineDataSet> = ArrayList()
        dataSets.add(set1) // add the data sets


        // create a data object with the data sets
        // create a data object with the data sets
        val data = LineData(dataSets)

        // set data
        // set data
        chart1.data = data

    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

}
