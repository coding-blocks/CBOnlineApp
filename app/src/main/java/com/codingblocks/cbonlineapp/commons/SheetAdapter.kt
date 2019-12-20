package com.codingblocks.cbonlineapp.commons

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.widget.ImageViewCompat
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.item_bottomsheet.view.*
import android.graphics.PorterDuff
import androidx.core.graphics.drawable.DrawableCompat



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
                var drawable = context.getDrawable(sheetItem.icon)!!
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable, resources.getColor(R.color.orangish))
                DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
                setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
            }
        }
        return view

    }

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount() = items.size

}
