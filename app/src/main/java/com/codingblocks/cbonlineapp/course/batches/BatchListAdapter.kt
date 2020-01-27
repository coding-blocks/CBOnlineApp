package com.codingblocks.cbonlineapp.course.batches

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.BaseModel
import com.codingblocks.onlineapi.models.Professions
import com.codingblocks.onlineapi.models.Runs
import kotlinx.android.synthetic.main.item_batch.view.*

class BatchListAdapter : ListAdapter<BaseModel, BatchListAdapter.ItemViewHolder>(DiffCallback()) {

    var onItemClick: ((BaseModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_batch, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: BaseModel) = with(itemView) {
            if (item is Runs) {
                batchTileTv.text = item.description
            } else if (item is Professions) {
                batchTileTv.text = item.title
            }
            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<BaseModel>() {
        override fun areItemsTheSame(oldItem: BaseModel, newItem: BaseModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BaseModel, newItem: BaseModel): Boolean {
            return oldItem.sameAndEqual(newItem)
        }
    }
}
