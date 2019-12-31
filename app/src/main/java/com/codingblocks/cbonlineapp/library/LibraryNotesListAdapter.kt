package com.codingblocks.cbonlineapp.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.util.VIDEO
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.cbonlineapp.util.extensions.secToTime
import com.codingblocks.cbonlineapp.util.extensions.timeAgo
import kotlinx.android.synthetic.main.item_note.view.*
import kotlinx.android.synthetic.main.item_note_player.view.*

class LibraryNotesListAdapter(val type: String = "") : ListAdapter<NotesModel, RecyclerView.ViewHolder>(DiffCallback()) {

    var onDeleteClick: DeleteNoteClickListener? = null
    var onEditClick: EditNoteClickListener? = null
    var onItemClick: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (type == VIDEO) {
            ItemVideoViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_note_player, parent, false)
            )
        } else {
            ItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_note, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (type == VIDEO)
            (holder as ItemVideoViewHolder).apply {
                bind(getItem(position))
                deleteClickListener = onDeleteClick
                editClickListener = onEditClick
                itemClickListener = onItemClick
            }
        else
            (holder as ItemViewHolder).bind(getItem(position))
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: NotesModel) = with(itemView) {

            noteTitleTv.text = item.contentTitle
            noteDescriptionTv.text = item.text
            noteTimeTv.text = item.createdAt.timeAgo()
        }
    }

    class ItemVideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var deleteClickListener: DeleteNoteClickListener? = null
        var editClickListener: EditNoteClickListener? = null
        var itemClickListener: ItemClickListener? = null


        fun bind(item: NotesModel) = with(itemView) {
            noteVTitleTv.text = item.contentTitle
            noteVDescriptionTv.text = item.text
            noteVTimeTv.text = item.createdAt.timeAgo()
            noteVCreateTv.text = item.duration.secToTime()
            noteVDeleteImg.setOnClickListener {
                deleteClickListener?.onClick(item.nttUid, adapterPosition, itemView)
            }
            noteVEditImg.setOnClickListener {
                editClickListener?.onClick(item)
            }
            setOnClickListener {
                itemClickListener?.onClick(item)
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<NotesModel>() {
    override fun areItemsTheSame(oldItem: NotesModel, newItem: NotesModel): Boolean {
        return oldItem.nttUid == newItem.nttUid
    }

    override fun areContentsTheSame(oldItem: NotesModel, newItem: NotesModel): Boolean {
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
    fun onClick(note: NotesModel)
}