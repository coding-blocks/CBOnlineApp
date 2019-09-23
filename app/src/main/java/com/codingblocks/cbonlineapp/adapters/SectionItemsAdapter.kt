package com.codingblocks.cbonlineapp.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.adapters.viewholders.ContentViewHolder
import com.codingblocks.cbonlineapp.adapters.viewholders.SectionViewHolder
import com.codingblocks.cbonlineapp.database.ListObject
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.database.models.SectionModel
import com.codingblocks.cbonlineapp.extensions.sameAndEqual
import com.codingblocks.cbonlineapp.util.DownloadStarter


class SectionItemsAdapter : ListAdapter<ListObject, RecyclerView.ViewHolder>(diffCallback) {

    var starter: DownloadStarter? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ListObject.TYPE_SECTION -> {
                val section = getItem(position) as SectionModel
                val sectionViewHolder = holder as SectionViewHolder
                sectionViewHolder.bindTo(section)
            }
            ListObject.TYPE_CONTENT -> {
                val content = getItem(position) as ContentModel
                val contentViewHolder = holder as ContentViewHolder
                contentViewHolder.bindTo(content)
                contentViewHolder.starterListener = starter
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            ListObject.TYPE_SECTION -> SectionViewHolder(parent)
            ListObject.TYPE_CONTENT -> ContentViewHolder(parent)
            else -> ContentViewHolder(parent)
        }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getType()
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<ListObject>() {
            override fun areItemsTheSame(oldItem: ListObject, newItem: ListObject): Boolean =
                oldItem.sameAndEqual(newItem)

            /**
             * Note that in kotlin, == checking on data classes compares all contents, but in Java,
             * typically you'll implement Object#equals, and use it to compare object contents.
             */
            override fun areContentsTheSame(oldItem: ListObject, newItem: ListObject): Boolean =
                oldItem.sameAndEqual(newItem)
        }
    }
}
