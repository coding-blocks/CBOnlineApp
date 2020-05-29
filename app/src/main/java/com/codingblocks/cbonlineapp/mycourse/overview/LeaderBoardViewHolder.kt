package com.codingblocks.cbonlineapp.mycourse.overview

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.models.Leaderboard
import kotlinx.android.synthetic.main.item_course_leaderboard.view.userNoTv
import kotlinx.android.synthetic.main.item_course_leaderboard.view.userRatingTv
import kotlinx.android.synthetic.main.item_course_leaderboard.view.usernameTv
import kotlinx.android.synthetic.main.item_course_leaderboard.view.leaderboadItemll
import kotlinx.android.synthetic.main.item_course_leaderboard.view.selectorIv

class LeaderBoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(leaderboard: Leaderboard, position: Int) {
        with(itemView) {
            userNoTv.text = "#$position"
            usernameTv.text = leaderboard.userName
            userRatingTv.text = leaderboard.score.toString()
            if (position == 0) {
                userNoTv.text = "#" + leaderboard.id
                leaderboadItemll.setBackgroundColor(ContextCompat.getColor(context, R.color.orangish))
                userNoTv.setTextColor(ContextCompat.getColor(context, R.color.white))
                usernameTv.setTextColor(ContextCompat.getColor(context, R.color.white))
                userRatingTv.setTextColor(ContextCompat.getColor(context, R.color.white))
                selectorIv.setColorFilter(ContextCompat.getColor(context, R.color.white))
            }

        }
    }
}
