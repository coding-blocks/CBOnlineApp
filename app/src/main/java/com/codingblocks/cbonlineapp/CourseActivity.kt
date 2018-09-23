package com.codingblocks.cbonlineapp

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.ahmadrosid.svgloader.SvgLoader
import com.codingblocks.cbonlineapp.API.Client
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import kotlinx.android.synthetic.main.activity_course.*
import org.jetbrains.anko.AnkoLogger


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

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        skeletonScreen = Skeleton.bind(courseRootView)
                .shimmer(true)
                .angle(20)
                .duration(1200)
                .load(R.layout.item_skeleton_course)
                .show()

        Client.api.getCourse(courseId).enqueue(retrofitcallback { t, resp ->
            resp?.body()?.let { it ->
                skeletonScreen.hide()
                coursePageTitle.text = courseName
                coursePageSubtitle.text = it.data.attributes.subtitle
                coursePageSummary.text = it.data.attributes.summary
                SvgLoader.pluck()
                        .with(this)
                        .load(it.data.attributes.logo, coursePageLogo)

                Client.api.getCourseRating(courseId).enqueue(retrofitcallback { throwable, response ->
                    response?.body().let { innerit ->

                        coursePageRatingCountTv.text = innerit?.count.toString() + " Rating"
                        coursePageRatingTv.text = innerit?.rating + " out of 5 stars"
                        coursePageRatingBar.rating = innerit?.rating?.toFloat()!!
                        for (i in 0 until progressBar.size) {
                            progressBar[i]?.max = innerit.count * 1000
                            progressBar[i]?.progress = innerit.stats[i].toInt() * 1000
                            val anim = ProgressBarAnimation(progressBar[i], 0F, innerit.stats[i].toInt() * 1000F)
                            anim.duration = 1500
                            progressBar[i]?.startAnimation(anim)
                        }

                    }
                })
            }
        })
    }

    inner class ProgressBarAnimation(private val progressBar: ProgressBar?, private val from: Float, private val to: Float) : Animation() {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            super.applyTransformation(interpolatedTime, t)
            val value = from + (to - from) * interpolatedTime
            progressBar!!.progress = value.toInt()
        }

    }
}
