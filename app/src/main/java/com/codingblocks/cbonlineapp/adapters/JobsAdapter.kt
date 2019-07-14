package com.codingblocks.cbonlineapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.JobDetailActivity
import com.codingblocks.cbonlineapp.commons.JobsDiffCallback
import com.codingblocks.cbonlineapp.database.models.JobsModel
import com.codingblocks.cbonlineapp.extensions.getSpannableSring
import com.codingblocks.cbonlineapp.util.JOB_ID
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_job.view.btnApply
import kotlinx.android.synthetic.main.item_job.view.companyLogo
import kotlinx.android.synthetic.main.item_job.view.companyTv
import kotlinx.android.synthetic.main.item_job.view.ctcTv
import kotlinx.android.synthetic.main.item_job.view.deadlineTv
import kotlinx.android.synthetic.main.item_job.view.experienceTv
import kotlinx.android.synthetic.main.item_job.view.jobTitleTv
import kotlinx.android.synthetic.main.item_job.view.locationTv
import kotlinx.android.synthetic.main.item_job.view.postedAgoTv
import kotlinx.android.synthetic.main.item_job.view.typeTv
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop

class JobsAdapter(diffCallback: JobsDiffCallback) :
    ListAdapter<JobsModel, JobsAdapter.JobsViewHolder>(
        diffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobsViewHolder {
        return JobsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_job,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: JobsViewHolder, position: Int) {
        holder.apply {
            bindView(getItem(position))
        }
    }

    inner class JobsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(job: JobsModel) {
            with(itemView) {
                Picasso.with(context).load(job.company.logo).into(companyLogo)
                jobTitleTv.text = job.title
                companyTv.text = job.company.name
                postedAgoTv.text = job.postedOn
                locationTv.text =
                    getSpannableSring("Job Location: ", job.location ?: "No experience required")
                experienceTv.text = getSpannableSring("Experience: ", job.experience)
                typeTv.text = getSpannableSring("Job Type: ", job.type)
                ctcTv.text = getSpannableSring("CTC: ", job.ctc)
                deadlineTv.text = job.deadline
                btnApply.setOnClickListener {
                    context.startActivity(context.intentFor<JobDetailActivity>(JOB_ID to job.uid).singleTop())
                }
            }
        }
    }
}
