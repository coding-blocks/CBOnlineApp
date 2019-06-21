package com.codingblocks.cbonlineapp.activities

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.BatchesAdapter
import com.codingblocks.cbonlineapp.adapters.InstructorDataAdapter
import com.codingblocks.cbonlineapp.adapters.SectionsDataAdapter
import com.codingblocks.cbonlineapp.database.models.Instructor
import com.codingblocks.cbonlineapp.extensions.loadSvg
import com.codingblocks.cbonlineapp.extensions.observeOnce
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.OnCartItemClickListener
import com.codingblocks.cbonlineapp.util.ProgressBarAnimation
import com.codingblocks.cbonlineapp.viewmodels.CourseViewModel
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Sections
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.squareup.picasso.Picasso
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_course.batchRv
import kotlinx.android.synthetic.main.activity_course.buyBtn
import kotlinx.android.synthetic.main.activity_course.coursePageLogo
import kotlinx.android.synthetic.main.activity_course.coursePageMentors
import kotlinx.android.synthetic.main.activity_course.coursePageRatingBar
import kotlinx.android.synthetic.main.activity_course.coursePageRatingCountTv
import kotlinx.android.synthetic.main.activity_course.coursePageRatingTv
import kotlinx.android.synthetic.main.activity_course.coursePageSubtitle
import kotlinx.android.synthetic.main.activity_course.coursePageSummary
import kotlinx.android.synthetic.main.activity_course.coursePageTitle
import kotlinx.android.synthetic.main.activity_course.coursePagevtags
import kotlinx.android.synthetic.main.activity_course.courseProgress1
import kotlinx.android.synthetic.main.activity_course.courseProgress2
import kotlinx.android.synthetic.main.activity_course.courseProgress3
import kotlinx.android.synthetic.main.activity_course.courseProgress4
import kotlinx.android.synthetic.main.activity_course.courseProgress5
import kotlinx.android.synthetic.main.activity_course.courseRootView
import kotlinx.android.synthetic.main.activity_course.instructorRv
import kotlinx.android.synthetic.main.activity_course.rvExpendableView
import kotlinx.android.synthetic.main.activity_course.scrollView
import kotlinx.android.synthetic.main.activity_course.tagsChipgroup
import kotlinx.android.synthetic.main.activity_course.tagstv
import kotlinx.android.synthetic.main.activity_course.toolbar
import kotlinx.android.synthetic.main.activity_course.trialBtn
import kotlinx.android.synthetic.main.bottom_cart_sheet.bottom_sheet
import kotlinx.android.synthetic.main.bottom_cart_sheet.checkoutBtn
import kotlinx.android.synthetic.main.bottom_cart_sheet.continueBtn
import kotlinx.android.synthetic.main.bottom_cart_sheet.newImage
import kotlinx.android.synthetic.main.bottom_cart_sheet.newTitle
import kotlinx.android.synthetic.main.bottom_cart_sheet.oldTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class CourseActivity : AppCompatActivity(), AnkoLogger {
    lateinit var courseId: String
    lateinit var courseName: String
    private lateinit var skeletonScreen: SkeletonScreen
    private lateinit var rvExpandableskeleton: SkeletonScreen
    private lateinit var progressBar: Array<ProgressBar?>
    private lateinit var batchAdapter: BatchesAdapter
    private lateinit var instructorAdapter: InstructorDataAdapter
    private lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener
    private val batchSnapHelper: SnapHelper = LinearSnapHelper()
    private val sectionAdapter = SectionsDataAdapter(ArrayList())

    private val viewModel by viewModel<CourseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        courseId = intent.getStringExtra("courseId")
        courseName = intent.getStringExtra("courseName") ?: ""
        val image = intent.getStringExtra("courseLogo") ?: ""
        setImageAndTitle(image, courseName)

        init()
    }

    private fun setImageAndTitle(image: String, courseName: String) {
        if (image.takeLast(3) == "png")
            Picasso.with(this).load(image).into(coursePageLogo)
        else
            coursePageLogo.loadSvg(image)
        title = courseName
        coursePageTitle.text = courseName
    }

    private fun init() {
        progressBar = arrayOf(
            courseProgress1,
            courseProgress2,
            courseProgress3,
            courseProgress4,
            courseProgress5
        )
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        skeletonScreen = Skeleton.bind(courseRootView)
            .shimmer(true)
            .angle(20)
            .duration(1200)
            .load(R.layout.item_skeleton_course)
            .show()

        viewModel.sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        viewModel.sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN

        fetchCourse()
    }

    private fun showBottomSheet(newId: String, newName: String) {
        viewModel.image.observer(this) {
            if (it.takeLast(3) == "png")
                Picasso.with(this@CourseActivity).load(it).into(newImage)
            else
                newImage.loadSvg(it ?: "")
        }
        viewModel.name.observer(this) {
            oldTitle.text = it
            newTitle.text = newName
        }
        checkoutBtn.setOnClickListener {
            viewModel.sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            Components.openChrome(this, "https://dukaan.codingblocks.com/mycart")
        }
        continueBtn.setOnClickListener {
            viewModel.sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.clearCartProgress.observeOnce {
                if (it) addToCart(newId, newName)
                else toast("Error in Clearing Cart, please try again")
            }
            viewModel.clearCart()
        }
        viewModel.getCart()
    }

    private fun fetchInstructors(id: String) {
        instructorAdapter = InstructorDataAdapter(ArrayList())

        instructorRv.layoutManager = LinearLayoutManager(this)
        instructorRv.adapter = instructorAdapter

        viewModel.getInstructorWithCourseId(id).observer(this) {
            instructorAdapter.setData(it as ArrayList<Instructor>)
            var instructors = "Mentors: "
            for (i in 0 until it.size) {
                if (i == 0) {
                    instructors += it[i].name
                } else if (i == 1) {
                    instructors += ", ${it[i].name}"
                } else if (i >= 2) {
                    instructors += "+ " + (it.size - 2) + " more"
                    break
                }
                coursePageMentors.text = instructors
            }
        }
    }

    private fun fetchCourse() {
        viewModel.fetchedCourse.observeOnce { course ->
            skeletonScreen.hide()

            fetchInstructors(course.id)

            batchAdapter = BatchesAdapter(ArrayList(), object : OnCartItemClickListener {
                override fun onItemClick(id: String, name: String) {
                    addToCart(id, name)
                }
            })
            batchRv.layoutManager =
                LinearLayoutManager(this@CourseActivity, LinearLayoutManager.HORIZONTAL, false)
            batchRv.adapter = batchAdapter
            batchSnapHelper.attachToRecyclerView(batchRv)
            setImageAndTitle(course.logo, course.title)
            coursePageSubtitle.text = course.subtitle
            coursePageSummary.loadMarkdown(course.summary)
            trialBtn.setOnClickListener {
                if (course.runs != null) {
                    viewModel.enrollTrialProgress.observeOnce {
                        Components.showconfirmation(this@CourseActivity, "trial")
                    }
                    viewModel.enrollTrial(course.runs?.get(0)?.id ?: "")
                }
            }
            course.runs?.let { batchAdapter.setData(it) }
            buyBtn.setOnClickListener {
                if (course.runs != null) {
                    focusOnView(scrollView, batchRv)
                } else {
                    toast("No available runs right now ! Please check back later")
                }
            }
            fetchTags(course)
            showPromoVideo(course.promoVideo)
            fetchRating(course.id)
            if (!course.runs.isNullOrEmpty()) {
                val sections = course.runs?.get(0)?.sections
                val sectionsList = ArrayList<Sections>()
                rvExpendableView.layoutManager = LinearLayoutManager(this@CourseActivity)
                rvExpendableView.adapter = sectionAdapter
                rvExpandableskeleton = Skeleton.bind(rvExpendableView)
                    .adapter(sectionAdapter)
                    .shimmer(true)
                    .angle(20)
                    .frozen(true)
                    .duration(1200)
                    .count(4)
                    .load(R.layout.item_skeleton_section_card)
                    .show()
                runOnUiThread {
                    sections?.forEachIndexed { index, section ->
                        GlobalScope.launch(Dispatchers.Main) {
                            val response2 = viewModel.getSectionsFromID(section.id)
                            if (response2.isSuccessful) {
                                val value = response2.body()
                                value?.order = index
                                if (value != null) {
                                    sectionsList.add(value)
                                }
                                if (sectionsList.size == sections.size) {
                                    sectionsList.sortBy { it.order }
                                    rvExpandableskeleton.hide()
                                    sectionAdapter.setData(sectionsList)
                                }
                            } else {
                                toast("Error ${response2.code()}")
                            }
                        }
                    }
                }
            }
        }
        viewModel.getCourse(courseId)
    }

    private fun fetchTags(course: Course) {
        course.runs?.forEach { singleCourse ->
            if (singleCourse.tags?.size == 0) {
                tagstv.visibility = View.GONE
                coursePagevtags.visibility = View.GONE
                tagsChipgroup.visibility = View.GONE
            } else {
                tagstv.visibility = View.VISIBLE
                coursePagevtags.visibility = View.VISIBLE
                tagsChipgroup.visibility = View.VISIBLE

                singleCourse.tags?.forEach {
                    val chip = Chip(this)
                    chip.text = it.name
                    val typeFace: Typeface? =
                        ResourcesCompat.getFont(this.applicationContext, R.font.nunitosans_regular)
                    chip.typeface = typeFace
                    tagsChipgroup.addView(chip)
                }
            }
        }
    }

    private fun addToCart(id: String, name: String) {
        viewModel.addedToCartProgress.observeOnce {
            if (it) {
                Components.openChrome(this, "https://dukaan.codingblocks.com/mycart")
            } else {
                showBottomSheet(id, name)
            }
        }
        viewModel.addToCart(id)
    }

    private fun showPromoVideo(promoVideo: String) {
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
                if (!p2) {
                    youtubePlayerInstance?.loadVideo(MediaUtils.getYotubeVideoId(promoVideo))
                }
            }
        }
        val youTubePlayerSupportFragment =
            supportFragmentManager.findFragmentById(R.id.displayYoutubeVideo) as YouTubePlayerSupportFragment?
        youTubePlayerSupportFragment?.initialize(BuildConfig.YOUTUBE_KEY, youtubePlayerInit)
    }

    private fun fetchRating(id: String) {
        viewModel.courseRating.observeOnce {
            it?.apply {
                coursePageRatingCountTv.text = "$count Rating"
                coursePageRatingTv.text = "$rating out of 5 stars"
                coursePageRatingBar.rating = rating.toFloat()
                for (i in 0 until progressBar.size) {
                    progressBar[i]?.max = it.count * 1000
                    progressBar[i]?.progress = it.stats[i].toInt() * 1000
                    val anim =
                        ProgressBarAnimation(progressBar[i], 0F, it.stats[i].toInt() * 1000F)
                    anim.duration = 1500
                    progressBar[i]?.startAnimation(anim)
                }
            }
        }
        viewModel.getCourseRating(id)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    private fun focusOnView(scroll: NestedScrollView, view: View) {
        Handler().post {
            val vTop = view.top
            val vBottom = view.bottom
            val sWidth = scroll.width
            scroll.smoothScrollTo(0, (vTop + vBottom - sWidth) / 2)
        }
    }

    override fun onBackPressed() {
        if (viewModel.sheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
            viewModel.sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            super.onBackPressed()
        }
    }
}
