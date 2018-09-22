package com.codingblocks.cbonlineapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ahmadrosid.svgloader.SvgLoader
import com.codingblocks.cbonlineapp.API.Client
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
import kotlinx.android.synthetic.main.activity_course.*
import org.jetbrains.anko.AnkoLogger


class CourseActivity : AppCompatActivity(), AnkoLogger {

    lateinit var courseId: String
    lateinit var courseName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)

        courseId = intent.getStringExtra("courseId")
        courseName = intent.getStringExtra("courseName")
        title = courseName

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Client.api.getCourse().enqueue(retrofitcallback { t, resp ->
            resp?.body()?.let {
                textView8.text = it.data.attributes.summary
                SvgLoader.pluck()
                        .with(this)
                        .load(it.data.attributes.logo, imageView4)
                textView7.text = it.data.attributes.subtitle
                textView5.text = courseName
            }
        })
    }
}
