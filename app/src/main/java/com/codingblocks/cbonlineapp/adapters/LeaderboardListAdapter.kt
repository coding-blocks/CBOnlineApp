package com.codingblocks.cbonlineapp.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.commons.LeaderboardDiffCallback
import com.codingblocks.cbonlineapp.ui.LeaderboardCardUi
import com.codingblocks.onlineapi.models.Leaderboard
import org.jetbrains.anko.AnkoContext

class LeaderboardListAdapter(diffCallback: LeaderboardDiffCallback) :
    ListAdapter<Leaderboard, LeaderboardListAdapter.LeaderboardViewHolder>(diffCallback) {

    val ui = LeaderboardCardUi()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardListAdapter.LeaderboardViewHolder {
        return LeaderboardViewHolder(ui.createView(AnkoContext.create(parent.context, parent)))
    }

    override fun onBindViewHolder(holder: LeaderboardListAdapter.LeaderboardViewHolder, position: Int) {
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