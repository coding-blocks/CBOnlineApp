package com.codingblocks.cbonlineapp.course

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.course.batches.BatchesAdapter
import com.codingblocks.cbonlineapp.insturctors.InstructorListAdapter
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.DividerItemDecorator
import com.codingblocks.cbonlineapp.util.MediaUtils.getYotubeVideoId
import com.codingblocks.cbonlineapp.util.UNAUTHORIZED
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.onlineapi.ErrorStatus
import com.codingblocks.onlineapi.models.Tags
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.activity_course.*
import org.jetbrains.anko.AnkoLogger
import org.koin.androidx.viewmodel.ext.android.viewModel


class CourseActivity : AppCompatActivity(), AnkoLogger {

    private val courseId by lazy {
        intent.getStringExtra("courseId")
    }
    private val courseName by lazy {
        intent.getStringExtra("courseName")
    }
    private lateinit var batchAdapter: BatchesAdapter
    private val batchSnapHelper: SnapHelper = LinearSnapHelper()

    private val projectAdapter = CourseProjectAdapter()
    private val instructorAdapter = InstructorListAdapter()
    private val courseSectionListAdapter = CourseSectionListAdapter()

    private val viewModel by viewModel<CourseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        viewModel.id = "17"
        viewModel.fetchCourse()
        lifecycle.addObserver(youtubePlayerView)

        courseProjectsRv.apply {
            setRv(this@CourseActivity, true)
            adapter = projectAdapter
        }
        courseInstructorRv.apply {
            setRv(this@CourseActivity)
            adapter = instructorAdapter
        }
        courseContentRv.apply {
            setRv(this@CourseActivity, true)
            adapter = courseSectionListAdapter
        }

        viewModel.course.observer(this) { course ->
            showTags(course.runs?.first()?.tags)
            courseSummaryTv.text = course.summary
            courseTitle.text = course.title
            shortTv.text = course.subtitle
            courseLogo.loadImage(course.logo)
            setYoutubePlayer(course.promoVideo)
            viewModel.fetchProjects(course.projects)
            viewModel.fetchSections(course.runs?.first()?.sections)
            instructorAdapter.submitList(course.instructors)
        }

        viewModel.projects.observer(this) { projects ->
            projectsTv.isVisible = !projects.isNullOrEmpty()
            projectAdapter.submitList(projects)
        }

        viewModel.sections.observer(this) { sections ->
            courseSectionListAdapter.submitList(sections)
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
                    Snackbar.make(courseRootView, it, Snackbar.LENGTH_INDEFINITE)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                        .setAction("Retry") {
                            viewModel.fetchCourse()
                        }
                        .show()
                }
            }
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
                ResourcesCompat.getFont(this.applicationContext, R.font.gilroy_medium)
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
}


