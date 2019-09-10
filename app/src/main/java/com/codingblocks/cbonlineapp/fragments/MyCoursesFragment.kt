package com.codingblocks.cbonlineapp.fragments

import android.annotation.TargetApi
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build
import android.os.Build.VERSION_CODES.N_MR1
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.CourseDataAdapter
import com.codingblocks.cbonlineapp.database.models.CourseInstructorHolder
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.observeOnce
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.ui.HomeFragmentUi
import com.codingblocks.cbonlineapp.viewmodels.HomeViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.ctx
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyCoursesFragment : Fragment(), AnkoLogger {

    val ui = HomeFragmentUi<Fragment>()
    private lateinit var courseDataAdapter: CourseDataAdapter
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = ui.createView(AnkoContext.create(ctx, this))


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs()?.SP_ONEAUTH_ID)
            putString(FirebaseAnalytics.Param.ITEM_NAME, "MyCourses")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)

        courseDataAdapter = CourseDataAdapter("myCourses")

        setHasOptionsMenu(true)

        ui.allcourseText.text = getString(R.string.my_courses)
        ui.titleText.visibility = View.GONE
        ui.homeImg.visibility = View.GONE
        ui.viewPager.visibility = View.GONE
        ui.rvCourses.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(ctx)
            adapter = courseDataAdapter
        }
        displayCourses()

        ui.swipeRefreshLayout.setOnRefreshListener {
            viewModel.progress.value = true
            viewModel.fetchMyCourses(true)
        }

        viewModel.progress.observer(viewLifecycleOwner) {
            ui.swipeRefreshLayout.isRefreshing = it
        }

        if (Build.VERSION.SDK_INT >= N_MR1)
            createShortcut()
    }

    private fun displayCourses(searchQuery: String = "") {
        viewModel.getMyRuns().observer(this) {
            if (it.isNotEmpty()) {
                val response = CourseInstructorHolder.groupInstructorByRun(it)
                courseDataAdapter.submitList(response.filter { c ->
                    c.courseRun.course.title.contains(searchQuery, true) ||
                        c.courseRun.course.summary.contains(searchQuery, true)
                })
                ui.shimmerLayout.stopShimmer()
            } else {
                viewModel.fetchMyCourses()
            }
            ui.shimmerLayout.isVisible = it.isEmpty()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
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
    }

    @TargetApi(N_MR1)
    fun createShortcut() {

        val sM = requireContext().getSystemService(ShortcutManager::class.java)
        val shortcutList = ArrayList<ShortcutInfo>()

        viewModel.getTopRun().observeOnce {
            doAsync {
                //                it.forEachIndexed { index, courseRun ->
//                    val data = viewModel.getCourseById(courseRun.crCourseId)
//
//                    val intent = Intent(activity, MyCourseActivity::class.java).apply {
//                        action = Intent.ACTION_VIEW
//                        putExtra(COURSE_ID, courseRun.crCourseId)
//                        putExtra(RUN_ATTEMPT_ID, courseRun.crAttemptId)
//                        putExtra(COURSE_NAME, data.title)
//                        putExtra(RUN_ID, courseRun.crUid)
//                    }
//
//                    val shortcut = ShortcutInfo.Builder(requireContext(), "topcourse$index")
//                    shortcut.setIntent(intent)
////                    shortcut.setLongLabel(courseRun.title)
////                    shortcut.setShortLabel(courseRun.title)
//                    shortcut.setDisabledMessage("Login to open this")
//
//                    okHttpClient.newCall(Request.Builder().url(data.logo).build())
//                        .execute().body()?.let {
//                            with(SVG.getFromInputStream(it.byteStream())) {
//                                val picDrawable = PictureDrawable(
//                                    this.renderToPicture(
//                                        400, 400
//                                    )
//                                )
//                                val bitmap =
//                                    MediaUtils.getBitmapFromPictureDrawable(picDrawable)
//                                val circularBitmap = MediaUtils.getCircularBitmap(bitmap)
//                                shortcut.setIcon(Icon.createWithBitmap(circularBitmap))
//                                shortcutList.add(index, shortcut.build())
//                            }
//                        }
//                }
                sM?.dynamicShortcuts = shortcutList
            }
        }
    }
}
