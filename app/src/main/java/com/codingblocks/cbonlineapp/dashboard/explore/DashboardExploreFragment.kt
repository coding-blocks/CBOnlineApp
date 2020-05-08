package com.codingblocks.cbonlineapp.dashboard.explore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.course.CourseActivity
import com.codingblocks.cbonlineapp.course.CourseListAdapter
import com.codingblocks.cbonlineapp.course.ItemClickListener
import com.codingblocks.cbonlineapp.course.SearchCourseActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardViewModel
import com.codingblocks.cbonlineapp.tracks.LearningTracksActivity
import com.codingblocks.cbonlineapp.tracks.TrackActivity
import com.codingblocks.cbonlineapp.tracks.TracksListAdapter
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_LOGO
import com.codingblocks.cbonlineapp.util.LOGO_TRANSITION_NAME
import com.codingblocks.cbonlineapp.util.extensions.hideAndStop
import com.codingblocks.cbonlineapp.util.extensions.setRv
import kotlinx.android.synthetic.main.activity_course.courseSuggestedRv
import kotlinx.android.synthetic.main.fragment_dashboard_explore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DashboardExploreFragment : BaseCBFragment() {

    private val vm: DashboardViewModel by sharedViewModel()

    private val courseCardListAdapter = CourseListAdapter()
    private val coursePopularListAdapter = CourseListAdapter("POPULAR")
    private val tracksListAdapter = TracksListAdapter()

    private val itemClickListener: ItemClickListener by lazy {
        object : ItemClickListener {
            override fun onClick(id: String, name: String, logo: ImageView) {
                val intent = Intent(requireContext(), CourseActivity::class.java)
                intent.putExtra(COURSE_ID, id)
                intent.putExtra(COURSE_LOGO, name)
                intent.putExtra(LOGO_TRANSITION_NAME, ViewCompat.getTransitionName(logo))

                val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    logo,
                    ViewCompat.getTransitionName(logo)!!)
                startActivity(intent, options.toBundle())
            }
        }
    }
    private val trackItemClickList: ItemClickListener by lazy {
        object : ItemClickListener {
            override fun onClick(id: String, name: String, logo: ImageView) {
                val intent = Intent(requireContext(), TrackActivity::class.java)
                intent.putExtra(COURSE_ID, id)
                intent.putExtra(COURSE_LOGO, name)
                intent.putExtra(LOGO_TRANSITION_NAME, ViewCompat.getTransitionName(logo))

                val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    logo,
                    ViewCompat.getTransitionName(logo)!!)
                startActivity(intent, options.toBundle())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = inflater.inflate(R.layout.fragment_dashboard_explore, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.fetchRecommendedCourses(0, 4)
        vm.fetchRecommendedCourses(4, 4)
        vm.fetchTracks()

        dashboardPopularRv.setRv(requireContext(), coursePopularListAdapter, orientation = RecyclerView.HORIZONTAL, space = 28f)
        courseSuggestedRv.setRv(requireContext(), courseCardListAdapter, orientation = RecyclerView.HORIZONTAL, space = 28f)
        dashboardTracksRv.setRv(requireContext(), tracksListAdapter, orientation = RecyclerView.HORIZONTAL, space = 28f)

        vm.suggestedCourses.observe(viewLifecycleOwner) { courses ->
            if (courses.isNotEmpty()) {
                courseCardListAdapter.submitList(courses)
                dashboardPopularShimmer.hideAndStop()
                dashboardPopularRv.isVisible = true
            }
        }
        vm.trendingCourses.observe(viewLifecycleOwner) { courses ->
            if (courses.isNotEmpty()) {
                coursePopularListAdapter.submitList(courses)
                dashboardSuggestedShimmer.hideAndStop()
                courseSuggestedRv.isVisible = true
            }
        }
        vm.tracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNotEmpty()) {
                tracksListAdapter.submitList(tracks)
                dashboardTrackShimmer.hideAndStop()
                dashboardTracksRv.isVisible = true
            }
        }

        courseCardListAdapter.onItemClick = itemClickListener
        coursePopularListAdapter.onItemClick = itemClickListener
        tracksListAdapter.onItemClick = trackItemClickList

        allCourseCardTv.setOnClickListener {
            startActivity<SearchCourseActivity>()
        }
        allCourseCard.setOnClickListener {
            startActivity<SearchCourseActivity>()
        }
        allTracksTv.setOnClickListener {
            startActivity<LearningTracksActivity>()
        }
        swipeToRefresh.setOnRefreshListener {
            vm.fetchRecommendedCourses(0, 4)
            vm.fetchRecommendedCourses(4, 4)
            vm.fetchTracks()
            GlobalScope.launch(Dispatchers.Main) {
                delay(5000)
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        courseCardListAdapter.onItemClick = null
        coursePopularListAdapter.onItemClick = null
        tracksListAdapter.onItemClick = null
        super.onDestroyView()
    }
}
