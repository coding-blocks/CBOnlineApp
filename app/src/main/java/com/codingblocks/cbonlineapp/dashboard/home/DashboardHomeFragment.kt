package com.codingblocks.cbonlineapp.dashboard.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.auth.LoginActivity
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.dashboard.DashboardViewModel
import com.codingblocks.cbonlineapp.mycourse.MyCourseActivity
import com.codingblocks.cbonlineapp.mycourse.player.VideoPlayerActivity
import com.codingblocks.cbonlineapp.util.extensions.hideAndStop
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.onlineapi.models.ProgressItem
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.skydoves.progressview.ProgressView
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard_home.*
import kotlinx.android.synthetic.main.item_performance.*
import org.jetbrains.anko.support.v4.intentFor
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DashboardHomeFragment : BaseCBFragment() {

    private val vm: DashboardViewModel by sharedViewModel()

    private val recentlyPlayedAdapter = RecentlyPlayedAdapter()

    private val itemClickListener: ItemClickListener by lazy {
        object : ItemClickListener {
            override fun onClick(sectionId: String, contentId: String, postition: Long) {
                startActivity(VideoPlayerActivity.createVideoPlayerActivityIntent(requireContext(), contentId, sectionId, postition))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dashboard_home, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dashboardHomeShimmer.startShimmer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recentPlayedRv.setRv(requireContext(), recentlyPlayedAdapter, orientation = RecyclerView.HORIZONTAL, space = 28f)
        recentlyPlayedAdapter.onItemClick = itemClickListener
        exploreBtn.setOnClickListener { requireActivity().dashboardBottomNav.selectedItemId = R.id.dashboard_explore }
        exploreBtn2.setOnClickListener { requireActivity().dashboardBottomNav.selectedItemId = R.id.dashboard_explore }
        loginBtn.setOnClickListener {
            startActivity(intentFor<LoginActivity>())
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        recentlyPlayedAdapter.onItemClick = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if (vm.isLoggedIn == true) {
            vm.fetchTopRunWithStats().observe(viewLifecycleOwner, Observer { coursePair ->
                dashboardProgressContainer.isVisible = coursePair != null
                dashboardEmptyProgress.isVisible = coursePair == null
                if (coursePair != null) {
                    vm.getStats(coursePair.runAttempt.attemptId)
                    dashboardHomeShimmer.hideAndStop()
                    dashboardHome.isVisible = true
                    homeCourseLogoImg.loadImage(coursePair.course.logo)
                    coursePair.getProgress().let { progress ->
                        homeProgressTv.text = getString(R.string.progress, progress.toInt())
                        homeProgressView.setGradientColor(progress)
                    }

                    with(requireActivity()) {
                        toolbarCourseTitleTv?.text = coursePair.course.title
                        toolbarCourseTitleTv?.isVisible = true
                        toolbarCourseResumeTv?.isVisible = true
                        dashboardToolbarSecondary?.setOnClickListener {
                            startActivity(MyCourseActivity.createMyCourseActivityIntent(
                                requireContext(),
                                coursePair.runAttempt.attemptId,
                                coursePair.course.title
                            ))
                        }
                    }
                    vm.getPerformance(coursePair.runAttempt.attemptId).observer(viewLifecycleOwner) {
                        homePerformanceTv.text = it.remarks
                        homePercentileTv.text = it.percentile.toString()
                        chart1.loadData(it.averageProgress, it.userProgress)
                    }
                }
            })

            vm.fetchRecentlyPlayed().observer(viewLifecycleOwner) {
                recentlyPlayedContainer.isVisible = it.isNotEmpty()
                recentlyPlayedAdapter.submitList(it)
            }
        } else {
            dashboardHomeShimmer.hideAndStop()
            dashboardHome.isVisible = false
            dashboardHomeLoggedOut.isVisible = true
        }
    }
}

fun ProgressView.setGradientColor(progress: Double) {
    this.progress = progress.toFloat()
    if (progress > 90) {
        highlightView.colorGradientStart =
            getColor(context, R.color.kiwigreen)
        highlightView.colorGradientEnd =
            getColor(context, R.color.tealgreen)
    } else {
        highlightView.colorGradientStart =
            getColor(context, R.color.pastel_red)
        highlightView.colorGradientEnd =
            getColor(context, R.color.dusty_orange)
    }
}

fun LineChart.loadData(averageProgress: java.util.ArrayList<ProgressItem>, userProgress: java.util.ArrayList<ProgressItem>) {
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
        color = getColor(context, R.color.pastel_red)
        setDrawValues(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        lineWidth = 3f
    }

    val set2 = LineDataSet(values2, "User Progress")
    set2.apply {
        setDrawCircles(false)
        color = getColor(context, R.color.kiwigreen)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawValues(false)
        lineWidth = 3f
    }
    val dataSets: ArrayList<ILineDataSet> = ArrayList()
    dataSets.add(set1)
    dataSets.add(set2)
    val data = LineData(dataSets)

    this.apply {
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
