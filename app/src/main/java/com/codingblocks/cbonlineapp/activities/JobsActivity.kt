package com.codingblocks.cbonlineapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.getDate
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.onlineapi.Clients

class JobsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jobs)

        Clients.onlineV2JsonApi.getJobs(getDate(),
            getDate()).enqueue(retrofitCallback { throwable, response ->
            response?.body().let {

            }
        })
    }
}
