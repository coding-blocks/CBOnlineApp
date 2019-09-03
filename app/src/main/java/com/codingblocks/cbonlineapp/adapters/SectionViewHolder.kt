package com.codingblocks.cbonlineapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.SectionContent
import kotlinx.android.synthetic.main.item_section.view.*

class SectionViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_section, parent, false)) {

    var sectionContent: SectionContent? = null

    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(sectionContent: SectionContent?) {
        this.sectionContent = sectionContent
        itemView.title.text = sectionContent?.section?.name
    }
}
