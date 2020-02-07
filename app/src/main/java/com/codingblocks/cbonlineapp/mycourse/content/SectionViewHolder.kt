package com.codingblocks.cbonlineapp.mycourse.content

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.DownloadStarter
import com.codingblocks.cbonlineapp.database.models.SectionModel
import com.codingblocks.cbonlineapp.util.extensions.getDurationBreakdown
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
        if (adapterPosition == 0) {
            itemView.dividerTop.isVisible = false
        }
        itemView.title.text = section.name
        itemView.lectures.text = "${section.totalContent} Items |"
        itemView.lectureTime.text = "Duration : ${section.totalTime.getDurationBreakdown()}"
    }
}
