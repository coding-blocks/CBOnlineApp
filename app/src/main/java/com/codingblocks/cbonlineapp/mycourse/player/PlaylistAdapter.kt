package com.codingblocks.cbonlineapp.mycourse.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import kotlinx.android.synthetic.main.item_playlist.view.*

class PlaylistAdapter : ListAdapter<ContentModel, PlaylistAdapter.PlaylistViewHolder>(diffCallback) {
    private var selectedItem: Int = -1
    var onItemClick: ((ContentModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false))

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getType()
    }

    fun submitList(list: List<ContentModel>, contentId: String) {
        selectedItem = list.indexOfFirst { it.ccid == contentId }
        super.submitList(list)
    }

    fun updateSelectedItem(contentId: String) {
        selectedItem = currentList.indexOfFirst { it.ccid == contentId }
        notifyDataSetChanged()
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ContentModel>() {
            override fun areItemsTheSame(oldItem: ContentModel, newItem: ContentModel): Boolean =
                oldItem.ccid == newItem.ccid

            override fun areContentsTheSame(oldItem: ContentModel, newItem: ContentModel): Boolean =
                oldItem.sameAndEqual(newItem)
        }
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val content = getItem(position)
        holder.bindTo(content, onItemClick, position)
    }

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindTo(content: ContentModel, onItemClick: ((ContentModel) -> Unit)?, position: Int) = with(itemView) {
            when {
                selectedItem == position -> {
                    contentTitleTv.isEnabled = false
                    contentTitleTv.isActivated = true
                }
                content.progress == "DONE" -> {
                    contentTitleTv.isEnabled = true
                }
                else -> {
                    contentTitleTv.isActivated = false
                    contentTitleTv.isEnabled = false
                }
            }
            contentTitleTv.text = content.title
            if (selectedItem != position) {
                setOnClickListener { onItemClick?.invoke(content) }
            }
        }
    }
}
