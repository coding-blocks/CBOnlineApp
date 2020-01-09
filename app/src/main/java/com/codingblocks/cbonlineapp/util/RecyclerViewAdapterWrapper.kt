package com.codingblocks.cbonlineapp.util

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual

/**
 * @author pulkit-mac
 */
open class RecyclerViewAdapterWrapper(val wrappedAdapter: ListAdapter<out Any, out RecyclerView.ViewHolder>) : ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return wrappedAdapter.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        wrappedAdapter.onBindViewHolder(holder, position)
    }

    override fun getItemCount(): Int {
        return wrappedAdapter.itemCount
    }

    override fun getItemViewType(position: Int): Int {
        return wrappedAdapter.getItemViewType(position)
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        wrappedAdapter.setHasStableIds(hasStableIds)
    }

    override fun getItemId(position: Int): Long {
        return wrappedAdapter.getItemId(position)
    }

    override fun registerAdapterDataObserver(observer: AdapterDataObserver) {
        wrappedAdapter.registerAdapterDataObserver(observer)
    }

    override fun unregisterAdapterDataObserver(observer: AdapterDataObserver) {
        wrappedAdapter.unregisterAdapterDataObserver(observer)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        wrappedAdapter.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        wrappedAdapter.onDetachedFromRecyclerView(recyclerView)
    }


    init {
        wrappedAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                notifyDataSetChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                notifyItemRangeRemoved(positionStart, itemCount)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }
        })
    }

    class DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem.sameAndEqual(newItem)
        }
    }
}
