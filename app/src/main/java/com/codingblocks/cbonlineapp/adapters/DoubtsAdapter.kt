package com.codingblocks.cbonlineapp.adapters

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
import com.codingblocks.cbonlineapp.database.DoubtsModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Comment
import com.codingblocks.onlineapi.models.DoubtsJsonApi
import kotlinx.android.synthetic.main.item_doubt.view.*
import java.text.SimpleDateFormat
import java.util.*



class DoubtsAdapter(private var doubtsData: ArrayList<DoubtsModel>) : RecyclerView.Adapter<DoubtsAdapter.DoubtsViewHolder>() {


    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var contentDao: ContentDao

    fun setData(doubtsData: ArrayList<DoubtsModel>) {
        this.doubtsData = doubtsData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtsViewHolder {
        context = parent.context
        database = AppDatabase.getInstance(context)
        contentDao = database.contentDao()

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
                itemView.resolveDoubtTv.setOnClickListener {

                }
            }
            itemView.doubtComment.editText?.setOnEditorActionListener { textView, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    createComment(itemView.doubtComment.editText!!.text, doubt)
                    itemView.doubtComment.editText!!.text.clear()
                    return@setOnEditorActionListener true
                }

                return@setOnEditorActionListener false
            }
        }

        private fun fetchComments(dbtUid: String) {
            Clients.onlineV2JsonApi.getCommentsById(dbtUid).enqueue(retrofitCallback { throwable, response ->
                response?.body().let {
                    if (it != null) {
                        if (it.isNotEmpty()) {
                            itemView.showCommentsTv.visibility = View.VISIBLE
                            itemView.setOnClickListener {
                                if (itemView.commentll.visibility == View.VISIBLE) {
                                    itemView.showCommentsTv.text = "Show Comments"
                                    itemView.commentll.visibility = View.GONE
                                } else {
                                    itemView.commentll.visibility = View.VISIBLE
                                    itemView.showCommentsTv.text = "Hide Comments"
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

                }
            })
        }
    }
}