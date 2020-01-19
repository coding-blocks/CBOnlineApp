package com.codingblocks.cbonlineapp.admin.overview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.util.extensions.round
import com.codingblocks.onlineapi.models.DoubtLeaderBoard
import kotlinx.android.synthetic.main.item_admin_leaderboard.view.*

class AdminLeaderBoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(doubtLeaderBoard: DoubtLeaderBoard, position: Int) {
        with(itemView) {
            userNoTv.text = (position + 1).toString()
            usernameTv.text = doubtLeaderBoard.user?.firstname + " " + doubtLeaderBoard.user?.lastname
            userRatingTv.text = doubtLeaderBoard.ratingAll.round(2).toString()
        }
    }
}
