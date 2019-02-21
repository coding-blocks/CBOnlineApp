package com.codingblocks.cbonlineapp.adapters

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.tiagohm.markdownview.MarkdownView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.database.*
import com.codingblocks.cbonlineapp.utils.formatDate
import com.codingblocks.cbonlineapp.utils.getPrefs
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Comment
import com.codingblocks.onlineapi.models.Contents
import com.codingblocks.onlineapi.models.DoubtsJsonApi
import com.codingblocks.onlineapi.models.RunAttemptsModel
import kotlinx.android.synthetic.main.item_doubt.view.*
import kotlinx.android.synthetic.main.item_notes.view.*
import java.util.ArrayList

class VideosNotesAdapter(private var notesData: ArrayList<NotesModel>) : RecyclerView.Adapter<VideosNotesAdapter.NotesViewHolder>() {


    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var contentDao: ContentDao
    private lateinit var doubtDao: DoubtsDao


    fun setData(notesData: ArrayList<NotesModel>) {
        this.notesData = notesData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        context = parent.context
        database = AppDatabase.getInstance(context)
        contentDao = database.contentDao()
        doubtDao = database.doubtsDao()


        return NotesViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_notes, parent, false))
    }

    override fun getItemCount(): Int {
        return notesData.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bindView(notesData[position])
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(note: NotesModel) {
            itemView.contentTitleTv.text = contentDao.getContentWithId(note.runAttemptId, note.contentId).title
            itemView.bodyTv.setText(note.text)
            itemView.timeTv.text = note.duration
            
            itemView.editTv.setOnClickListener {

            }

            itemView.deleteTv.setOnClickListener {

            }
        }

        private fun resolveDoubt(doubt: DoubtsModel) {
            val solvedDoubt = DoubtsJsonApi()
            solvedDoubt.body = doubt.body
            solvedDoubt.title = doubt.title
            val runAttempts = RunAttemptsModel() // type run-attempts
            val contents = Contents() // type contents
            runAttempts.id = doubt.runAttemptId
            contents.id = doubt.contentId
            solvedDoubt.status = "RESOLVED"
            solvedDoubt.discourseTopicId = doubt.discourseTopicId
            solvedDoubt.id = doubt.dbtUid
            solvedDoubt.resolvedById = (context as Activity).getPrefs().SP_USER_ID
            solvedDoubt.postrunAttempt = runAttempts
            solvedDoubt.content = contents
            Clients.onlineV2JsonApi.resolveDoubt(doubt.dbtUid, solvedDoubt).enqueue(retrofitCallback { throwable, response ->
                response?.body().let {
                    if (response?.isSuccessful!!) {
                        doubtDao.updateStatus(doubt.dbtUid, solvedDoubt.status)
                    }
                }
            })

        }

        private fun createComment(text: Editable?, doubt: DoubtsModel) {
            val comment = Comment()
            comment.body = text.toString()
            comment.discourseTopicId = doubt.discourseTopicId
            val doubts = DoubtsJsonApi() // type doubts
            doubts.id = doubt.dbtUid
            comment.doubt = doubts
            Clients.onlineV2JsonApi.createComment(comment).enqueue(retrofitCallback { throwable, response ->
                response?.body().let {
                    notifyDataSetChanged()
                }
            })
        }
    }
}