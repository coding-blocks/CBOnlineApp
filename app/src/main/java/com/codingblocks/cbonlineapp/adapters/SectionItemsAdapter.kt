package com.codingblocks.cbonlineapp.adapters

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.codingblocks.cbonlineapp.database.models.SectionContent

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
class SectionItemsAdapter : PagedListAdapter<SectionContent, SectionViewHolder>(diffCallback) {
    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder =
        SectionViewHolder(parent)

    companion object {
        /**
         * This diff callback informs the PagedListAdapter how to compute list differences when new
         * PagedLists arrive.
         * <p>
         * When you add a Cheese with the 'Add' button, the PagedListAdapter uses diffCallback to
         * detect there's only a single item difference from before, so it only needs to animate and
         * rebind a single view.
         *
         * @see android.support.v7.util.DiffUtil
         */
        private val diffCallback = object : DiffUtil.ItemCallback<SectionContent>() {
            override fun areItemsTheSame(oldItem: SectionContent, newItem: SectionContent): Boolean =
                oldItem.content.ccid == newItem.content.ccid

            /**
             * Note that in kotlin, == checking on data classes compares all contents, but in Java,
             * typically you'll implement Object#equals, and use it to compare object contents.
             */
            override fun areContentsTheSame(oldItem: SectionContent, newItem: SectionContent): Boolean =
                oldItem == newItem
        }
    }
}
