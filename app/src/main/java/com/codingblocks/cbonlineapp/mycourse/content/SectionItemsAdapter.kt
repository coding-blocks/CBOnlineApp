package com.codingblocks.cbonlineapp.mycourse.content

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.commons.DownloadStarter
import com.codingblocks.cbonlineapp.database.ListObject
import com.codingblocks.cbonlineapp.database.models.ContentModel
import com.codingblocks.cbonlineapp.database.models.SectionModel
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual

class SectionItemsAdapter : ListAdapter<ListObject, RecyclerView.ViewHolder>(diffCallback) {

    var starter: DownloadStarter? = null
    var onItemClick: ((ListObject) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ListObject.TYPE_SECTION -> {
                val section = getItem(position) as SectionModel
                val sectionViewHolder = holder as SectionViewHolder
                sectionViewHolder.bindTo(section)
                sectionViewHolder.starterListener = starter
            }
            ListObject.TYPE_CONTENT -> {
                val content = getItem(position) as ContentModel
                val contentViewHolder = holder as ContentViewHolder
                contentViewHolder.bindTo(content, onItemClick)
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

    override fun getItemId(position: Int): Long {
        return when (currentList[position].getType()) {
            ListObject.TYPE_SECTION -> (getItem(position) as SectionModel).csid.toLong()
            else -> (getItem(position) as ContentModel).ccid.toLong()
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ListObject>() {
            override fun areItemsTheSame(oldItem: ListObject, newItem: ListObject): Boolean =
                when (oldItem) {
                    is SectionModel -> if (newItem is SectionModel) oldItem.csid == newItem.csid else false
                    is ContentModel -> if (newItem is ContentModel) oldItem.ccid == newItem.ccid else false
                    else -> false
                }

            /**
             * Note that in kotlin, == checking on data classes compares all contents, but in Java,
             * typically you'll implement Object#equals, and use it to compare object contents.
             */
            override fun areContentsTheSame(oldItem: ListObject, newItem: ListObject): Boolean =
                oldItem.sameAndEqual(newItem)
        }
    }
}
