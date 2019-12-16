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
        val dividerItemDecoration = DividerItemDecorator(ContextCompat.getDrawable(this, R.drawable.divider)!!)

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

//
//    private fun init() {
//        progressBar = arrayOf(
//            courseProgress1,
//            courseProgress2,
//            courseProgress3,
//            courseProgress4,
//            courseProgress5
//        )
//        setSupportActionBar(toolbar)
//        supportActionBar?.setHomeButtonEnabled(true)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        viewModel.sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
//        viewModel.sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
//
//        fetchCourse()
//    }
//
//    private fun showBottomSheet(newId: String, newName: String) {
//        viewModel.image.observer(this) {
//            newImage.loadImage(it ?: "")
//        }
//        viewModel.name.observer(this) {
//            oldTitle.text = it
//            newTitle.text = newName
//        }
//        checkoutBtn.setOnClickListener {
//            viewModel.sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
//            Components.openChrome(this, "https://dukaan.codingblocks.com/mycart")
//        }
//        continueBtn.setOnClickListener {
//            viewModel.sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
//            viewModel.clearCartProgress.observeOnce {
//                if (it) addToCart(newId, newName)
//                else toast("Error in Clearing Cart, please try again")
//            }
//            viewModel.clearCart()
//        }
//        viewModel.getCart()
//    }
//

//
//    private fun fetchCourse() {
//
//        viewModel.fetchedCourse
//            .observer(this) { course ->
//                fetchInstructors(course.id)
//                viewModel.getCourseFeatures(courseId).observer(this) {
//                    if (it.isNotEmpty())
//                        it.forEachIndexed { index, courseFeatures ->
//                            when (index) {
//                                0 -> {
//                                    feature_icon_1.loadImage(courseFeatures.icon, scale = true)
//                                    features_text_1.text = courseFeatures.text
//                                }
//                                1 -> {
//                                    feature_icon_2.loadImage(courseFeatures.icon, scale = true)
//                                    features_text_2.text = courseFeatures.text
//                                }
//                                2 -> {
//                                    feature_icon_3.loadImage(courseFeatures.icon, scale = true)
//                                    features_text_3.text = courseFeatures.text
//                                }
//                                3 -> {
//                                    feature_icon_4.loadImage(courseFeatures.icon, scale = true)
//                                    features_text_4.text = courseFeatures.text
//                                }
//                            }
//                        }
//                }
//
//                batchAdapter = BatchesAdapter(ArrayList(), object : OnCartItemClickListener {
//                    override fun onItemClick(id: String, name: String) {
//                        addToCart(id, name)
//                    }
//                })
//                batchRv.layoutManager =
//                    LinearLayoutManager(this@CourseActivity, LinearLayoutManager.HORIZONTAL, false)
//                batchRv.adapter = batchAdapter
//                batchSnapHelper.attachToRecyclerView(batchRv)
//                setImageAndTitle(course.logo, course.title)
//                coursePageSubtitle.text = course.subtitle
//                if (course.faq.isNullOrEmpty()) {
//                    faqMarkdown.isVisible = false
//                    faqTitleTv.isVisible = false
//                    faqView.isVisible = false
//                } else {
//                    faqMarkdown.loadMarkdown(course.faq)
//                }
//                coursePageSummary.loadMarkdown(course.summary)
//                trialBtn.setOnClickListener {
//                    if (course.runs != null) {
//                        viewModel.enrollTrialProgress.observeOnce {
//                            Components.showConfirmation(this@CourseActivity, "trial")
//                        }
//                        viewModel.enrollTrial(course.runs?.get(0)?.id ?: "")
//                    } else {
//                        toast("No available runs right now ! Please check back later")
//                    }
//                }
//                course.runs?.let { batchAdapter.setData(it) }
//                buyBtn.setOnClickListener {
//                    if (course.runs != null) {
//                        focusOnView(scrollView, batchRv)
//                    } else {
//                        toast("No available runs right now ! Please check back later")
//                    }
//                }
//                fetchTags(course)
//                showPromoVideo(course.promoVideo)
//                fetchRating(course.id)
//                if (!course.runs.isNullOrEmpty()) {
//                    val sections = course.runs?.get(0)?.sections
//                    val sectionsList = ArrayList<Sections>()
//                    rvExpendableView.layoutManager = LinearLayoutManager(this@CourseActivity)
//                    rvExpendableView.adapter = sectionAdapter
//                    runOnUiThread {
//                        sections?.forEachIndexed { index, section ->
//                            GlobalScope.launch(Dispatchers.IO) {
//                                val response2 = viewModel.getCourseSection(section.id)
//                                if (response2.isSuccessful) {
//                                    val value = response2.body()
//                                    value?.order = index
//                                    if (value != null) {
//                                        sectionsList.add(value)
//                                    }
//                                    if (sectionsList.size == sections.size) {
//                                        sectionsList.sortBy { it.order }
//                                        runOnUiThread {
//                                            sectionAdapter.setData(sectionsList)
//                                        }
//                                    }
//                                } else {
//                                    toast("Error ${response2.code()}")
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        viewModel.getCourse(courseId)
//    }
//
//
//    private fun addToCart(id: String, name: String) {
//        viewModel.addedToCartProgress.observeOnce {
//            if (it) {
//                Components.openChrome(this, "https://dukaan.codingblocks.com/mycart")
//            } else {
//                showBottomSheet(id, name)
//            }
//        }
//        viewModel.addToCart(id)
//    }
//

//
//    private fun fetchRating(id: String) {
//        viewModel.getCourseRating(id).observeOnce {
//            it.apply {
//                coursePageRatingCountTv.text = "$count Rating"
//                coursePageRatingTv.text = "$rating out of 5 stars"
//                coursePageRatingBar.rating = rating.toFloat()
//                for (i in 0 until progressBar.size) {
//                    progressBar[i]?.max = it.count * 1000
//                    progressBar[i]?.progress = it.stats[i].toInt() * 1000
//
//                    // Todo Add Animation on Focus
//                    //                    val anim =
//                    //                        ProgressBarAnimation(progressBar[i], 0F, it.stats[i].toInt() * 1000F)
//                    //                    anim.duration = 1500
//                    //                    progressBar[i]?.startAnimation(anim)
//                }
//            }
//        }
//        viewModel.getCourseRating(id)
//    }
//
//    override fun attachBaseContext(newBase: Context) {
//        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
//    }
//
//    private fun focusOnView(scroll: NestedScrollView, view: View) {
//        Handler().post {
//            val vTop = view.top
//            val vBottom = view.bottom
//            val sWidth = scroll.width
//            scroll.smoothScrollTo(0, (vTop + vBottom - sWidth) / 2)
//        }
//    }
//
//    override fun onBackPressed() {
//        if (viewModel.sheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
//            viewModel.sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
//        } else {
//            super.onBackPressed()
//        }
//    }

}


