package com.codingblocks.cbonlineapp.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.extensions.getSpannableSring
import com.codingblocks.cbonlineapp.extensions.observer
import com.codingblocks.cbonlineapp.util.JOB_ID
import com.codingblocks.cbonlineapp.viewmodels.JobDetailViewModel
import com.squareup.picasso.Picasso
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_job_detail.companyDescriptionTv
import kotlinx.android.synthetic.main.activity_job_detail.jobDescriptionTv
import kotlinx.android.synthetic.main.item_job.companyLogo
import kotlinx.android.synthetic.main.item_job.companyTv
import kotlinx.android.synthetic.main.item_job.ctcTv
import kotlinx.android.synthetic.main.item_job.deadlinell
import kotlinx.android.synthetic.main.item_job.experienceTv
import kotlinx.android.synthetic.main.item_job.jobTitleTv
import kotlinx.android.synthetic.main.item_job.locationTv
import kotlinx.android.synthetic.main.item_job.postedAgoTv
import kotlinx.android.synthetic.main.item_job.typeTv
import org.koin.androidx.viewmodel.ext.android.viewModel

class JobDetailActivity : AppCompatActivity() {

    private val viewModel by viewModel<JobDetailViewModel>()

    lateinit var jobId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_detail)

        jobId = intent.getStringExtra(JOB_ID)

        deadlinell.visibility = View.GONE

        viewModel.fetchJob(jobId)


        viewModel.getJobById(jobId).observer(this) {

            with(it) {
                Picasso.with(this@JobDetailActivity).load(company.logo).into(companyLogo)
                jobTitleTv.text = title
                companyTv.text = company.name
                postedAgoTv.text = postedOn
                locationTv.text = getSpannableSring("Job Location: ", location)
                experienceTv.text = getSpannableSring("Experience: ", experience)
                typeTv.text = getSpannableSring("Job Type: ", type)
                ctcTv.text = getSpannableSring("CTC: ", ctc)
                jobDescriptionTv.text = description
                companyDescriptionTv.text = company.companyDescription
                viewModel.getCourses(courseId)
            }
        }

    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}
