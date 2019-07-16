package com.codingblocks.cbonlineapp.commons

import androidx.recyclerview.widget.DiffUtil
import com.codingblocks.cbonlineapp.database.models.JobsModel

class JobsDiffCallback : DiffUtil.ItemCallback<JobsModel>() {

    /**
     * return true if the contents of both items are same
     */
    override fun areContentsTheSame(oldItem: JobsModel, newItem: JobsModel): Boolean {
        return oldItem == newItem
    }

    /**
     * returns true if both items have same ID
     */
    override fun areItemsTheSame(oldItem: JobsModel, newItem: JobsModel): Boolean {
        return oldItem.uid == newItem.uid
    }
}
