package com.codingblocks.cbonlineapp.commons

import androidx.recyclerview.widget.DiffUtil
import com.codingblocks.onlineapi.models.Leaderboard

/**
 * The DiffUtil ItemCallback class for the [Leaderboard] model class.
 * This enables proper diffing of items in Recycler Views using [DiffUtil]
 */
class LeaderboardDiffCallback : DiffUtil.ItemCallback<Leaderboard>() {

    override fun areContentsTheSame(oldItem: Leaderboard, newItem: Leaderboard): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: Leaderboard, newItem: Leaderboard): Boolean {
        return oldItem == newItem
    }

}
