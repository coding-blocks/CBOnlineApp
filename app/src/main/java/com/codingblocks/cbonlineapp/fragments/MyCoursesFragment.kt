package com.codingblocks.cbonlineapp.fragments

import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.graphics.drawable.PictureDrawable
import android.os.Build.VERSION_CODES.N_MR1
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.caverock.androidsvg.SVG
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.MyCourseActivity
import com.codingblocks.cbonlineapp.adapters.CourseDataAdapter
import com.codingblocks.cbonlineapp.database.models.CourseRun
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.ui.HomeFragmentUi
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.viewmodels.HomeViewModel
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.firebase.analytics.FirebaseAnalytics
import okhttp3.Request
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
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
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        val params = Bundle()
        params.putString(FirebaseAnalytics.Param.ITEM_ID, getPrefs()?.SP_ONEAUTH_ID)
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, "MyCourses")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params)

        courseDataAdapter =
            CourseDataAdapter(
                ArrayList(),
                viewModel.getCourseDao(),
                requireContext(),
                viewModel.getCourseWithInstructorDao(),
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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1)
            createShortcut()
    }

    private fun displayCourses(searchQuery: String = "") {
        viewModel.getMyRuns().observer(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                skeletonScreen.hide()
                courseDataAdapter.setData(it.filter { c ->
                    c.title.contains(searchQuery, true) ||
                        c.summary.contains(searchQuery, true)
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

    @TargetApi(N_MR1)
    fun createShortcut() {

        val sM = requireContext().getSystemService(ShortcutManager::class.java)
        val shortcutList: MutableList<ShortcutInfo> = ArrayList()

        viewModel.getTopRun().observer(viewLifecycleOwner) {
            doAsync {
                it.forEachIndexed { index, courseRun ->
                    val data = viewModel.getCourseById(courseRun.crCourseId)

                    val intent = Intent(activity, MyCourseActivity::class.java).apply {
                        action = Intent.ACTION_VIEW
                        putExtra(COURSE_ID, courseRun.crCourseId)
                        putExtra(RUN_ATTEMPT_ID, courseRun.crAttemptId)
                        putExtra(COURSE_NAME, data.title)
                        putExtra(RUN_ID, courseRun.crUid)
                    }

                    val shortcut = ShortcutInfo.Builder(requireContext(), "topcourse$index")
                    shortcut.setIntent(intent)
                    shortcut.setLongLabel(courseRun.title)
                    shortcut.setShortLabel(courseRun.title)
                    shortcut.setDisabledMessage("Login to open this")

                    MediaUtils.okHttpClient.newCall(Request.Builder().url(data.logo).build())
                        .execute().body()?.let {
                            with(SVG.getFromInputStream(it.byteStream())) {
                                val picDrawable = PictureDrawable(
                                    this.renderToPicture(
                                        400, 400
                                    )
                                )
                                val bitmap =
                                    MediaUtils.getBitmapFromPictureDrawable(picDrawable)
                                val circularBitmap = MediaUtils.getCircularBitmap(bitmap)
                                shortcut.setIcon(Icon.createWithBitmap(circularBitmap))
                                shortcutList.add(index, shortcut.build())
                            }
                        }
                }
                sM?.dynamicShortcuts = shortcutList
            }
        }
    }
}
