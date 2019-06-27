package com.codingblocks.cbonlineapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.getDate
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.viewmodels.JobsViewModel
import com.codingblocks.onlineapi.Clients
import org.koin.androidx.viewmodel.ext.android.viewModel

class JobsActivity : AppCompatActivity() {

    private val viewModel by viewModel<JobsViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jobs)

        viewModel.getJobs()
    }
}
