package com.codingblocks.cbonlineapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.API.Client
import com.codingblocks.cbonlineapp.Utils.retrofitcallback

class CourseActivity : AppCompatActivity() {

    lateinit var courseId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)

        courseId = intent.getStringExtra("courseId")

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Client.api.getCourse(courseId).enqueue(retrofitcallback { t, resp ->
            resp?.body()?.let {

            }
        })
    }
}
