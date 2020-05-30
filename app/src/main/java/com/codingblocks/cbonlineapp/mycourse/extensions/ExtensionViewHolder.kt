package com.codingblocks.cbonlineapp.mycourse.extensions

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.onlineapi.models.ProductExtensionsItem
import kotlinx.android.synthetic.main.item_extension_pack.view.*
import kotlinx.android.synthetic.main.list_item_extension.view.*
import kotlin.math.ceil

class ExtensionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(extension: ProductExtensionsItem, position: Int) {
        with(itemView) {
            extensionItem.text = extension.description ?: ""
            extensionItem.id = extension.id ?: -1
            extensionItemAmount.text = if (extension.mrp != null && extension.mrp!! > 0)
                (extension.mrp?.div(100))?.toDouble()?.let { ceil(it).toString() }
            else "FREE"
            extensionGrp.addView(extensionItem)
        }
    }

}
