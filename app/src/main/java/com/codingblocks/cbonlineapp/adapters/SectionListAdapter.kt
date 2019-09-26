package com.codingblocks.cbonlineapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.commons.SectionListClickListener
import com.codingblocks.cbonlineapp.database.models.SectionModel

class SectionListAdapter(private val sectionList: ArrayList<SectionModel>) : RecyclerView.Adapter<SectionListAdapter.SectionListViewHolder>() {

    var onSectionListClick: SectionListClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionListViewHolder {
        return SectionListViewHolder(LayoutInflater.from(parent.context).inflate(
            android.R.layout.simple_expandable_list_item_1,
            parent,
            false
        ))
    }

    override fun getItemCount(): Int = sectionList.size

    override fun onBindViewHolder(holder: SectionListViewHolder, position: Int) {
        holder.apply {
            bind(sectionList[position])
            sectionClickListener = onSectionListClick

        }

    }

    class SectionListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var sectionClickListener: SectionListClickListener? = null

        fun bind(item: SectionModel) {
            itemView.findViewById<TextView>(android.R.id.text1).text = item.name
            itemView.setOnClickListener {
                sectionClickListener?.onClick(item.pos)
            }
        }
    }


}
