package com.codingblocks.cbonlineapp.mycourse

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.invoke
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.analytics.AppCrashlyticsWrapper
import com.codingblocks.cbonlineapp.auth.LoginActivity
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.dashboard.ViewPager2Adapter
import com.codingblocks.cbonlineapp.mycourse.content.player.VideoPlayerActivity.Companion.createVideoPlayerActivityIntent
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.DIALOG_TYPE
import com.codingblocks.cbonlineapp.util.LECTURE
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.TAB_POS
import com.codingblocks.cbonlineapp.util.VIDEO
import com.codingblocks.cbonlineapp.util.extensions.animateVisibility
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.cbonlineapp.util.livedata.observer
import com.codingblocks.cbonlineapp.util.livedata.pageChangeCallback
import com.codingblocks.cbonlineapp.util.showConfirmDialog
import com.codingblocks.onlineapi.ErrorStatus
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_my_course.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class MyCourseActivity : BaseCBActivity(), AnkoLogger, SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: MyCourseViewModel by stateViewModel()
    private val pagerAdapter: ViewPager2Adapter by lazy { ViewPager2Adapter(this) }
    private var confirmDialog: AlertDialog? = null

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            confirmDialog?.dismiss()
            toast(getString(R.string.logged_in))
            viewModel.fetchSections()
            viewModel.getStats()
        }
    }

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
        viewModel.nextContent?.observe(
            this,
            Observer { content ->
                courseResumeBtn.setOnClickListener {
                    if (content != null)
                        when (content.contentable) {
                            LECTURE, VIDEO -> startActivity(createVideoPlayerActivityIntent(this, content.contentId, content.sectionId))
                        } else {
                        toast("Please Wait while the content is being updated!")
                    }
                }
            }
        )

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
                    if (confirmDialog == null)
                        confirmDialog = showConfirmDialog(DIALOG_TYPE.UNAUTHORIZED) {
                            cancelable = false
                            positiveBtnClickListener { startForResult(intentFor<LoginActivity>()) }
                            negativeBtnClickListener { finish() }
                        }
                    confirmDialog?.show()
                }
                else -> {
                    AppCrashlyticsWrapper.log(it)
                }
            }
        }
        setupViewPager()
    }

    private fun setupViewPager() {

        pagerAdapter.apply {
            add(ViewPager2Adapter.FragmentName.COURSE_OVERVIEW)
            add(ViewPager2Adapter.FragmentName.COURSE_CURRICULUM)
            add(ViewPager2Adapter.FragmentName.COURSE_LIBRARY)
//            add(ViewPager2Adapter.FragmentName.COURSE_MISC)
        }
        coursePager.apply {
            isUserInputEnabled = false
            adapter = pagerAdapter
            offscreenPageLimit = 3
            registerOnPageChangeCallback(
                pageChangeCallback { pos, _, _ ->
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
        val badge: BadgeDrawable? = myCourseTabs.getTabAt(3)?.orCreateBadge
        badge?.isVisible = true
        TabLayoutMediator(myCourseTabs, coursePager) { tab, position ->
            tab.text = resources.getStringArray(R.array.tab_titles)[position]
            coursePager.setCurrentItem(tab.position, true)
        }.attach()
        // TODO(#1): Fix this hack
        Handler().postDelayed(
            {
                coursePager.setCurrentItem(intent.getIntExtra(TAB_POS, 0), true)
            },
            100
        )
    }

    fun showFab() {
        if (fab.visibility == View.GONE)
            fab.animateVisibility(View.VISIBLE)
    }

    fun hideFab() {
        if (fab.visibility == View.VISIBLE)
            fab.animateVisibility(View.GONE)
    }

    override fun onRefresh() {
        viewModel.fetchSections(true)
    }

    companion object {

        fun createMyCourseActivityIntent(context: Context, attemptId: String, name: String = "", pos: Int = 0): Intent {
            return context.intentFor<MyCourseActivity>(COURSE_NAME to name, RUN_ATTEMPT_ID to attemptId, TAB_POS to pos).singleTop()
        }
    }
}
