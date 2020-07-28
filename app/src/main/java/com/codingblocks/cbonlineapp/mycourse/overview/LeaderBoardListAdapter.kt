package com.codingblocks.cbonlineapp.mycourse.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.Leaderboard

class LeaderBoardListAdapter : ListAdapter<Leaderboard, LeaderBoardViewHolder>(diffCallback) {
    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Leaderboard>() {
            override fun areItemsTheSame(oldItem: Leaderboard, newItem: Leaderboard): Boolean =
                oldItem.sameAndEqual(newItem)

            override fun areContentsTheSame(oldItem: Leaderboard, newItem: Leaderboard): Boolean =
                oldItem.id.sameAndEqual(newItem.id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderBoardViewHolder {
        return LeaderBoardViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_course_leaderboard, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LeaderBoardViewHolder, position: Int) {
        val leaderBoard = getItem(position)
        if (leaderBoard != null)
            holder.apply {
                bind(leaderBoard, position)
            }
    }

    fun clear() {
        this.submitList(null)
    }
}
