package com.codingblocks.cbonlineapp.course

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.InstructorListAdapter
import com.codingblocks.cbonlineapp.course.batches.BatchListAdapter
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_LOGO
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.LOGO_TRANSITION_NAME
import com.codingblocks.cbonlineapp.util.MediaUtils.getYotubeVideoId
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.loadSvg
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.onlineapi.ErrorStatus
import com.codingblocks.onlineapi.models.Tags
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.activity_course.*
import kotlinx.android.synthetic.main.bottom_sheet_batch.view.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseActivity : AppCompatActivity(), AnkoLogger, AppBarLayout.OnOffsetChangedListener {

    private val courseId by lazy {
        intent.getStringExtra(COURSE_ID) ?: "17"
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
    private val batchListAdapter = BatchListAdapter()
    private val dialog by lazy { BottomSheetDialog(this) }

    private val itemClickListener: ItemClickListener by lazy {
        object : ItemClickListener {
            override fun onClick(id: String, name: String, logo: ImageView) {
                val intent = Intent(this@CourseActivity, CourseActivity::class.java)
                intent.putExtra(COURSE_ID, id)
                intent.putExtra(COURSE_LOGO, name)
                intent.putExtra(LOGO_TRANSITION_NAME, ViewCompat.getTransitionName(logo))

                val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this@CourseActivity,
                    logo,
                    ViewCompat.getTransitionName(logo)!!)
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
        lifecycle.addObserver(youtubePlayerView)
        setUpBottomSheet()

        courseProjectsRv.setRv(this@CourseActivity, projectAdapter, true)
        courseSuggestedRv.setRv(this@CourseActivity, courseCardListAdapter, orientation = RecyclerView.HORIZONTAL)
        courseInstructorRv.setRv(this@CourseActivity, instructorAdapter)
        courseContentRv.setRv(this@CourseActivity, courseSectionListAdapter, true)
        if (!courseLogoImage.isNullOrEmpty()) {
            courseLogo.transitionName = courseLogoImage
            courseLogo.loadSvg(courseLogoUrl) {
                supportStartPostponedEnterTransition()
            }
        }

        viewModel.course.observer(this) { course ->
            showTags(course.tags)
            courseSummaryTv.text = course.summary
            title = course.title
            shortTv.text = course.subtitle
            ratingBar.numStars = course.rating.toInt()
            ratingTv.text = "${course.rating}/5, ${course.reviewCount} ratings"
            if (courseLogoUrl.isNullOrEmpty()) courseLogo.loadImage(course.logo)
            courseBackdrop.loadImage(course.coverImage)
            setYoutubePlayer(course.promoVideo)
            viewModel.fetchProjects(course.projects)
            viewModel.fetchSections(course.runs?.first()?.sections)
            instructorAdapter.submitList(course.instructors)
            batchListAdapter.submitList(course.activeRuns)
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
//                    showEmptyView(internetll, emptyll, doubtShimmer)
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
        buyBtn.setOnClickListener {
            dialog.show()
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
            val typeFace =
                ResourcesCompat.getFont(this, R.font.gilroy_medium)
            chip.typeface = typeFace
            courseChipsGroup.addView(chip)
        }
    }

    private fun setYoutubePlayer(promoVideo: String) {
        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                youTubePlayer.cueVideo(getYotubeVideoId(promoVideo), 0F)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.course_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {

        else -> super.onOptionsItemSelected(item)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val alpha = (appBarLayout.totalScrollRange + verticalOffset).toFloat() / appBarLayout.totalScrollRange
        courseLogo.alpha = alpha
        shortTv.alpha = alpha
    }

    private fun setUpBottomSheet() {
        val sheetDialog = layoutInflater.inflate(R.layout.bottom_sheet_batch, null)
        sheetDialog.run {
            batchRv.setRv(this@CourseActivity, batchListAdapter)
        }
        dialog.dismissWithAnimation = true
        dialog.setContentView(sheetDialog)
    }

    override fun onBackPressed() {
//        youtubePlayerView.release()
//        supportFinishAfterTransition()
        super.onBackPressed()
    }
}
