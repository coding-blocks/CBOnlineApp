package com.codingblocks.cbonlineapp.course

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.invoke
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.auth.LoginActivity
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.baseclasses.STATE
import com.codingblocks.cbonlineapp.commons.InstructorListAdapter
import com.codingblocks.cbonlineapp.course.batches.CourseTierFragment
import com.codingblocks.cbonlineapp.course.batches.RUNTIERS
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
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.onlineapi.ErrorStatus
import com.codingblocks.onlineapi.models.Tags
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tables.TableTheme
import kotlinx.android.synthetic.main.activity_course.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
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
    val loadingDialog: AlertDialog by lazy {
        getLoadingDialog()
    }
    private val viewModel by viewModel<CourseViewModel>()

    private val projectAdapter = CourseProjectAdapter()
    private val instructorAdapter = InstructorListAdapter()
    private val courseSectionListAdapter = CourseSectionListAdapter()
    private val courseCardListAdapter = CourseListAdapter()
    private lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener
    private var youtubePlayer: YouTubePlayer? = null

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            // Handle the Intent
        }
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

    var endLink:String = ""
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
            endLink = course.slug.toString()
            showTags(course.tags)
            val tableTheme:TableTheme = TableTheme.create(this).asBuilder()
                .tableBorderColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .build()
            val markWon = Markwon.builder(this)
                .usePlugin(TablePlugin.create(tableTheme))
                .build()
            courseSummaryTv.post {
                markWon.setMarkdown(courseSummaryTv, course.summary)
            }

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
            course.getTrialRun(RUNTIERS.LITE.name)?.let {
                trialBtn.setOnClickListener { _ ->
                    viewModel.enrollTrial(it.id)
                }
            }
            course.getContentRun(RUNTIERS.PREMIUM.name)?.let {
                it.sections?.let { sectionList -> viewModel.fetchSections(sectionList) }
                val price = it.price.toInt()
                if (price < 10) {
                    goodiesImg.isVisible = false
                }
            }
            instructorAdapter.submitList(course.instructors)
        }

        viewModel.projects.observer(this) { projects ->
            projectsTv.isVisible = !projects.isNullOrEmpty()
            projectAdapter.submitList(projects)
        }

        viewModel.sections.observer(this) { sections ->
            courseSectionListAdapter.submitList(sections.take(5))
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
                        if (it) {
                            startForResult(intentFor<LoginActivity>())
                        }
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
        viewModel.enrollTrialProgress.observer(this) { status ->
            when (status!!) {
                STATE.LOADING -> loadingDialog.show()
                STATE.ERROR -> loadingDialog.dismiss()
                STATE.SUCCESS -> {
                    loadingDialog.hide()
                    startActivity(DashboardActivity.createDashboardActivityIntent(this, true))
                    finish()
                }
            }
        }

        viewAllTv.setOnClickListener {
            val courseSectionAllFragment = CourseSectionAllFragment()

            courseSectionAllFragment.show(supportFragmentManager, "courseSectionAllFragment")
        }

        batchBtn.setOnClickListener {
            val courseTierFragment = CourseTierFragment()
            courseTierFragment.show(supportFragmentManager, "CourseTierFragment")
        }
    }

    var tagsList = ArrayList<String>()
    private fun showTags(tags: ArrayList<Tags>?) {
        tagsList.clear()
        courseChipsGroup.removeAllViews()
        with(!tags.isNullOrEmpty()) {
            topicsTv.isVisible = this
            courseChipsGroup.isVisible = this
        }
        tags?.take(5)?.forEach {
            val chip = Chip(this)
            it.name?.let { it1 -> tagsList.add(it1) }
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
            share("Check out the course *$title* by Coding Blocks!\n\n" +
                shortTv.text + "\n\n" +
                "Major topics covered: \n" +
                tagsList.joinToString( separator = "\n", limit = 5 ) + "\n\n" +
                "https://online.codingblocks.com/courses/$endLink/")
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

    override fun onDestroy() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
        super.onDestroy()
    }
}
