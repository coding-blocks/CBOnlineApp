package com.codingblocks.cbonlineapp.mycourse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.analytics.AppCrashlyticsWrapper
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.commons.TabLayoutAdapter
import com.codingblocks.cbonlineapp.mycourse.content.CourseContentFragment
import com.codingblocks.cbonlineapp.mycourse.library.CourseLibraryFragment
import com.codingblocks.cbonlineapp.mycourse.overview.OverviewFragment
import com.codingblocks.cbonlineapp.mycourse.player.VideoPlayerActivity.Companion.createVideoPlayerActivityIntent
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.LECTURE
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.VIDEO
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.extensions.animateVisibility
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.pageChangeCallback
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.onlineapi.ErrorStatus
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_my_course.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class MyCourseActivity : BaseCBActivity(), AnkoLogger, SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: MyCourseViewModel by stateViewModel()
    private val pagerAdapter by lazy { TabLayoutAdapter(supportFragmentManager) }
    private val sharedPrefs: PreferenceHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_course)
        setToolbar(toolbar_mycourse)
        intent.getStringExtra(RUN_ATTEMPT_ID)?.let {
            viewModel.attemptId = it
        }
        intent.getStringExtra(COURSE_NAME)?.let {
            viewModel.name = it
        }
        title = viewModel.name

        if (!MediaUtils.checkPermission(this)) {
            MediaUtils.isStoragePermissionGranted(this)
        }
        viewModel.nextContent?.observe(this, Observer { content ->
            courseResumeBtn.setOnClickListener {
                if (content != null)
                    when (content.contentable) {
                        LECTURE, VIDEO -> startActivity(createVideoPlayerActivityIntent(this, content.contentId, content.sectionId))
                    } else {
                    toast("Please Wait while the content is being updated!")
                }
            }
        })

        viewModel.errorLiveData.observer(this) {
            when (it) {
                ErrorStatus.NO_CONNECTION -> {
                    myCourseRoot.showSnackbar(it, Snackbar.LENGTH_SHORT)
                }
                ErrorStatus.TIMEOUT -> {
                    myCourseRoot.showSnackbar(it, Snackbar.LENGTH_INDEFINITE) {
                        viewModel.fetchSections()
                        viewModel.getStats()
                    }
                }
                ErrorStatus.UNAUTHORIZED -> {
                    Components.showConfirmation(this, UNAUTHORIZED) {
                    }
                }
                else -> {
                    myCourseRoot.showSnackbar(it, Snackbar.LENGTH_SHORT)
                    AppCrashlyticsWrapper.log(it)
                }
            }
        }
        setupViewPager()
        walkthrough()
    }

    private fun setupViewPager() {
        myCourseTabs.setupWithViewPager(course_pager)
        pagerAdapter.apply {
            add(OverviewFragment(), getString(R.string.dashboard))
            add(CourseContentFragment(), getString(R.string.curriculum))
            add(CourseLibraryFragment(), getString(R.string.library))
        }

        course_pager.apply {
            setPagingEnabled(true)
            adapter = pagerAdapter
            currentItem = 0
            offscreenPageLimit = 2
            addOnPageChangeListener(
                pageChangeCallback { pos, fl, i2 ->
                    if (pos == 1) {
                        fab.animateVisibility(View.VISIBLE)
                    } else {
                        if (fab.visibility == View.VISIBLE) {
                            fab.animateVisibility(View.GONE)
                        }
                    }
                }
            )
        }
    }

    override fun onRefresh() {
        viewModel.fetchSections(true)
    }

    companion object {

        fun createMyCourseActivityIntent(context: Context, attemptId: String, name: String = ""): Intent {
            return context.intentFor<MyCourseActivity>(COURSE_NAME to name, RUN_ATTEMPT_ID to attemptId).singleTop()
        }
    }

    fun walkthrough() {
        if (!sharedPrefs.SP_FIRST_COURSE_RUN) {
            TapTargetSequence(this)
                .targets(
                    TapTarget.forView(findViewById(R.id.courseResumeBtn), "Resume", "Continue the course from exactly where you left it.")
                        .outerCircleColor(R.color.colorPrimary)
                        .outerCircleAlpha(.90f)
                        .targetRadius(70)
                        .titleTextSize(30)
                        .titleTextColor(R.color.white)
                        .descriptionTextSize(16)
                        .descriptionTextColor(R.color.white)
                        .dimColor(R.color.black)
                        .drawShadow(true)
                        .cancelable(false)
                        .tintTarget(true)
                        .transparentTarget(true),
                    TapTarget.forView((myCourseTabs.getChildAt(0) as ViewGroup).getChildAt(1), "Curriculum", "Go through all the material of the selected course.")
                        .outerCircleColor(R.color.colorPrimary)
                        .outerCircleAlpha(.90f)
                        .titleTextSize(30)
                        .titleTextColor(R.color.white)
                        .descriptionTextSize(16)
                        .descriptionTextColor(R.color.white)
                        .dimColor(R.color.black)
                        .drawShadow(true)
                        .cancelable(false)
                        .tintTarget(true)
                        .transparentTarget(true),
                    TapTarget.forView((myCourseTabs.getChildAt(0) as ViewGroup).getChildAt(2), "Library", "See all you important bookmarks, notes and downloads at a single place")
                        .outerCircleColor(R.color.colorPrimary)
                        .outerCircleAlpha(.90f)
                        .titleTextSize(30)
                        .titleTextColor(R.color.white)
                        .descriptionTextSize(16)
                        .descriptionTextColor(R.color.white)
                        .dimColor(R.color.black)
                        .drawShadow(true)
                        .cancelable(false)
                        .tintTarget(true)
                        .transparentTarget(true)
                )
                .start()
            sharedPrefs.SP_FIRST_COURSE_RUN = true
        }
    }
}
