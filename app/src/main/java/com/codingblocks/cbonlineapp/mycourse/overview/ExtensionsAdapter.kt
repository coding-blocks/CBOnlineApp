package com.codingblocks.cbonlineapp.mycourse.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.models.ProductExtensionsItem
import kotlinx.android.synthetic.main.item_extension.view.*

class ExtensionsAdapter(var list: ArrayList<ProductExtensionsItem>) : RecyclerView.Adapter<ExtensionsAdapter.ExtensionViewHolder>() {

    var checkedPosition = MutableLiveData<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
        ExtensionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_extension, parent, false)
        return ExtensionViewHolder(view)
    }

    fun setData(extensionData: ArrayList<ProductExtensionsItem>) {
        this.list = extensionData
        notifyDataSetChanged()
        checkedPosition.value = -1
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ExtensionViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun getSelected(): ProductExtensionsItem? {
        return if (checkedPosition.value != -1) {
            list[checkedPosition.value!!]
        } else null
    }

    inner class ExtensionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(extension: ProductExtensionsItem) {
            itemView.title.text = extension.description
            itemView.date.text = "${extension.duration} Days"
            itemView.price.text = "Rs. ${extension.mrp?.div(100)}"
            if (checkedPosition.value == -1) {
                itemView.cardView.setBackgroundColor(itemView.context.resources.getColor(R.color.white))
            } else {
                if (checkedPosition.value == adapterPosition) {
                    itemView.cardView.setBackgroundColor(itemView.context.resources.getColor(R.color.light_transparent))
                } else {
                    itemView.cardView.setBackgroundColor(itemView.context.resources.getColor(R.color.white))
                }
            }
            itemView.setOnClickListener {
                itemView.cardView.setBackgroundColor(itemView.context.resources.getColor(R.color.light_transparent))
                if (checkedPosition.value != adapterPosition) {
                    notifyItemChanged(checkedPosition.value!!)
                    checkedPosition.value = adapterPosition
                }
            }
        }
    }
}
