package com.codingblocks.cbonlineapp.dashboard.doubts

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.doubts.CommentsListAdapter.ItemViewHolder
import com.codingblocks.cbonlineapp.database.models.CommentModel
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.cbonlineapp.util.extensions.timeAgo
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentsListAdapter : ListAdapter<CommentModel, ItemViewHolder>(DiffCallback()) {

    init {
        setHasStableIds(true)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_comment, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: CommentModel) = with(itemView) {
            commentUserTv.text = item.username
//            val markdown = instructor.description ?: ""
//
//            val markWon = Markwon.builder(context)
//                .usePlugin(CorePlugin.create())
//                .build()
//            markWon.setMarkdown(instructorDescTv, markdown)
            val string = Html.fromHtml(item.body)
            commentBodyTv.text = string
            commentTimeTv.text = item.updatedAt.timeAgo()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CommentModel>() {
        override fun areItemsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CommentModel, newItem: CommentModel): Boolean {
            return oldItem.sameAndEqual(newItem)
        }
    }
}
