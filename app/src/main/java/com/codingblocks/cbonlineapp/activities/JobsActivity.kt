package com.codingblocks.cbonlineapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.adapters.JobsAdapter
import com.codingblocks.cbonlineapp.commons.JobsDiffCallback
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.viewmodels.JobsViewModel
import kotlinx.android.synthetic.main.activity_jobs.rvJobs
import org.koin.androidx.viewmodel.ext.android.viewModel

class JobsActivity : AppCompatActivity() {

    private val viewModel by viewModel<JobsViewModel>()
    private val jobsAdapter = JobsAdapter(JobsDiffCallback())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jobs)

        rvJobs.layoutManager = LinearLayoutManager(this)
        rvJobs.adapter = jobsAdapter

        viewModel.getJobs()

        viewModel.getAllJobs().observer(this) {

            jobsAdapter.submitList(it)
        }
    }
}
