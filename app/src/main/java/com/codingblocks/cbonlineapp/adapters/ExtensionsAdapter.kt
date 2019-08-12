package com.codingblocks.cbonlineapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.models.ProductExtensionsItem
import kotlinx.android.synthetic.main.item_extension.view.cardView
import kotlinx.android.synthetic.main.item_extension.view.date
import kotlinx.android.synthetic.main.item_extension.view.price
import kotlinx.android.synthetic.main.item_extension.view.title

class ExtensionsAdapter(var list: ArrayList<ProductExtensionsItem>) : RecyclerView.Adapter<ExtensionsAdapter.ExtensionViewHolder>() {

    private var checkedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
        ExtensionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_extension, parent, false)
        return ExtensionViewHolder(view)
    }

    fun setData(extensionData: ArrayList<ProductExtensionsItem>) {
        this.list = extensionData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ExtensionViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun getSelected(): ProductExtensionsItem? {
        return if (checkedPosition != -1) {
            list[checkedPosition]
        } else null
    }

    inner class ExtensionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(extension: ProductExtensionsItem) {
            itemView.title.text = extension.description
            itemView.date.text = "${extension.duration} Days"
            itemView.price.text = "Rs. ${extension.mrp?.div(100)}"
            if (checkedPosition == -1) {
                itemView.cardView.setCardBackgroundColor(itemView.context.resources.getColor(R.color.white))
            } else {
                if (checkedPosition == adapterPosition) {
                    itemView.cardView.setCardBackgroundColor(itemView.context.resources.getColor(R.color.dark_transparent))
                } else {
                    itemView.cardView.setCardBackgroundColor(itemView.context.resources.getColor(R.color.white))
                }
            }
            itemView.setOnClickListener {
                itemView.cardView.setCardBackgroundColor(itemView.context.resources.getColor(R.color.dark_transparent))
                if (checkedPosition != adapterPosition) {
                    notifyItemChanged(checkedPosition)
                    checkedPosition = adapterPosition
                }
            }
        }
    }
}
