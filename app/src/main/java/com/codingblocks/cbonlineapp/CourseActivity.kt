package com.codingblocks.cbonlineapp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.Transformation
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ahmadrosid.svgloader.SvgLoader
import com.codingblocks.cbonlineapp.Utils.ProgressBarAnimation
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
import com.codingblocks.cbonlineapp.adapters.SectionsDataAdapter
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Sections
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_course.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class CourseActivity : AppCompatActivity(), AnkoLogger {

    lateinit var skeletonScreen: SkeletonScreen
    lateinit var courseId: String
    lateinit var courseName: String
    lateinit var progressBar: Array<ProgressBar?>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)

        courseId = intent.getStringExtra("courseId")
        courseName = intent.getStringExtra("courseName")
        title = courseName


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



        Clients.onlineV2PublicClient.courseById(courseId).enqueue(retrofitcallback { t, resp ->
            resp?.body()?.let { it ->
                skeletonScreen.hide()
                coursePageTitle.text = courseName
                coursePageSubtitle.text = it.subtitle
                coursePageSummary.text = it.summary
                SvgLoader.pluck()
                        .with(this)
                        .load(it.logo, coursePageLogo)
                showpromoVideo(it.promoVideo)
                fetchRating()
                val sections = it.runs?.get(0)?.sections
                val sectionsList = ArrayList<Sections>()
                val sectionAdapter = SectionsDataAdapter(ArrayList())
                rvExpendableView.layoutManager = LinearLayoutManager(this)
                rvExpendableView.adapter = sectionAdapter
                for (item in sections!!) {
                    Clients.onlineV2PublicClient.getSections(item.id!!).enqueue(retrofitcallback { throwable, response ->
                        response?.body()?.let {
                            info { it.toString() }
                            sectionsList.add(it)
                        }

                    })
                }
                Handler().postDelayed(
                        {
                            sectionAdapter.setData(sectionsList)
                        }, 3000
                )
            }
        })


//        fetchRating()


    }

    private fun showpromoVideo(promoVideo: String?) {

        val frameVideo = "<html><iframe width=\"420\" height=\"315\" src=\"$promoVideo\" frameborder=\"0\" allowfullscreen></iframe></body></html>"

        displayYoutubeVideo.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }
        }
        val webSettings = displayYoutubeVideo.settings
        webSettings.javaScriptEnabled = true
        displayYoutubeVideo.loadData(frameVideo, "text/html", "utf-8")

    }

    fun fetchRating() {
        Clients.api.getCourseRating(courseId).enqueue(retrofitcallback { throwable, response ->
            response?.body().let { it ->

                coursePageRatingCountTv.text = it?.count.toString() + " Rating"
                coursePageRatingTv.text = it?.rating + " out of 5 stars"
                coursePageRatingBar.rating = it?.rating?.toFloat()!!
                for (i in 0 until progressBar.size) {
                    progressBar[i]?.max = it.count * 1000
                    progressBar[i]?.progress = it.stats[i].toInt() * 1000
                    val anim = ProgressBarAnimation(progressBar[i], 0F, it.stats[i].toInt() * 1000F)
                    anim.duration = 1500
                    progressBar[i]?.startAnimation(anim)
                }

            }
        })
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }


}
