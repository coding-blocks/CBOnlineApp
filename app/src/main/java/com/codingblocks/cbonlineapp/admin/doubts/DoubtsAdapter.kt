package com.codingblocks.cbonlineapp.admin.doubts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.Doubts

class DoubtsAdapter : ListAdapter<Doubts, DoubtViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtViewHolder {
        return DoubtViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_doubt, parent, false))
    }

    override fun onBindViewHolder(holder: DoubtViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Doubts>() {
            override fun areItemsTheSame(oldItem: Doubts, newItem: Doubts): Boolean =
                oldItem.sameAndEqual(newItem)

            override fun areContentsTheSame(oldItem: Doubts, newItem: Doubts): Boolean =
                oldItem.id.sameAndEqual(newItem.id)
        }
    }
}
