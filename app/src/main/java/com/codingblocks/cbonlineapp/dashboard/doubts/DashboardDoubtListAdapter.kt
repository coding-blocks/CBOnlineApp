package com.codingblocks.cbonlineapp.dashboard.doubts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.admin.doubts.ChatClickListener
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.util.PENDING
import com.codingblocks.cbonlineapp.util.RESOLVED
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.cbonlineapp.util.extensions.timeAgo
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.item_doubts.view.*

class DashboardDoubtListAdapter : ListAdapter<DoubtsModel, DashboardDoubtListAdapter.ItemViewHolder>(DiffCallback()) {

    var onResolveClick: ResolveDoubtClickListener? = null
    var onCommentClick: DoubtCommentClickListener? = null
    var onChatClick: ChatClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_doubts, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val doubt = getItem(position)
        if (doubt != null)
            holder.apply {
                bind(doubt)
                resolveClickListener = onResolveClick
                commentClickListener = onCommentClick
                chatClickListener = onChatClick
            }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var resolveClickListener: ResolveDoubtClickListener? = null
        var commentClickListener: DoubtCommentClickListener? = null
        var chatClickListener: ChatClickListener? = null

        fun bind(item: DoubtsModel) = with(itemView) {
            doubtTitleTv.text = item.title
            val markWon = Markwon.create(context)
            markWon.setMarkdown(doubtDescriptionTv, item.body)
            doubtTimeTv.text = item.createdAt.timeAgo()
            chatTv.isVisible = !item.conversationId.isNullOrEmpty()
            when (item.status) {
                "RESOLVED" -> {
                    markResolvedTv.isVisible = false
                    chatTv.apply {
                        isVisible = true
                        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_reopen_small), null)
                        text = context.getString(R.string.reopen_doubt)
                        setOnClickListener {
                            resolveClickListener?.onClick(item.apply {
                                status = PENDING
                            })
                        }
                    }
                }
                else -> {
                    markResolvedTv.apply {
                        isVisible = true
                        setOnClickListener {
                            resolveClickListener?.onClick(item.apply {
                                status = RESOLVED
                            })
                        }
                    }
                    chatTv.apply {
                        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_chat), null)
                        text = context.getString(R.string.chat_with_ta)
                        setOnClickListener {
                            chatClickListener?.onClick(item.conversationId ?: "", item.dbtUid)
                        }
                    }
                }
            }

            commentTv.setOnClickListener {
                commentClickListener?.onClick(item.dbtUid)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DoubtsModel>() {
        override fun areItemsTheSame(oldItem: DoubtsModel, newItem: DoubtsModel): Boolean {
            return oldItem.dbtUid == newItem.dbtUid
        }

        override fun areContentsTheSame(oldItem: DoubtsModel, newItem: DoubtsModel): Boolean {
            return oldItem.sameAndEqual(newItem)
        }
    }
}

interface ResolveDoubtClickListener {
    fun onClick(doubt: DoubtsModel)
}

interface DoubtCommentClickListener {
    fun onClick(doubtId: String)
}
