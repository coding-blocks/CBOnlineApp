package com.codingblocks.cbonlineapp.mycourse.leaderboard

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.onlineapi.models.Leaderboard
import org.jetbrains.anko.AnkoContext

class LeaderboardListAdapter(diffCallback: LeaderboardDiffCallback) :
    ListAdapter<Leaderboard, LeaderboardListAdapter.LeaderboardViewHolder>(diffCallback) {

    val ui = LeaderboardCardUi()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        return LeaderboardViewHolder(ui.createView(AnkoContext.create(parent.context, parent)))
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.apply {
            bindView(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(item: Leaderboard) {
            ui.leaderboardsno.text = (adapterPosition + 1).toString()
            ui.leaderboarduser.text = item.userName
            ui.leaderboardcollege.text = item.collegeName
            ui.leaderboardsscore.text = item.score.toString()
        }
    }
}

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
