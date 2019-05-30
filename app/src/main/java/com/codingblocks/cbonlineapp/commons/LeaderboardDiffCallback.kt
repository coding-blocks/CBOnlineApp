package com.codingblocks.cbonlineapp.commons

import androidx.recyclerview.widget.DiffUtil
import com.codingblocks.onlineapi.models.Leaderboard

/**
 * The DiffUtil ItemCallback class for the [Leaderboard] model class.
 * This enables proper diffing of items in Recycler Views using [DiffUtil]
 */
class LeaderboardDiffCallback : DiffUtil.ItemCallback<Leaderboard>() {

    /**
     * return true if the contents of both items are same
     */
    override fun areContentsTheSame(oldItem: Leaderboard, newItem: Leaderboard): Boolean {
        return oldItem == newItem
    }

    /**
     * returns true if both items have same ID
     */
    override fun areItemsTheSame(oldItem: Leaderboard, newItem: Leaderboard): Boolean {
        return oldItem.id == newItem.id
    }
}
