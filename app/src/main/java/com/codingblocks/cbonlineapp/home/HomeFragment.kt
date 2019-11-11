package com.codingblocks.cbonlineapp.home

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
import com.codingblocks.cbonlineapp.commons.EndlessPagerAdapter
import com.codingblocks.cbonlineapp.util.extensions.nonNull
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.onlineapi.models.CarouselCards
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.ctx
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.TimerTask
import java.util.Timer

class HomeFragment : Fragment(), AnkoLogger {

    private lateinit var courseDataAdapter: CourseDataAdapter
    val ui = HomeFragmentUi<Fragment>()
    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
        View? = ui.createView(AnkoContext.create(ctx, this))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        attachObservers()
    }

    private fun attachObservers() {

        viewModel.courses.nonNull().observer(this) { list ->
            if (list.isEmpty()) {
                viewModel.fetchRecommendedCourses()
            } else {
                courseDataAdapter.submitList(list)
                ui.shimmerLayout.stopShimmer()
            }
            ui.shimmerLayout.isVisible = list.isEmpty()
        }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home, menu)
        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView
        searchView.setOnCloseListener {
            viewModel.courseFilter.value = ""
            false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty())
                    viewModel.courseFilter.value = newText
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }
}
