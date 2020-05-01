package com.codingblocks.cbonlineapp.course

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.baseclasses.STATE
import com.codingblocks.cbonlineapp.commons.InstructorListAdapter
import com.codingblocks.cbonlineapp.course.batches.CourseTierFragment
import com.codingblocks.cbonlineapp.course.checkout.CheckoutActivity
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_LOGO
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.LOGO_TRANSITION_NAME
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.getLoadingDialog
import com.codingblocks.cbonlineapp.util.extensions.getSpannableSring
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.observeOnce
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.onlineapi.ErrorStatus
import com.codingblocks.onlineapi.models.Runs
import com.codingblocks.onlineapi.models.Tags
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import io.noties.markwon.Markwon
import io.noties.markwon.core.CorePlugin
import kotlinx.android.synthetic.main.activity_course.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.share
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseActivity : BaseCBActivity(), AnkoLogger, AppBarLayout.OnOffsetChangedListener {

    private val courseId by lazy {
        intent.getStringExtra(COURSE_ID)
    }
    private val courseLogoImage by lazy {
        intent.getStringExtra(LOGO_TRANSITION_NAME)
    }

    private val courseLogoUrl by lazy {
        intent.getStringExtra(COURSE_LOGO)
    }
    private val viewModel by viewModel<CourseViewModel>()

    private val projectAdapter = CourseProjectAdapter()
    private val instructorAdapter = InstructorListAdapter()
    private val courseSectionListAdapter = CourseSectionListAdapter()
    private val courseCardListAdapter = CourseListAdapter()
    private lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener
    private var youtubePlayer: YouTubePlayer? = null
    private val loadingDialog by lazy {
        getLoadingDialog()
    }

    private val itemClickListener: ItemClickListener by lazy {
        object : ItemClickListener {
            override fun onClick(id: String, name: String, logo: ImageView) {
                val intent = Intent(this@CourseActivity, CourseActivity::class.java)
                intent.putExtra(COURSE_ID, id)
                intent.putExtra(COURSE_LOGO, name)
                intent.putExtra(LOGO_TRANSITION_NAME, ViewCompat.getTransitionName(logo))

                val options: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@CourseActivity,
                        logo,
                        ViewCompat.getTransitionName(logo)!!
                    )
                startActivity(intent, options.toBundle())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        supportPostponeEnterTransition()
        setToolbar(courseToolbar)
        viewModel.id = courseId
        viewModel.fetchCourse()

        courseProjectsRv.setRv(this@CourseActivity, projectAdapter, true)
        courseSuggestedRv.setRv(
            this@CourseActivity,
            courseCardListAdapter,
            orientation = RecyclerView.HORIZONTAL,
            space = 28f
        )
        courseInstructorRv.setRv(this@CourseActivity, instructorAdapter)
        courseContentRv.setRv(this@CourseActivity, courseSectionListAdapter, true)
        if (!courseLogoImage.isNullOrEmpty()) {
            courseLogo.transitionName = courseLogoImage
            courseLogo.loadImage(courseLogoUrl) {
                if (it)
                    supportStartPostponedEnterTransition()
                else {
                    toast("No Internet Connection Available")
                    onBackPressed()
                }
            }
        }

        viewModel.course.observer(this) { course ->
            showTags(course.tags)
            val markWon = Markwon.builder(this)
                .usePlugin(CorePlugin.create())
                .build()
            markWon.setMarkdown(courseSummaryTv, course.summary)
            course.faq?.let {
                faqTv.isVisible = true
                courseFaqTv.isVisible = true
                markWon.setMarkdown(courseFaqTv, it)
            }
            title = course.title
            shortTv.text = course.subtitle
            ratingBar.rating = course.rating
            ratingTv.text =
                getSpannableSring("${course.rating}/5.0, ", "${course.reviewCount} ratings")
            if (courseLogoUrl.isNullOrEmpty()) courseLogo.loadImage(course.logo)
            courseBackdrop.loadImage(course.coverImage ?: "")
            setYoutubePlayer(course.promoVideo ?: "")
            viewModel.fetchProjects(course.projects)
            if (!course.runs.isNullOrEmpty()) viewModel.fetchSections(course.runs?.first()?.sections)
            instructorAdapter.submitList(course.instructors)
            if (!course.activeRuns.isNullOrEmpty())
                course.activeRuns?.first()?.let {
                    setRun(it)
                }
        }

        viewModel.projects.observer(this) { projects ->
            projectsTv.isVisible = !projects.isNullOrEmpty()
            projectAdapter.submitList(projects)
        }

        viewModel.sections.observer(this) { sections ->
            courseSectionListAdapter.submitList(sections)
        }

        viewModel.suggestedCourses.observer(this) { courses ->
            courseCardListAdapter.submitList(courses)
        }

        viewModel.errorLiveData.observer(this) {
            when (it) {
                ErrorStatus.NO_CONNECTION -> {
                    showOffline()
                }
                ErrorStatus.UNAUTHORIZED -> {
                    Components.showConfirmation(this, UNAUTHORIZED) {
                        finish()
                    }
                }
                ErrorStatus.TIMEOUT -> {
//                    Snackbar.make(courseRootView, it, Snackbar.LENGTH_INDEFINITE)
//                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
//                        .setAction("Retry") {
//                            viewModel.fetchCourse()
//                        }
//                        .show()
                }
            }
        }
        appbar.addOnOffsetChangedListener(this)

        courseCardListAdapter.onItemClick = itemClickListener

        viewModel.addedToCartProgress.observer(this) {
            when (it!!) {
                STATE.LOADING -> loadingDialog.show()
                STATE.ERROR -> loadingDialog.dismiss()
                STATE.SUCCESS -> {
                    loadingDialog.hide()
                    startActivity<CheckoutActivity>()
                }
            }

        }
        viewModel.enrollTrialProgress.observeOnce { status ->
            when (status!!) {
                STATE.LOADING -> loadingDialog.show()
                STATE.ERROR -> loadingDialog.dismiss()
                STATE.SUCCESS -> {
                    loadingDialog.hide()
                    startActivity<DashboardActivity>()
                    finish()
                }
            }
        }

        viewAllTv.setOnClickListener {
            // Bottom Sheet
            val courseSectionAllFragment = CourseSectionAllFragment()
            val bundle = Bundle()
            bundle.putString(COURSE_ID, courseId)
            courseSectionAllFragment.arguments = bundle

            courseSectionAllFragment.show(supportFragmentManager, "courseSectionAllFragment")
        }

        batchBtn.setOnClickListener {
            val courseTierFragment = CourseTierFragment()
            val bundle = Bundle()
            bundle.putString(COURSE_ID, courseId)
            courseTierFragment.arguments = bundle

            courseTierFragment.show(supportFragmentManager, "CourseTierFragment")
        }


    }

    @Deprecated("Tier Based")
    private fun setRun(it: Runs) {

        val price = it.price.toInt()
        if (price < 10) {
            goodiesImg.isVisible = false
        }
        trialBtn.setOnClickListener { _ ->
            viewModel.enrollTrial(it.id)
        }
    }

    private fun showTags(tags: ArrayList<Tags>?) {
        with(!tags.isNullOrEmpty()) {
            topicsTv.isVisible = this
            courseChipsGroup.isVisible = this
        }
        tags?.take(5)?.forEach {
            val chip = Chip(this)
            chip.text = it.name
            val font = Typeface.createFromAsset(assets, "fonts/gilroy_medium.ttf")
            chip.typeface = font
            courseChipsGroup.addView(chip)
        }
    }

    private fun setYoutubePlayer(youtubeUrl: String) {
        youtubePlayerInit = object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
            }

            override fun onInitializationSuccess(
                p0: YouTubePlayer.Provider?,
                youtubePlayerInstance: YouTubePlayer?,
                p2: Boolean
            ) {
                youtubePlayer = youtubePlayerInstance
                if (!p2) {
                    val url = if (youtubeUrl.isNotEmpty()) MediaUtils.getYoutubeVideoId(youtubeUrl) else ""
                    youtubePlayerInstance?.cueVideo(url)
                }
            }
        }
        val youTubePlayerSupportFragment =
            supportFragmentManager.findFragmentById(R.id.youtubePlayerView) as YouTubePlayerSupportFragment?
        youTubePlayerSupportFragment?.initialize(BuildConfig.YOUTUBE_KEY, youtubePlayerInit)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.course_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.share -> {
            share("http://online.codingblocks.com/app/$courseId")
            true
        }
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val alpha =
            (appBarLayout.totalScrollRange + verticalOffset).toFloat() / appBarLayout.totalScrollRange
        courseLogo.alpha = alpha
        shortTv.alpha = alpha
        ratingBar.alpha = alpha
        ratingTv.alpha = alpha
    }

    override fun onBackPressed() {
        youtubePlayer?.release()
        super.onBackPressed()
    }
}
