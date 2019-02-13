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
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitCallback
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.ContentDao
import com.codingblocks.cbonlineapp.database.DoubtsDao
import com.codingblocks.cbonlineapp.database.DoubtsModel
import com.codingblocks.cbonlineapp.utils.getPrefs
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Comment
import com.codingblocks.onlineapi.models.Contents
import com.codingblocks.onlineapi.models.DoubtsJsonApi
import com.codingblocks.onlineapi.models.RunAttemptsModel
import kotlinx.android.synthetic.main.item_doubt.view.*
import java.text.SimpleDateFormat
import java.util.*


class VideosDoubtsAdapter(private var doubtsData: ArrayList<DoubtsModel>) : RecyclerView.Adapter<VideosDoubtsAdapter.DoubtsViewHolder>() {


    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var contentDao: ContentDao
    private lateinit var doubtDao: DoubtsDao


    fun setData(doubtsData: ArrayList<DoubtsModel>) {
        this.doubtsData = doubtsData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtsViewHolder {
        context = parent.context
        database = AppDatabase.getInstance(context)
        contentDao = database.contentDao()
        doubtDao = database.doubtsDao()


        return DoubtsViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_doubt, parent, false))
    }

    override fun getItemCount(): Int {
        return doubtsData.size
    }

    override fun onBindViewHolder(holder: DoubtsViewHolder, position: Int) {
        holder.bindView(doubtsData[position])
    }

    inner class DoubtsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(doubt: DoubtsModel) {
            fetchComments(doubt.dbtUid)
            itemView.doubtTopic.text = contentDao.getContentWithId(doubt.runAttemptId, doubt.contentId).title
            itemView.doubtTitle.text = doubt.title
            itemView.doubtDescription.text = doubt.body
            if (doubt.status == "RESOLVED") {
                itemView.resolveDoubtTv.visibility = View.GONE
            } else {
                itemView.resolveDoubtTv.visibility = View.VISIBLE
                itemView.resolveDoubtTv.setOnClickListener {
                    resolveDoubt(doubt)
                }
            }
            itemView.doubtComment.editText?.setOnEditorActionListener { textView, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (itemView.doubtComment.editText!!.text.length < 15 || itemView.doubtComment.editText!!.text.isEmpty()) {
                        itemView.doubtComment.error = "Comment length must be at-least 15 characters."
                        return@setOnEditorActionListener false
                    } else {
                        createComment(itemView.doubtComment.editText!!.text, doubt)
                        itemView.doubtComment.editText!!.text.clear()
                        return@setOnEditorActionListener true
                    }
                }

                return@setOnEditorActionListener false
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
                        doubtDao.updateStatus(doubt.dbtUid,solvedDoubt.status)
                    }
                }
            })

        }

        private fun fetchComments(dbtUid: String) {
            Clients.onlineV2JsonApi.getCommentsById(dbtUid).enqueue(retrofitCallback { throwable, response ->
                response?.body().let {
                    if (it != null) {
                        if (it.isNotEmpty()) {
                            itemView.showCommentsTv.visibility = View.VISIBLE
                            itemView.setOnClickListener {
                                if (itemView.commentll.visibility == View.VISIBLE) {
                                    itemView.showCommentsTv.text = context.getString(R.string.showComments)
                                    itemView.commentll.visibility = View.GONE
                                } else {
                                    itemView.commentll.visibility = View.VISIBLE
                                    itemView.showCommentsTv.text = context.getString(R.string.hideComments)
                                }
                            }
                            val ll = itemView.findViewById<LinearLayout>(R.id.commentll)
                            ll.removeAllViews()
                            ll.orientation = LinearLayout.VERTICAL
                            for (comment in it) {
                                val factory = LayoutInflater.from(context)
                                val inflatedView = factory.inflate(R.layout.item_comment, ll, false)
                                val subTitle = inflatedView.findViewById(R.id.usernameTv) as TextView
                                val time = inflatedView.findViewById(R.id.timeTv) as TextView
                                val body = inflatedView.findViewById(R.id.bodyTv) as TextView
                                body.text = comment.body
                                subTitle.text = comment.username
                                var format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                                val newDate = format.parse(comment.updatedAt)

                                format = SimpleDateFormat("MMM dd,yyyy hh:mm", Locale.US)
                                val date = format.format(newDate)


                                time.text = date
                                ll.addView(inflatedView)
                            }

                        }
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