package com.codingblocks.cbonlineapp.course.batches

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.Runs
import kotlinx.android.synthetic.main.item_batch.view.*

class BatchListAdapter : ListAdapter<Runs, BatchListAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_batch, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Runs) = with(itemView) {
            batchTileTv.text = item.description
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Runs>() {
        override fun areItemsTheSame(oldItem: Runs, newItem: Runs): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Runs, newItem: Runs): Boolean {
            return oldItem.sameAndEqual(newItem)
        }
    }
}
