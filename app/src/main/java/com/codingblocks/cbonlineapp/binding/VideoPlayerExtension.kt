package com.codingblocks.cbonlineapp.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R

@BindingAdapter("setVideoPlayerAdapter")
fun bindRecyclerViewAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)

    val itemDecorator = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
    recyclerView.adapter = adapter
    itemDecorator.setDrawable(recyclerView.context.resources.getDrawable(R.drawable.divider))
    recyclerView.addItemDecoration(itemDecorator)
}
