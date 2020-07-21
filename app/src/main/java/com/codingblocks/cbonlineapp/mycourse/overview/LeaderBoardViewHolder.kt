package com.codingblocks.cbonlineapp.mycourse.overview

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.glide.loadImage
import com.codingblocks.onlineapi.models.Leaderboard
import kotlinx.android.synthetic.main.item_course_leaderboard.view.*

class LeaderBoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(leaderboard: Leaderboard, position: Int) {
        with(itemView) {
            userNoTv.text = "#$position"
            usernameTv.text = leaderboard.userName
            userRatingTv.text = leaderboard.score.toString()
            userImgView.loadImage(leaderboard.photo ?: "")
            if (position == 0) {
                userNoTv.text = "#" + leaderboard.id
                leaderboadItemll.setBackgroundColor(ContextCompat.getColor(context, R.color.orangish))
                userNoTv.setTextColor(ContextCompat.getColor(context, R.color.white))
                usernameTv.setTextColor(ContextCompat.getColor(context, R.color.white))
                userRatingTv.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }
}
