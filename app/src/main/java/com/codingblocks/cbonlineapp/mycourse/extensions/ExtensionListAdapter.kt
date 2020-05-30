package com.codingblocks.cbonlineapp.mycourse.extensions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.ProductExtensionsItem

class ExtensionListAdapter: ListAdapter<ProductExtensionsItem, ExtensionViewHolder>(diffCallback) {
    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<ProductExtensionsItem>() {
            override fun areItemsTheSame(oldItem: ProductExtensionsItem, newItem: ProductExtensionsItem): Boolean =
                oldItem.sameAndEqual(newItem)

            override fun areContentsTheSame(oldItem: ProductExtensionsItem, newItem: ProductExtensionsItem): Boolean =
                oldItem.sameAndEqual(newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtensionViewHolder {
        return ExtensionViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_extension, parent, false))
    }

    override fun onBindViewHolder(holder: ExtensionViewHolder, position: Int) {
        val productExtensionsItem = getItem(position)
        if (productExtensionsItem != null)
            holder.apply {
                bind(productExtensionsItem, position)
            }
    }

    fun clear() {
        this.submitList(null)
    }
}
