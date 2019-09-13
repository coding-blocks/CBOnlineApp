package com.codingblocks.cbonlineapp.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.CarouselSliderAdapter
import com.codingblocks.cbonlineapp.adapters.CourseDataAdapter
import com.codingblocks.cbonlineapp.adapters.EndlessPagerAdapter
import com.codingblocks.cbonlineapp.database.models.CourseInstructorHolder
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.ui.HomeFragmentUi
import com.codingblocks.cbonlineapp.viewmodels.HomeViewModel
import com.codingblocks.onlineapi.models.CarouselCards
import com.google.firebase.analytics.FirebaseAnalytics
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.ctx
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import java.util.TimerTask
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), AnkoLogger {

    private lateinit var courseDataAdapter: CourseDataAdapter
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val ui = HomeFragmentUi<Fragment>()

    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = ui.createView(AnkoContext.create(ctx, this))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs()?.SP_ONEAUTH_ID)
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, "Home")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)

        setHasOptionsMenu(true)

        courseDataAdapter = CourseDataAdapter()
        ui.rvCourses.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(ctx)
            adapter = courseDataAdapter
        }
        ui.homeImg.visibility = View.GONE

        ui.swipeRefreshLayout.setOnRefreshListener {
            viewModel.progress.value = true
            viewModel.fetchRecommendedCourses()
        }

        viewModel.fetchCards()

        displayCourses()
        viewModel.carouselCards.observer(viewLifecycleOwner) {
            if (it.isEmpty()) {
                ui.viewPager.visibility = View.GONE
                ui.homeImg.visibility = View.VISIBLE
            } else {
                ui.viewPager.visibility = View.VISIBLE
                ui.homeImg.visibility = View.GONE
                val carouselSliderAdapter =
                    CarouselSliderAdapter(it as ArrayList<CarouselCards>, context)
                val endlessPagerAdapter = EndlessPagerAdapter(carouselSliderAdapter, ui.viewPager)
                ui.viewPager.adapter = endlessPagerAdapter
                ui.viewPager.currentItem = 1
                // Todo - fix this
//                ui.viewPager.setPageTransformer(true, ZoomOutPageTransformer())
                val handler = Handler()
                val update = Runnable {
                    ui.viewPager.setCurrentItem(ui.viewPager.currentItem + 1, true)
                }
                val swipeTimer = Timer()
                swipeTimer.schedule(object : TimerTask() {
                    override fun run() {
                        handler.post(update)
                    }
                }, 5000, 5000)
            }
        }

        viewModel.progress.observer(viewLifecycleOwner) {
            ui.swipeRefreshLayout.isRefreshing = it
        }
    }

    private fun displayCourses(searchQuery: String = "") {
        viewModel.getAllCourses().observer(this) {
            if (it.isNotEmpty()) {
                val response = CourseInstructorHolder.groupInstructorByCourse(it)
                courseDataAdapter.submitList(response.filter { c ->
                    c.courseRun.course.title.contains(searchQuery, true) ||
                        c.courseRun.course.summary.contains(searchQuery, true)
                })
                ui.shimmerLayout.stopShimmer()
            } else {
                viewModel.fetchRecommendedCourses()
            }
            ui.shimmerLayout.isVisible = it.isEmpty()
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
                if (newText.isNotEmpty())
                    displayCourses(newText)
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }
}
