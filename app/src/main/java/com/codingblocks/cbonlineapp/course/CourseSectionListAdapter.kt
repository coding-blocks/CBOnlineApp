package com.codingblocks.cbonlineapp.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.Sections
import kotlinx.android.synthetic.main.item_course_section.view.*

class CourseSectionListAdapter : ListAdapter<Sections, CourseSectionListAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_course_section, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Sections) = with(itemView) {
            title.text = item.name
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Sections>() {
        override fun areItemsTheSame(oldItem: Sections, newItem: Sections): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Sections, newItem: Sections): Boolean {
            return oldItem.sameAndEqual(newItem)
        }
    }
}
