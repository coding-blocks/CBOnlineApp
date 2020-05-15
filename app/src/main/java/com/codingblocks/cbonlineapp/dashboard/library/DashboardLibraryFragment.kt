package com.codingblocks.cbonlineapp.dashboard.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.dashboard.DashboardViewModel
import com.codingblocks.cbonlineapp.dashboard.mycourses.ItemClickListener
import com.codingblocks.cbonlineapp.dashboard.mycourses.MyCourseListAdapter
import com.codingblocks.cbonlineapp.library.LibraryActivity
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.extensions.changeViewState
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard_library.*
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.support.v4.intentFor
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class DashboardLibraryFragment : BaseCBFragment() {

    private val viewModel: DashboardViewModel by sharedViewModel()
    private val courseListAdapter = MyCourseListAdapter("RUN")

    private val itemClickListener: ItemClickListener by lazy {
        object : ItemClickListener {

            override fun onClick(id: String, runId: String, runAttemptId: String, name: String) {
                startActivity(
                    intentFor<LibraryActivity>(
                        RUN_ATTEMPT_ID to runAttemptId,
                        COURSE_NAME to name
                    ).singleTop()
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_dashboard_library, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.isLoggedIn == true) {
            dashboardCoursesRv.setRv(requireContext(), courseListAdapter, true)
            viewModel.allRuns.observer(viewLifecycleOwner) {
                courseListAdapter.submitList(it)
                changeViewState(dashboardCoursesRv, emptyLl, dashboardCourseShimmer, it.isEmpty())
            }
        }
        dashboardLibraryEmptyBtn.setOnClickListener {
            requireActivity().dashboardBottomNav.selectedItemId = R.id.dashboard_explore
        }
        courseListAdapter.onItemClick = itemClickListener
//        loginBtn.setOnClickListener{ startActivity(intentFor<LoginActivity>()) }
    }

    override fun onDestroyView() {
        courseListAdapter.onItemClick = null
        super.onDestroyView()
    }
}
