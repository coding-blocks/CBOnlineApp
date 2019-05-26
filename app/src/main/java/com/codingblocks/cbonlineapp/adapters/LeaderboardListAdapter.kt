package com.codingblocks.cbonlineapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.models.Leaderboard
import kotlinx.android.synthetic.main.leader_board_list.view.*

class LeaderboardListAdapter(val dataset: ArrayList<Leaderboard>) :
    RecyclerView.Adapter<LeaderboardListAdapter.LeaderboardViewHolder>() {

    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(leaderboard: Leaderboard) {
            itemView.leaderboard_user.text = leaderboard.userName
            itemView.leaderboard_college.text = leaderboard.collegeName
            itemView.leaderboard_score.text = leaderboard.score.toString()
            itemView.leaderboard_sno.text = (adapterPosition + 1).toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.leader_board_list, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(dataset[position])
    }

    override fun getItemCount() = dataset.size

}
