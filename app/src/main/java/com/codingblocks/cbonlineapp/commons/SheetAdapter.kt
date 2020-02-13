package com.codingblocks.cbonlineapp.commons

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import kotlinx.android.synthetic.main.item_bottomsheet.view.*

class SheetAdapter(val items: ArrayList<SheetItem>, initialSelectedItem: Int = 0) : BaseAdapter() {

    var selectedItem = initialSelectedItem

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.item_bottomsheet, parent, false)

        val sheetItem = getItem(position)

        if (position == selectedItem) {
            view.textView.apply {
                text = sheetItem.name
                setTextColor(resources.getColor(R.color.orangish))
                var drawable: Drawable
                if (sheetItem.image.isNotEmpty()) {
                    view.imgView.apply {
                        loadImage(sheetItem.image)
                        isVisible = true
                    }
                } else {
                    drawable = context.getDrawable(sheetItem.icon)!!
                    drawable = DrawableCompat.wrap(drawable)
                    DrawableCompat.setTint(drawable, resources.getColor(R.color.orangish))
                    DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
                    setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
                }
            }
        } else {
            view.textView.apply {
                text = sheetItem.name
                setTextColor(resources.getColor(R.color.black))
                var drawable: Drawable
                if (sheetItem.image.isNotEmpty()) {
                    view.imgView.apply {
                        loadImage(sheetItem.image)
                        isVisible = true
                    }
                } else {
                    drawable = context.getDrawable(sheetItem.icon)!!
                    drawable = DrawableCompat.wrap(drawable)
                    DrawableCompat.setTint(drawable, resources.getColor(R.color.black))
                    DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN)
                    setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
                }
            }
        }
        return view
    }

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount() = items.size
}

data class SheetItem(val name: String, val icon: Int = 0, val image: String = "", val courseId: String = "")
