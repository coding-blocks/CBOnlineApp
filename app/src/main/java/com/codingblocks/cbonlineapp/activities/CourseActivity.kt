package com.codingblocks.cbonlineapp.activities

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.codingblocks.cbonlineapp.BuildConfig
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.ProgressBarAnimation
import com.codingblocks.cbonlineapp.adapters.BatchesAdapter
import com.codingblocks.cbonlineapp.adapters.InstructorDataAdapter
import com.codingblocks.cbonlineapp.adapters.SectionsDataAdapter
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.models.Instructor
import com.codingblocks.cbonlineapp.extensions.loadSvg
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.util.Components
import com.codingblocks.cbonlineapp.util.MediaUtils
import com.codingblocks.cbonlineapp.util.OnCartItemClickListener
import com.codingblocks.onlineapi.Clients
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

class CourseActivity : AppCompatActivity(), AnkoLogger {
    lateinit var skeletonScreen: SkeletonScreen
    lateinit var rvExpandableskeleton: SkeletonScreen
    lateinit var courseId: String
    lateinit var courseName: String
    lateinit var progressBar: Array<ProgressBar?>
    private lateinit var batchAdapter: BatchesAdapter
    private lateinit var instructorAdapter: InstructorDataAdapter
    private lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener
    var sheetBehavior: BottomSheetBehavior<*>? = null
    val batchSnapHelper: SnapHelper = LinearSnapHelper()
    val sectionAdapter = SectionsDataAdapter(ArrayList())


    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }
    private val courseWithInstructorDao by lazy {
        database.courseWithInstructorDao()
    }

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


        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
        sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN


        fetchCourse()
    }

    private fun showBottomSheet(newId: String, newName: String) {
        Clients.api.getCart().enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                it?.getAsJsonArray("cartItems")!![0].asJsonObject.let { it ->
                    val image = it.get("image_url").asString
                    val name = it.get("productName").asString
                    if (image.takeLast(3) == "png")
                        Picasso.with(this).load(image).into(newImage)
                    else
                        newImage.loadSvg(image)
                    oldTitle.text = name
                    newTitle.text = newName
                    sheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED

                    checkoutBtn.setOnClickListener {
                        sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                        val builder = CustomTabsIntent.Builder().enableUrlBarHiding()
                            .setToolbarColor(resources.getColor(R.color.colorPrimaryDark))
                        val customTabsIntent = builder.build()
                        customTabsIntent.launchUrl(
                            this@CourseActivity,
                            Uri.parse("https://dukaan.codingblocks.com/mycart")
                        )
                    }
                    continueBtn.setOnClickListener {
                        sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
                        Clients.api.clearCart().enqueue(retrofitCallback { throwable, response ->
                            response?.body().let {
                                if (response?.isSuccessful!!) {
                                    addtocart(newId, newName)
                                }
                            }
                        })
                    }
                }
            }
        })
    }

    private fun fetchInstructors(id: String) {
        instructorAdapter = InstructorDataAdapter(ArrayList())

        instructorRv.layoutManager = LinearLayoutManager(this)
        instructorRv.adapter = instructorAdapter

        courseWithInstructorDao.getInstructorWithCourseId(id)
            .observe(this, Observer<List<Instructor>> {
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
            })
    }

    private fun fetchCourse() {
        val service = Clients.onlineV2JsonApi
        Clients.onlineV2JsonApi.courseById(courseId).enqueue(retrofitCallback { t, resp ->
            resp?.body()?.let { course ->
                skeletonScreen.hide()
                fetchInstructors(course.id)
                batchAdapter = BatchesAdapter(ArrayList(), object : OnCartItemClickListener {
                    override fun onItemClick(id: String, name: String) {
                        addtocart(id, name)
                    }
                })
                batchRv.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                batchRv.adapter = batchAdapter
                batchSnapHelper.attachToRecyclerView(batchRv)
                setImageAndTitle(course.logo!!, course.title!!)
                coursePageSubtitle.text = course.subtitle
                coursePageSummary.text = course.summary
                trialBtn.setOnClickListener {
                    if (course.runs != null)
                        Clients.api.enrollTrial(course.runs!![0].id!!).enqueue(retrofitCallback { throwable, response ->
                            if (response?.isSuccessful!!) {
                                Components.showconfirmation(this, "trial")
                            }
                        })
                }
                batchAdapter.setData(course.runs!!)
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
                    rvExpendableView.layoutManager = LinearLayoutManager(this)
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
                        sections!!.forEachIndexed { index, section ->
                            GlobalScope.launch(Dispatchers.Main) {
                                val request = service.getSections(section.id!!)
                                val response = request.await()
                                if (response.isSuccessful) {
                                    val value = response.body()!!
                                    value.order = index
                                    sectionsList.add(value)
                                    if (sectionsList.size == sections.size) {
                                        sectionsList.sortBy { it.order }
                                        rvExpandableskeleton.hide()
                                        sectionAdapter.setData(sectionsList)
                                    }
                                } else {
                                    toast("Error ${response.code()}")
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun fetchTags(course: Course) {

        course.runs?.forEach {
            if (it.tags?.size == 0) {
                tagstv.visibility = View.GONE
                coursePagevtags.visibility = View.GONE
                tagsChipgroup.visibility = View.GONE
            } else {
                tagstv.visibility = View.VISIBLE
                coursePagevtags.visibility = View.VISIBLE
                tagsChipgroup.visibility = View.VISIBLE

                it.tags?.forEach {
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

    private fun addtocart(id: String, name: String) {
        Clients.api.addToCart(id).enqueue(retrofitCallback { throwable, response ->
            response.let {
                if (it?.code() == 400) {
                    showBottomSheet(id, name)
                } else if (it?.isSuccessful!!) {
                    val builder = CustomTabsIntent.Builder().enableUrlBarHiding()
                        .setToolbarColor(resources.getColor(R.color.colorPrimaryDark))
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(
                        this@CourseActivity,
                        Uri.parse("https://dukaan.codingblocks.com/mycart")
                    )
                }
            }
        })
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
        if (this@CourseActivity != null)
            youTubePlayerSupportFragment?.initialize(BuildConfig.YOUTUBE_KEY, youtubePlayerInit)
    }

    private fun fetchRating(id: String) {
        Clients.api.getCourseRating(id).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
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
        })
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
        if (sheetBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            super.onBackPressed()
        }
    }
}

