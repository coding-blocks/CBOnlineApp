package com.codingblocks.cbonlineapp.dashboard.home

import android.graphics.Color
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
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
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
        loadData()
    }

    fun loadData() {
        chart1.setTouchEnabled(false);
        chart1.isDragEnabled = true;
        chart1.setScaleEnabled(true);
        chart1.setPinchZoom(false);
        chart1.setDrawGridBackground(false);
        chart1.maxHighlightDistance = 200f;
        chart1.setViewPortOffsets(0f, 0f, 0f, 0f)


        val entryArrayList: ArrayList<Entry> = ArrayList()
        entryArrayList.add(Entry(0f, 60f, "1"))
        entryArrayList.add(Entry(1f, 55f, "2"))
        entryArrayList.add(Entry(2f, 60f, "3"))
        entryArrayList.add(Entry(3f, 40f, "4"))
        entryArrayList.add(Entry(4f, 45f, "5"))
        entryArrayList.add(Entry(5f, 36f, "6"))
        entryArrayList.add(Entry(6f, 30f, "7"))
        entryArrayList.add(Entry(7f, 40f, "8"))
        entryArrayList.add(Entry(8f, 45f, "9"))
        entryArrayList.add(Entry(9f, 60f, "10"))
        entryArrayList.add(Entry(10f, 45f, "10"))
        entryArrayList.add(Entry(11f, 20f, "10"))


        //LineDataSet is the line on the graph
        //LineDataSet is the line on the graph
        val lineDataSet = LineDataSet(entryArrayList, "This is y bill")


        lineDataSet.highLightColor = Color.RED
        lineDataSet.setDrawValues(false)
        lineDataSet.circleRadius = 10f
        lineDataSet.setCircleColor(Color.YELLOW)

        //to make the smooth line as the graph is adrapt change so smooth curve
        //to make the smooth line as the graph is adrapt change so smooth curve
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        //to enable the cubic density : if 1 then it will be sharp curve
        //to enable the cubic density : if 1 then it will be sharp curve
        lineDataSet.cubicIntensity = 0.2f

        //to fill the below of smooth line in graph
        //to fill the below of smooth line in graph
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillColor = Color.BLACK
        //set the transparency
        //set the transparency
        lineDataSet.fillAlpha = 80

        //set the gradiant then the above draw fill color will be replace
        //set the gradiant then the above draw fill color will be replace
        val drawable = ContextCompat.getDrawable(context!!, R.drawable.fade_yellow)
        lineDataSet.fillDrawable = drawable

        //set legend disable or enable to hide {the left down corner name of graph}
        //set legend disable or enable to hide {the left down corner name of graph}
        val legend: Legend = chart1.getLegend()
        legend.isEnabled = false

        //to remove the cricle from the graph
        //to remove the cricle from the graph
        lineDataSet.setDrawCircles(false)

        //lineDataSet.setColor(ColorTemplate.COLORFUL_COLORS);


        //lineDataSet.setColor(ColorTemplate.COLORFUL_COLORS);
        val iLineDataSetArrayList: ArrayList<ILineDataSet> = ArrayList()
        iLineDataSetArrayList.add(lineDataSet)

        //LineData is the data accord
        //LineData is the data accord
        val lineData = LineData(iLineDataSetArrayList)
        lineData.setValueTextSize(13f)
        lineData.setValueTextColor(Color.BLACK)


        chart1.setData(lineData)
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }

}
