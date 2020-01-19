package com.codingblocks.cbonlineapp.library

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class MyItemDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return when (recyclerView.getChildViewHolder(view)) {
                is LibraryListAdapter.NoteViewHolder -> (recyclerView.getChildViewHolder(view) as LibraryListAdapter.NoteViewHolder).getItemDetails()
                is LibraryListAdapter.BookmarkViewHolder -> (recyclerView.getChildViewHolder(view) as LibraryListAdapter.BookmarkViewHolder).getItemDetails()
                else -> (recyclerView.getChildViewHolder(view) as LibraryListAdapter.NoteViewHolder).getItemDetails()
            }
        }
        return null
    }
}

class MyItemKeyProvider(private val recyclerView: RecyclerView) :
    ItemKeyProvider<Long>(SCOPE_MAPPED) {

    override fun getKey(position: Int): Long? {
        return recyclerView.adapter?.getItemId(position)
    }

    override fun getPosition(key: Long): Int {
        val viewHolder = recyclerView.findViewHolderForItemId(key)
        return viewHolder?.layoutPosition ?: RecyclerView.NO_POSITION
    }
}
