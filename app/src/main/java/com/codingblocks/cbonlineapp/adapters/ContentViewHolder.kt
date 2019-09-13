package com.codingblocks.cbonlineapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.util.DownloadStarter
import kotlinx.android.synthetic.main.item_content.view.*

class ContentViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_content, parent, false)) {

    var content: ContentModel? = null
    var starterListener: DownloadStarter? = null

    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(content: ContentModel) {
        this.content = content
        itemView.title.text = content.title
//        val ll = itemView.findViewById<LinearLayout>(R.id.sectionContents)
//        if (ll.visibility == View.VISIBLE) {
//            ll.removeAllViews()
//        } else {
//            ll.removeAllViews()
//            ll.visibility = View.GONE
//        }
//        itemView.lectures.text = "0/${sectionContent?.content?.size} Lectures Completed"
//        var duration: Long = 0
//        var sectionComplete = 0
//        sectionContent?.content?.forEach { content ->
//            val factory = LayoutInflater.from(itemView.context)
//            val inflatedView = factory.inflate(R.layout.item_section_detailed_info, ll, false)
//            val subTitle = inflatedView.findViewById(R.id.textView15) as TextView
//            val downloadBtn = inflatedView.findViewById(R.id.downloadBtn) as ImageView
//            val contentType = inflatedView.findViewById(R.id.contentType) as ImageView
//            subTitle.text = content.title
//            inflatedView.setOnClickListener {
//                starterListener?.updateProgress(content.ccid)
//            }
//            ll.addView(inflatedView)
//
//            itemView.setOnClickListener {
//                ll.visibility = if (ll.visibility == View.VISIBLE) View.GONE else View.VISIBLE
//            }
//
//            itemView.arrow.setOnClickListener {
//                ll.visibility = if (ll.visibility == View.VISIBLE) View.GONE else View.VISIBLE
//            }
//        }

    }
}
