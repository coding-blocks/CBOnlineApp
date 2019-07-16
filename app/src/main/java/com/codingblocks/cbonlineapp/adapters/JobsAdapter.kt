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
import com.codingblocks.cbonlineapp.extensions.isotomillisecond
import com.codingblocks.cbonlineapp.extensions.loadSvg
import com.codingblocks.cbonlineapp.extensions.timeAgo
import com.codingblocks.cbonlineapp.util.JOB_ID
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_job.view.*
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
                if (job.company.logo.takeLast(3) == "svg") {
                    companyLogo.loadSvg(job.company.logo)
                } else {
                    Picasso.with(context).load(job.company.logo).into(companyLogo)
                }
                jobTitleTv.text = job.title
                companyTv.text = job.company.name
                postedAgoTv.text = timeAgo(job.postedOn.isotomillisecond())
                locationTv.text =
                    getSpannableSring("Job Location: ", job.location ?: "No experience required")
                experienceTv.text = getSpannableSring("Experience: ", job.experience)
                typeTv.text = getSpannableSring("Job Type: ", job.type)
                ctcTv.text = getSpannableSring("CTC: ", job.ctc)
                deadlineTv.text = "Deadline : No Deadline"
                btnApply.setOnClickListener {
                    context.startActivity(context.intentFor<JobDetailActivity>(JOB_ID to job.uid).singleTop())
                }
            }
        }

    }
}
