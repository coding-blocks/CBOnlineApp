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
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.extensions.formatDate
import com.codingblocks.cbonlineapp.extensions.getPrefs
import com.codingblocks.cbonlineapp.extensions.retrofitCallback
import com.codingblocks.cbonlineapp.viewmodels.VideoPlayerViewModel
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Comment
import com.codingblocks.onlineapi.models.ContentsId
import com.codingblocks.onlineapi.models.DoubtsJsonApi
import com.codingblocks.onlineapi.models.RunAttemptsId
import kotlinx.android.synthetic.main.item_doubt.view.commentll
import kotlinx.android.synthetic.main.item_doubt.view.doubtComment
import kotlinx.android.synthetic.main.item_doubt.view.doubtDescription
import kotlinx.android.synthetic.main.item_doubt.view.doubtTitle
import kotlinx.android.synthetic.main.item_doubt.view.doubtTopic
import kotlinx.android.synthetic.main.item_doubt.view.resolveDoubtTv
import kotlinx.android.synthetic.main.item_doubt.view.showCommentsTv

class VideosDoubtsAdapter(
    private var doubtsData: ArrayList<DoubtsModel>,
    private var viewModel: VideoPlayerViewModel
) : RecyclerView.Adapter<VideosDoubtsAdapter.DoubtsViewHolder>() {

    private lateinit var context: Context

    fun setData(doubtsData: ArrayList<DoubtsModel>) {
        this.doubtsData = doubtsData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtsViewHolder {
        context = parent.context

        return DoubtsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_doubt, parent, false)
        )
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
            itemView.doubtTopic.text =
                viewModel.getContentWithId(doubt.runAttemptId, doubt.contentId).title
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
            itemView.doubtComment.editText?.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (itemView.doubtComment.editText?.text.toString().length < 15 || itemView.doubtComment.editText?.text.toString().isEmpty()) {
                        itemView.doubtComment.error =
                            "Comment length must be at-least 15 characters."
                        return@setOnEditorActionListener false
                    } else {
                        createComment(itemView.doubtComment.editText?.text, doubt)
                        itemView.doubtComment.editText?.text?.clear()
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
            solvedDoubt.status = "RESOLVED"
            solvedDoubt.discourseTopicId = doubt.discourseTopicId
            solvedDoubt.id = doubt.dbtUid
            solvedDoubt.resolvedById = (context as Activity).getPrefs().SP_USER_ID
            solvedDoubt.postrunAttempt = RunAttemptsId(doubt.runAttemptId)
            solvedDoubt.contents = ContentsId(doubt.contentId)
            Clients.onlineV2JsonApi.resolveDoubt(doubt.dbtUid, solvedDoubt)
                .enqueue(retrofitCallback { _, response ->
                    response?.body().let {
                        if (response?.isSuccessful == true) {
                            viewModel.updateDoubtStatus(doubt.dbtUid, solvedDoubt.status)
                        }
                    }
                })
        }

        private fun fetchComments(dbtUid: String) {
            Clients.onlineV2JsonApi.getCommentsById(dbtUid)
                .enqueue(retrofitCallback { throwable, response ->
                    response?.body()?.let {
                        if (it.isNotEmpty()) {
                            itemView.showCommentsTv.visibility = View.VISIBLE
                            itemView.setOnClickListener {
                                if (itemView.commentll.visibility == View.VISIBLE) {
                                    itemView.showCommentsTv.text =
                                        context.getString(R.string.showComments)
                                    itemView.commentll.visibility = View.GONE
                                } else {
                                    itemView.commentll.visibility = View.VISIBLE
                                    itemView.showCommentsTv.text =
                                        context.getString(R.string.hideComments)
                                }
                            }
                            val ll = itemView.findViewById<LinearLayout>(R.id.commentll)
                            ll.removeAllViews()
                            ll.orientation = LinearLayout.VERTICAL
                            for (comment in it) {
                                val factory = LayoutInflater.from(context)
                                val inflatedView =
                                    factory.inflate(R.layout.item_comment, ll, false)
                                val subTitle =
                                    inflatedView.findViewById(R.id.usernameTv) as TextView
                                val time = inflatedView.findViewById(R.id.timeTv) as TextView
                                val body =
                                    inflatedView.findViewById(R.id.bodyTv) as MarkdownView
                                body.loadMarkdown(comment.body)
                                subTitle.text = comment.username
                                time.text =
                                    formatDate(comment.updatedAt)
                                ll.addView(inflatedView)
                            }
                        }
                    }
                    throwable?.let {
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
            Clients.onlineV2JsonApi.createComment(comment)
                .enqueue(retrofitCallback { _, response ->
                    if (response?.isSuccessful == true)
                        notifyDataSetChanged()
                })
        }
    }
}
