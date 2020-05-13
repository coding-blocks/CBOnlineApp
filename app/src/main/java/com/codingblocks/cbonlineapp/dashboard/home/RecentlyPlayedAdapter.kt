package com.codingblocks.cbonlineapp.dashboard.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.PlayerState
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import kotlinx.android.synthetic.main.item_continue_card.view.*

class RecentlyPlayedAdapter : ListAdapter<PlayerState, ItemViewHolder>(
    object : DiffUtil.ItemCallback<PlayerState>() {
        override fun areItemsTheSame(oldItem: PlayerState, newItem: PlayerState): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlayerState, newItem: PlayerState): Boolean {
            return oldItem.sameAndEqual(newItem)
        }
    }) {

    var onItemClick: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_continue_card, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemClickListener = onItemClick
    }
}

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var itemClickListener: ItemClickListener? = null

    fun bind(item: PlayerState) = with(itemView) {
        progressTv.text = context.getString(R.string.completed, item.getProgress())
        progressBar.progress = item.getProgress()
        sectionTitle.text = item.sectionName
        contentTitle.text = item.contentTitle
        thumbnailImg.loadImage(item.thumbnail)
        setOnClickListener {
            itemClickListener?.onClick(
                item.sectionId, item.contentId, item.position
            )
        }
    }
}

interface ItemClickListener {
    fun onClick(sectionId: String, contentId: String, postition: Long)
}
