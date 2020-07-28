package com.codingblocks.cbonlineapp.campaign

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.cbonlineapp.util.glide.loadImage
import com.codingblocks.onlineapi.models.Spins
import kotlinx.android.synthetic.main.item_campaign_leaderboard.view.*

class LeaderBoardPagedListAdapter : PagedListAdapter<Spins, LeaderBoardPagedListAdapter.CampaignViewHolder>(object : DiffUtil.ItemCallback<Spins>() {
    override fun areItemsTheSame(oldItem: Spins, newItem: Spins): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Spins, newItem: Spins): Boolean {
        return oldItem.sameAndEqual(newItem)
    }
}
) {
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignViewHolder {
        return CampaignViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_campaign_leaderboard, parent, false)
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: CampaignViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class CampaignViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Spins) = with(itemView) {
            usernameTv.text = "${item.user?.firstname} ${item.user?.lastname}"
            subTitleTv.text = item.spinPrize?.title ?: ""
            imgView.loadImage(item.spinPrize?.img ?: "")
            if (item.user?.photo.isNullOrEmpty()) {
                userImgView.setImageResource(R.drawable.defaultavatar)
            } else {
                userImgView.loadImage(item.user?.photo ?: "")
            }
        }
    }
}
