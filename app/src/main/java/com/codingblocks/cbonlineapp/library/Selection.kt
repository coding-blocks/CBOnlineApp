package com.codingblocks.cbonlineapp.library

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.course.batches.RunListAdapter
import com.codingblocks.cbonlineapp.database.models.BookmarkModel
import com.codingblocks.cbonlineapp.database.models.ContentLecture
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.onlineapi.models.Runs

class MyItemDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<String>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return when (recyclerView.getChildViewHolder(view)) {
                is LibraryListAdapter.NoteViewHolder -> (recyclerView.getChildViewHolder(view) as LibraryListAdapter.NoteViewHolder).getItemDetails()
                is LibraryListAdapter.BookmarkViewHolder -> (recyclerView.getChildViewHolder(view) as LibraryListAdapter.BookmarkViewHolder).getItemDetails()
                is LibraryListAdapter.DownloadViewHolder -> (recyclerView.getChildViewHolder(view) as LibraryListAdapter.DownloadViewHolder).getItemDetails()
                is RunListAdapter.RunsViewHolder -> (recyclerView.getChildViewHolder(view) as RunListAdapter.RunsViewHolder).getItemDetails()
                else -> (recyclerView.getChildViewHolder(view) as LibraryListAdapter.NoteViewHolder).getItemDetails()
            }
        }
        return null
    }
}

class MyItemKeyProvider(private val adapter: ListAdapter<out Any, out RecyclerView.ViewHolder>) : ItemKeyProvider<String>(SCOPE_CACHED) {
    override fun getKey(position: Int): String? = when (adapter.currentList[position]) {
        is NotesModel -> (adapter.currentList[position] as NotesModel).nttUid
        is BookmarkModel -> (adapter.currentList[position] as BookmarkModel).bookmarkUid
        is ContentLecture -> (adapter.currentList[position] as ContentLecture).lectureId
        is Runs -> (adapter.currentList[position] as Runs).id
        else -> null
    }

    override fun getPosition(key: String): Int =
        adapter.currentList.indexOfFirst {
            when (it) {
                is NotesModel -> it.nttUid == key
                is BookmarkModel -> it.bookmarkUid == key
                is ContentLecture -> it.lectureId == key
                is Runs -> it.id == key
                else -> false
            }
        }
}
