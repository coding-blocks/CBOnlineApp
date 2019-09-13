package com.codingblocks.cbonlineapp.adapters.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.SectionModel
import com.codingblocks.cbonlineapp.extensions.getDurationBreakdown
import com.codingblocks.cbonlineapp.util.DownloadStarter
import kotlinx.android.synthetic.main.item_section.view.*

class SectionViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_section, parent, false)) {

    var section: SectionModel? = null
    var starterListener: DownloadStarter? = null

    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(section: SectionModel) {
        this.section = section
        itemView.title.text = section.name
        itemView.lectures.text = "${section.completedContent}/${section.totalContent} Lectures Completed"
        itemView.lectureTime.text = section.totalTime.getDurationBreakdown()

    }
}
