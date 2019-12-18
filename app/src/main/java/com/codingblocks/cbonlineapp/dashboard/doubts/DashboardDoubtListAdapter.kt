package com.codingblocks.cbonlineapp.dashboard.doubts

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import kotlinx.android.synthetic.main.item_admin_doubt.view.*
import kotlinx.android.synthetic.main.item_doubts.view.*

class DashboardDoubtListAdapter : ListAdapter<DoubtsModel, DashboardDoubtListAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_doubts, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: DoubtsModel) = with(itemView) {
            doubtTitleTv.text = item.title
            doubtDescriptionTv.text = item.body
            doubtTimeTv.text = item.createdAt
            chatTv.isVisible = !item.conversationId.isNullOrEmpty()
            setOnClickListener {
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
