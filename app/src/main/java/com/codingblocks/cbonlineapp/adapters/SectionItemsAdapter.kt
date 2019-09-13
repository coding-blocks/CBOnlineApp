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
import com.codingblocks.cbonlineapp.util.DownloadStarter

/**
 * A  PagedListAdapter that binds SectionContent items into Section and their respectiv content.
 * <p>
 * PagedListAdapter is a RecyclerView.Adapter base class which can present the content of PagedLists
 * in a RecyclerView. It requests new pages as the user scrolls, and handles new PagedLists by
 * computing list differences on a background thread, and dispatching minimal, efficient updates to
 * the RecyclerView to ensure minimal UI thread work.
 * <p>
 * If you want to use your own Adapter base class, try using a PagedListAdapterHelper inside your
 * adapter instead.
 *
 * @see android.arch.paging.PagedListAdapter
 * @see android.arch.paging.AsyncPagedListDiffer
 */
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
                oldItem.equals(newItem)

            /**
             * Note that in kotlin, == checking on data classes compares all contents, but in Java,
             * typically you'll implement Object#equals, and use it to compare object contents.
             */
            override fun areContentsTheSame(oldItem: ListObject, newItem: ListObject): Boolean =
                oldItem.equals(newItem)
        }
    }
}
