package com.codingblocks.cbonlineapp.course.batches


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.codingblocks.cbonlineapp.R

class SheetAdapter(val items: ArrayList<Comparision>) : BaseAdapter() {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: LayoutInflater.from(parent.context).inflate(R.layout.item_run_comparision, parent, false)

        val sheetItem = getItem(position)

        return view
    }

    override fun getItem(position: Int) = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount() = items.size
}

data class Comparision(val name: String, val lite: Boolean, val premium: Boolean, val live: Boolean, val classroom: Boolean)

