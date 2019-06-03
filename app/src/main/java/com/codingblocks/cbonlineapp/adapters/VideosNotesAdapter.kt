package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.NotesModel
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.extensions.secToTime
import com.codingblocks.cbonlineapp.util.OnItemClickListener
import com.codingblocks.cbonlineapp.viewmodels.VideoPlayerViewModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.ContentsId
import com.codingblocks.onlineapi.models.Notes
import com.codingblocks.onlineapi.models.RunAttemptsId
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_notes.view.bodyTv
import kotlinx.android.synthetic.main.item_notes.view.contentTitleTv
import kotlinx.android.synthetic.main.item_notes.view.deleteTv
import kotlinx.android.synthetic.main.item_notes.view.editTv
import kotlinx.android.synthetic.main.item_notes.view.timeTv
import org.jetbrains.anko.design.snackbar

class VideosNotesAdapter(
    private var notesData: ArrayList<NotesModel>,
    private val listener: OnItemClickListener,
    private val viewModel: VideoPlayerViewModel
) : RecyclerView.Adapter<VideosNotesAdapter.NotesViewHolder>() {

    private lateinit var context: Context

    fun setData(notesData: ArrayList<NotesModel>) {
        this.notesData = notesData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        context = parent.context

        return NotesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notes, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return notesData.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bindView(notesData[position], position)
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(note: NotesModel, position: Int) {
            itemView.contentTitleTv.text =
                viewModel.getContentWithId(note.runAttemptId, note.contentId).title
            itemView.bodyTv.setText(note.text)
            itemView.timeTv.text = secToTime(note.duration)

            itemView.setOnClickListener {
                listener.onItemClick(note.duration.toInt(), note.contentId)
            }

            itemView.editTv.setOnClickListener {
                if (itemView.editTv.text == "Edit") {
                    itemView.editTv.text = "Save"
                    itemView.deleteTv.text = "Cancel"
                    itemView.bodyTv.isEnabled = true
                } else {
                    createNote(note)
                }
            }

            itemView.deleteTv.setOnClickListener {
                if (itemView.deleteTv.text == "Delete") {
                    notesData.removeAt(position)
                    notifyItemRemoved(position)
                    itemView.snackbar("Deleted Accidentally ??", "Undo") {
                        notesData.add(position, note)
                        notifyItemInserted(position)
                    }.addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event == DISMISS_EVENT_TIMEOUT) {
                                Clients.onlineV2JsonApi.deleteNoteById(note.nttUid)
                                    .enqueue(retrofitCallback { _, response ->
                                        response.let {
                                            if (it?.isSuccessful == true) {
                                                viewModel.deleteNoteByID(note.nttUid)
                                            }
                                        }
                                    })
                            }
                        }
                    }).setActionTextColor(context.resources.getColor(R.color.salmon))
                } else {
                    itemView.editTv.text = "Edit"
                    itemView.deleteTv.text = "Delete"
                    itemView.bodyTv.isEnabled = false
                }
            }
        }

        private fun createNote(notesModel: NotesModel) {
            val note = Notes()
            note.text = itemView.bodyTv.text.toString()
            note.duration = notesModel.duration
            note.runAttempt = RunAttemptsId(notesModel.runAttemptId)
            note.content = ContentsId(notesModel.contentId)
            notesModel.text = itemView.bodyTv.text.toString()
            Clients.onlineV2JsonApi.updateNoteById(notesModel.nttUid, note)
                .enqueue(retrofitCallback { _, response ->
                    response?.body().let {
                        if (response?.isSuccessful == true)
                            try {
                                itemView.editTv.text = "Edit"
                                itemView.deleteTv.text = "Delete"
                                itemView.bodyTv.isEnabled = false
                                viewModel.updateNote(notesModel)
                            } catch (e: Exception) {
                            }
                    }
                })
        }
    }
}
