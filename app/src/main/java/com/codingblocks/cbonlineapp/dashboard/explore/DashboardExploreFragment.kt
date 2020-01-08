package com.codingblocks.cbonlineapp.dashboard.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.course.CourseListAdapter
import com.codingblocks.cbonlineapp.dashboard.DashboardViewModel
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import kotlinx.android.synthetic.main.activity_course.courseSuggestedRv
import kotlinx.android.synthetic.main.fragment_dashboard_explore.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DashboardExploreFragment : Fragment() {

    private val vm by sharedViewModel<DashboardViewModel>()
    private val courseCardListAdapter = CourseListAdapter()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_dashboard_explore, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.fetchRecommendedCourses()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dashboardPopularRv.setRv(requireContext(), courseCardListAdapter, orientation = RecyclerView.HORIZONTAL)
        courseSuggestedRv.setRv(requireContext(), courseCardListAdapter, orientation = RecyclerView.HORIZONTAL)

        vm.suggestedCourses.observer(this) { courses ->
            courseCardListAdapter.submitList(courses)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
