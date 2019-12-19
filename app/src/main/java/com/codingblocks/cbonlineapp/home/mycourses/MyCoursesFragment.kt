package com.codingblocks.cbonlineapp.home.mycourses

import android.annotation.TargetApi
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build
import android.os.Build.VERSION_CODES.N_MR1
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.home.CourseDataAdapter
import com.codingblocks.cbonlineapp.home.HomeFragmentUi
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.getPrefs
import com.codingblocks.cbonlineapp.util.extensions.nonNull
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.google.firebase.analytics.FirebaseAnalytics
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.support.v4.ctx
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyCoursesFragment : Fragment(), AnkoLogger {

    val ui = HomeFragmentUi<Fragment>()
    private var courseDataAdapter = CourseDataAdapter("myCourses")

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private val viewModel by viewModel<MyCoursesViewModel>()

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

        attachObservers()

        ui.swipeRefreshLayout.setOnRefreshListener {
            //            viewModel.fetchMyCourses(true)
        }

        if (Build.VERSION.SDK_INT >= N_MR1)
            createShortcut()
    }

    private fun attachObservers() {
        viewModel.progress.observer(viewLifecycleOwner) {
            ui.swipeRefreshLayout.isRefreshing = it
        }

        viewModel.message.observer(viewLifecycleOwner) { error ->
            if (error.message == UNAUTHORIZED) {
                getPrefs()?.SP_ACCESS_TOKEN_KEY = PreferenceHelper.ACCESS_TOKEN
                Components.showConfirmation(requireContext(), UNAUTHORIZED) {
                    requireActivity().finish()
                }
            } else {
                ui.snackbarView.longSnackbar(getString(R.string.offline_message))
            }
        }
        viewModel.courses.nonNull().observer(this) {
            if (it.isEmpty()) {
//                viewModel.fetchMyCourses()
            } else {
                courseDataAdapter.submitList(it)
                ui.shimmerLayout.stopShimmer()
            }
            ui.shimmerLayout.isVisible = it.isEmpty()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home, menu)
        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView

        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean = true

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                viewModel.courseFilter.value = ""
                return true
            }
        })
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
    }

    override fun onOptionsMenuClosed(menu: Menu) {
        viewModel.courseFilter.value = ""
        super.onOptionsMenuClosed(menu)
    }

    @TargetApi(N_MR1)
    fun createShortcut() {

        val sM = requireContext().getSystemService(ShortcutManager::class.java)
        val shortcutList: MutableList<ShortcutInfo> = ArrayList()
//        viewModel.getTopRun().observeOnce {
//            doAsync {
//                if (it.isNotEmpty())
//                    it.forEachIndexed { index, courseRun ->
//                        val intent = Intent(activity, MyCourseActivity::class.java).apply {
//                            action = Intent.ACTION_VIEW
//                            putExtra(COURSE_ID, courseRun.crCourseId)
//                            putExtra(RUN_ATTEMPT_ID, courseRun.crAttemptId)
//                            putExtra(COURSE_NAME, courseRun.course.title)
//                            putExtra(RUN_ID, courseRun.crUid)
//                        }
//
//                        val shortcut = ShortcutInfo.Builder(requireContext(), "topcourse$index")
//                        shortcut.setIntent(intent)
//                        shortcut.setLongLabel(courseRun.course.title)
//                        shortcut.setShortLabel(courseRun.course.title)
//
//                        okHttpClient.newCall(Request.Builder().url(courseRun.course.logo).build())
//                            .execute().body()?.let {
//                                with(SVG.getFromInputStream(it.byteStream())) {
//                                    val picDrawable = PictureDrawable(
//                                        this.renderToPicture(
//                                            400, 400
//                                        )
//                                    )
//                                    val bitmap =
//                                        MediaUtils.getBitmapFromPictureDrawable(picDrawable)
//                                    val circularBitmap = MediaUtils.getCircularBitmap(bitmap)
//                                    shortcut.setIcon(Icon.createWithBitmap(circularBitmap))
//                                    shortcutList.add(index, shortcut.build())
//                                }
//                            }
//                    }
//            }
//            // Todo Crash Here null pointer
//            sM?.dynamicShortcuts = shortcutList
//        }
    }

    fun refreshCourses() {
//        viewModel.fetchMyCourses(true)
    }
}
