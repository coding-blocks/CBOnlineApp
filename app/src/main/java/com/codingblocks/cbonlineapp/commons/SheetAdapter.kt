package com.codingblocks.cbonlineapp.commons

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.item_bottomsheet.view.*

data class SheetItem(val name: String, val icon: Int, val selected: Boolean = false)


class SheetAdapter(val items: ArrayList<SheetItem>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: LayoutInflater.from(parent.context).inflate(R.layout.item_bottomsheet, parent, false)
        val sheetItem = getItem(position)
        view.textView.apply {
            text = sheetItem.name
            setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(sheetItem.icon), null, null, null)
        }
        if (getItem(position).selected) {
            view.textView.apply {
                setTextColor(resources.getColor(R.color.orangish))
//                compoundDrawableTintMode
                setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(sheetItem.icon), null, null, null)
            }
        }
        return view

    }

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount() = items.size

}
