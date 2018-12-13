package com.codingblocks.cbonlineapp.activities

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadrosid.svgloader.SvgLoader
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.ProgressBarAnimation
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.adapters.SectionsDataAdapter
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Sections
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_course.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast


class CourseActivity : AppCompatActivity(), AnkoLogger {

    lateinit var skeletonScreen: SkeletonScreen
    lateinit var courseId: String
    var runId: String = ""
    lateinit var courseName: String
    lateinit var progressBar: Array<ProgressBar?>

    companion object {
        val YOUTUBE_API_KEY = "AIzaSyAqdhonCxTsQ5oQ-tyNaSgDJWjEM7UaEt4"
    }

    private lateinit var youtubePlayerInit: YouTubePlayer.OnInitializedListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        courseId = intent.getStringExtra("courseId")
        courseName = intent.getStringExtra("courseName")
        val image = intent.getStringExtra("courseLogo")
        SvgLoader.pluck()
                .with(this)
                .load(image, coursePageLogo)
        title = courseName
        coursePageTitle.text = courseName


        progressBar = arrayOf(courseProgress1, courseProgress2, courseProgress3, courseProgress4, courseProgress5)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        skeletonScreen = Skeleton.bind(courseRootView)
                .shimmer(true)
                .angle(20)
                .duration(1200)
                .load(R.layout.item_skeleton_course)
                .show()


        val service = Clients.onlineV2JsonApi

        Clients.onlineV2JsonApi.courseById(courseId).enqueue(retrofitCallback { t, resp ->
            resp?.body()?.let { course ->
                skeletonScreen.hide()
                coursePageSubtitle.text = course.subtitle
                coursePageSummary.text = course.summary
                runId = course.runs!![0].id!!
                info { runId }
                trialBtn.setOnClickListener {
                    Clients.api.enrollTrial(course.runs!![0].id!!).enqueue(retrofitCallback { throwable, response ->

                    })
                }
                buyBtn.setOnClickListener {
                    Clients.api.addToCart(course.runs!![0].id!!).enqueue(retrofitCallback { throwable, response ->
                        val builder = CustomTabsIntent.Builder().enableUrlBarHiding().setToolbarColor(resources.getColor(R.color.colorPrimaryDark))
                        val customTabsIntent = builder.build()
                        customTabsIntent.launchUrl(this, Uri.parse("https://dukaan.codingblocks.com/mycart"))

                    })
                }
//                showPromoVideo(course.promoVideo)
                fetchRating()
                val sections = course.runs?.get(0)?.sections
                val sectionsList = ArrayList<Sections>()
                val sectionAdapter = SectionsDataAdapter(ArrayList())
                rvExpendableView.layoutManager = LinearLayoutManager(this)
                rvExpendableView.adapter = sectionAdapter
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
                                sectionAdapter.setData(sectionsList)

                            }
                        } else {
                            toast("Error ${response.code()}")
                        }
                    }
                }
            }
        })
//        fetchRating()
    }

    private fun showPromoVideo(promoVideo: String?) {

        youtubePlayerInit = object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
            }

            override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, youtubePlayerInstance: YouTubePlayer?, p2: Boolean) {
                if (!p2) {
                    youtubePlayerInstance?.loadVideo(MediaUtils.getYotubeVideoId(promoVideo!!))
                }
            }
        }
        val youTubePlayerSupportFragment = supportFragmentManager.findFragmentById(R.id.displayYoutubeVideo) as YouTubePlayerSupportFragment?
        youTubePlayerSupportFragment!!.initialize(YOUTUBE_API_KEY, youtubePlayerInit)
    }

    private fun fetchRating() {
        Clients.api.getCourseRating(courseId).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {
                it?.apply {
                    coursePageRatingCountTv.text = "$count Rating"
                    coursePageRatingTv.text = "$rating out of 5 stars"
                    coursePageRatingBar.rating = rating.toFloat()
                    for (i in 0 until progressBar.size) {
                        progressBar[i]?.max = it.count * 1000
                        progressBar[i]?.progress = it.stats[i].toInt() * 1000
                        val anim = ProgressBarAnimation(progressBar[i], 0F, it.stats[i].toInt() * 1000F)
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
}
