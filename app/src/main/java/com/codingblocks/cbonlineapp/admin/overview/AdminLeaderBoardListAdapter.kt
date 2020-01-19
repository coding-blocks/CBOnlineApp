package com.codingblocks.cbonlineapp.admin.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.DoubtLeaderBoard

class AdminLeaderBoardListAdapter : ListAdapter<DoubtLeaderBoard, AdminLeaderBoardViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminLeaderBoardViewHolder {
        return AdminLeaderBoardViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_leaderboard, parent, false))
    }

    override fun onBindViewHolder(holder: AdminLeaderBoardViewHolder, position: Int) {
        val leaderBoard = getItem(position)
        if (leaderBoard != null)
            holder.apply {
                bind(leaderBoard, position)
            }
    }

    /**
     * The function to call when the adapter has to be cleared of items
     */
    fun clear() {
        this.submitList(null)
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<DoubtLeaderBoard>() {
            override fun areItemsTheSame(oldItem: DoubtLeaderBoard, newItem: DoubtLeaderBoard): Boolean =
                oldItem.sameAndEqual(newItem)

            override fun areContentsTheSame(oldItem: DoubtLeaderBoard, newItem: DoubtLeaderBoard): Boolean =
                oldItem.id.sameAndEqual(newItem.id)
        }
    }
}
