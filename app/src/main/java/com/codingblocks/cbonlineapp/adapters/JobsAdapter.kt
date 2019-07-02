package com.codingblocks.cbonlineapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.JobsDiffCallback
import com.codingblocks.cbonlineapp.commons.NotificationClickListener
import com.codingblocks.cbonlineapp.database.models.JobsModel

class JobsAdapter(diffCallback: JobsDiffCallback) :
    ListAdapter<JobsModel, JobsAdapter.JobsViewHolder>(
        diffCallback
    ) {
    var onClick: NotificationClickListener? = null

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
            onClickListener = onClick
        }
    }

    inner class JobsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var onClickListener: NotificationClickListener? = null

        fun bindView(item: JobsModel) {
        }

    }
}


