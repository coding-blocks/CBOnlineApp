package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.CourseDataAdapter
import com.codingblocks.cbonlineapp.database.models.CourseRun
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.ui.HomeFragmentUi
import com.codingblocks.cbonlineapp.viewmodels.HomeViewModel
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.firebase.analytics.FirebaseAnalytics
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.ctx
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyCoursesFragment : Fragment(), AnkoLogger {

    val ui = HomeFragmentUi<Fragment>()
    private lateinit var courseDataAdapter: CourseDataAdapter
    private lateinit var skeletonScreen: SkeletonScreen
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = ui.createView(AnkoContext.create(ctx, this))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs()?.SP_ONEAUTH_ID)
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, "MyCourses")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)

        courseDataAdapter =
            CourseDataAdapter(
                ArrayList(),
                activity!!,
                viewModel.courseWithInstructorDao,
                "myCourses"
            )

        setHasOptionsMenu(true)

        ui.allcourseText.text = getString(R.string.my_courses)
        ui.titleText.visibility = View.GONE
        ui.homeImg.visibility = View.GONE
        ui.viewPager.visibility = View.GONE

        ui.rvCourses.layoutManager = LinearLayoutManager(ctx)
        ui.rvCourses.adapter = courseDataAdapter

        skeletonScreen = Skeleton.bind(ui.rvCourses)
            .adapter(courseDataAdapter)
            .shimmer(true)
            .angle(20)
            .frozen(true)
            .duration(1200)
            .count(4)
            .load(R.layout.item_skeleton_course_card)
            .show()
        viewModel.fetchMyCourses()

        displayCourses()

        ui.swipeRefreshLayout.setOnRefreshListener {
            viewModel.progress.value = true
            skeletonScreen.show()
            viewModel.fetchMyCourses(true)
        }

        viewModel.progress.observer(viewLifecycleOwner) {
            ui.swipeRefreshLayout.isRefreshing = it
        }
    }

    private fun displayCourses(searchQuery: String = "") {
        viewModel.runDao.getMyRuns().observer(viewLifecycleOwner) {
            if (!it.isEmpty()) {
                skeletonScreen.hide()
                courseDataAdapter.setData(it.filter { c ->
                    c.title.contains(searchQuery, true)
                } as ArrayList<CourseRun>)
            } else {
                viewModel.fetchMyCourses()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home, menu)
        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView
        searchView.setOnCloseListener {
            displayCourses()
            false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                displayCourses(newText)
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }
}
