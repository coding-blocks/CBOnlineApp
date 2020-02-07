package com.codingblocks.cbonlineapp.mycourse.content

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.SectionListClickListener
import com.codingblocks.cbonlineapp.database.models.SectionModel
import kotlinx.android.synthetic.main.item_section_list.view.*

class SectionListAdapter(private val sectionList: ArrayList<SectionModel>) : RecyclerView.Adapter<SectionListAdapter.SectionListViewHolder>() {

    var onSectionListClick: SectionListClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionListViewHolder {
        return SectionListViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_section_list,
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

    inner class SectionListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var sectionClickListener: SectionListClickListener? = null

        fun bind(item: SectionModel) {
            itemView.sectionTitle.text = item.name
            itemView.sectionCount.text = item.totalContent.toString()
            itemView.setOnClickListener {
                sectionClickListener?.onClick(item.pos, adapterPosition)
            }
        }
    }
}
