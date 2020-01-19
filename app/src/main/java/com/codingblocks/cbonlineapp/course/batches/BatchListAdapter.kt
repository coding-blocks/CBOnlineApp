package com.codingblocks.cbonlineapp.course.batches

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.listen
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.Runs
import kotlinx.android.synthetic.main.item_batch.view.*

class BatchListAdapter : ListAdapter<Runs, BatchListAdapter.ItemViewHolder>(DiffCallback()) {

    var onItemClick: ((Runs) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_batch, parent, false)
        ).listen { position, type ->
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Runs) = with(itemView) {
            batchTileTv.text = item.description
            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
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
