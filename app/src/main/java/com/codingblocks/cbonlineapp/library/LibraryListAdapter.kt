package com.codingblocks.cbonlineapp.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.BaseModel
import com.codingblocks.cbonlineapp.database.models.BookmarkModel
import com.codingblocks.cbonlineapp.database.models.ContentLecture
import com.codingblocks.cbonlineapp.database.models.LibraryTypes
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.util.extensions.getDurationBreakdown
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.cbonlineapp.util.extensions.secToTime
import com.codingblocks.cbonlineapp.util.extensions.timeAgo
import kotlinx.android.synthetic.main.item_bookmark.view.bookmarkSubtitleTv
import kotlinx.android.synthetic.main.item_bookmark.view.bookmarkTimeTv
import kotlinx.android.synthetic.main.item_bookmark.view.bookmarkTitleTv
import kotlinx.android.synthetic.main.item_download.view.downloadArwBtn
import kotlinx.android.synthetic.main.item_download.view.downloadSubtitleTv
import kotlinx.android.synthetic.main.item_download.view.downloadTitleTv
import kotlinx.android.synthetic.main.item_note.view.*
import kotlinx.android.synthetic.main.item_note.view.selectionImg
import kotlinx.android.synthetic.main.item_note_player.view.*

class LibraryListAdapter(val type: LibraryTypes) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(DiffCallback()) {

    var onDeleteClick: DeleteNoteClickListener? = null
    var onEditClick: EditNoteClickListener? = null
    var onItemClick: ItemClickListener? = null
    var tracker: SelectionTracker<String>? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (type) {
            LibraryTypes.NOTESVIDEO -> NoteVideoViewHolder(
                inflater.inflate(R.layout.item_note_player, parent, false))
            LibraryTypes.NOTE -> NoteViewHolder(
                inflater.inflate(R.layout.item_note, parent, false))
            LibraryTypes.BOOKMARK -> BookmarkViewHolder(
                inflater.inflate(R.layout.item_bookmark, parent, false))
            LibraryTypes.DOWNLOADS -> DownloadViewHolder(
                inflater.inflate(R.layout.item_download, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (type) {
            LibraryTypes.NOTESVIDEO -> {
                (holder as NoteVideoViewHolder).apply {
                    bind(getItem(position) as NotesModel)
                    deleteClickListener = onDeleteClick
                    editClickListener = onEditClick
                    itemClickListener = onItemClick
                }
            }
            LibraryTypes.NOTE -> {
                (holder as NoteViewHolder).apply {
                    tracker?.let {
                        val item = getItem(position) as NotesModel
                        bind(item, it.isSelected(item.nttUid))
                    }
                }
            }
            LibraryTypes.BOOKMARK -> {
                (holder as BookmarkViewHolder).apply {
                    tracker?.let {
                        val item = getItem(position) as BookmarkModel
                        bind(item, it.isSelected(item.bookmarkUid))
                        itemClickListener = onItemClick
                    }
                }
            }

            LibraryTypes.DOWNLOADS -> {
                (holder as DownloadViewHolder).apply {
                    tracker?.let {
                        val item = getItem(position) as ContentLecture
                        bind(item, it.isSelected(item.lectureId))
                        itemClickListener = onItemClick
                    }
                }
            }
        }
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: NotesModel, isActivated: Boolean = false) = with(itemView) {

            noteTitleTv.text = item.contentTitle
            noteDescriptionTv.text = item.text
            noteTimeTv.text = item.createdAt.timeAgo()
            selectionImg.isVisible = isActivated
            noteTimeTv.isVisible = !isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): String? = (getItem(adapterPosition) as NotesModel).nttUid
            }
    }

    class NoteVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var deleteClickListener: DeleteNoteClickListener? = null
        var editClickListener: EditNoteClickListener? = null
        var itemClickListener: ItemClickListener? = null

        fun bind(item: NotesModel) = with(itemView) {
            //            noteVTitleTv.text = item.contentTitle
            noteVDescriptionTv.text = item.text
            noteVTimeTv.text = item.createdAt.timeAgo()
            noteVCreateTv.text = item.duration.secToTime()
//            noteVDeleteImg.setOnClickListener {
//                deleteClickListener?.onClick(item.nttUid, adapterPosition, itemView)
//            }
            noteVEditImg.setOnClickListener {
                editClickListener?.onClick(item)
            }
            setOnClickListener {
                itemClickListener?.onClick(item)
            }
        }
    }

    inner class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemClickListener: ItemClickListener? = null

        fun bind(item: BookmarkModel, isActivated: Boolean = false) = with(itemView) {

            bookmarkTitleTv.text = item.sectionName
            bookmarkSubtitleTv.text = item.contentName
            bookmarkTimeTv.apply {
                text = item.createdAt.timeAgo()
                isVisible = !isActivated
            }
            selectionImg.isVisible = isActivated

            setOnClickListener {
                itemClickListener?.onClick(item)
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): String? = (getItem(adapterPosition) as BookmarkModel).bookmarkUid
            }
    }

    inner class DownloadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemClickListener: ItemClickListener? = null

        fun bind(item: ContentLecture, isActivated: Boolean = false) = with(itemView) {

            downloadTitleTv.text = item.lectureName
            downloadSubtitleTv.text = "Duration ${item.lectureDuration.getDurationBreakdown()}"
            downloadArwBtn.apply {
                isVisible = isActivated
                isVisible = !isActivated
            }
            selectionImg.isVisible = isActivated

            setOnClickListener {
                itemClickListener?.onClick(item)
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): String? = (getItem(adapterPosition) as ContentLecture).lectureId
            }
    }
}

class DiffCallback : DiffUtil.ItemCallback<BaseModel>() {
    override fun areItemsTheSame(oldItem: BaseModel, newItem: BaseModel):
        Boolean = when (oldItem) {
        is NotesModel -> if (newItem is NotesModel) oldItem.nttUid == newItem.nttUid else false
        is BookmarkModel -> if (newItem is BookmarkModel) oldItem.bookmarkUid == newItem.bookmarkUid else false
        is ContentLecture -> if (newItem is ContentLecture) oldItem.lectureId == newItem.lectureId else false
        else -> false
    }

    override fun areContentsTheSame(oldItem: BaseModel, newItem: BaseModel): Boolean {
        return oldItem.sameAndEqual(newItem)
    }
}

interface DeleteNoteClickListener {
    fun onClick(noteId: String, position: Int, view: View)
}

interface EditNoteClickListener {
    fun onClick(note: NotesModel)
}

interface ItemClickListener {
    fun onClick(item: BaseModel)
}
