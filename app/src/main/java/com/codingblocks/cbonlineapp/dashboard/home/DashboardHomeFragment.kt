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
                        RUN_ATTEMPT_ID to crRunAttemptEnd,
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}
