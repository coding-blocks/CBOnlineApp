package com.codingblocks.cbonlineapp.dashboard

import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.analytics.AppCrashlyticsWrapper
import com.codingblocks.cbonlineapp.baseclasses.BaseCBActivity
import com.codingblocks.cbonlineapp.dashboard.doubts.CommentsListAdapter
import com.codingblocks.cbonlineapp.dashboard.doubts.DashboardDoubtsViewModel
import com.codingblocks.cbonlineapp.util.CONVERSATION_ID
import com.codingblocks.cbonlineapp.util.DOUBT_ID
import com.codingblocks.cbonlineapp.util.PENDING
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.cbonlineapp.util.REOPENED
import com.codingblocks.cbonlineapp.util.RESOLVED
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.extensions.showDialog
import com.codingblocks.cbonlineapp.util.extensions.showSnackbar
import com.codingblocks.cbonlineapp.util.extensions.timeAgo
import com.google.android.material.snackbar.Snackbar
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.activity_doubt_comment.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class DoubtCommentActivity : BaseCBActivity() {

    private val doubtId: String by lazy {
        intent.getStringExtra(DOUBT_ID)
    }
    private var discourseId: String = ""
    private val sharedPrefs by inject<PreferenceHelper>()
    private val viewModel by viewModel<DashboardDoubtsViewModel>()
    private val commentsListAdapter = CommentsListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doubt_comment)
        setToolbar(commentsToolbar)
        commentRv.setRv(this@DoubtCommentActivity, commentsListAdapter)

        viewModel.getDoubt(doubtId).observer(this) {
            doubtTitleTv.text = it.title
            val markWon = Markwon.create(this)
            markWon.setMarkdown(doubtDescriptionTv, it.body)
            doubtTimeTv.text = it.createdAt.timeAgo()
            discourseId = it.discourseTopicId
            chatTv.apply {
                isVisible = !it.conversationId.isNullOrEmpty()
                setOnClickListener { _ ->
                    startActivity(intentFor<ChatActivity>(CONVERSATION_ID to it.conversationId).singleTop())
                }
            }

            markResolvedTv.apply {
                text = when (it.status) {
                    RESOLVED -> {
                        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_reopen_small), null)
                        setTextColor(resources.getColor(R.color.neon_red))
                        setOnClickListener { _ ->
                            viewModel.resolveDoubt(it.apply {
                                status = PENDING
                            }, true)
                            showDialog(REOPENED, cancelable = true) {
                                onBackPressed()
                            }
                        }
                        context.getString(R.string.reopen_doubt)
                    }
                    else -> {
                        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_tick), null)
                        setTextColor(resources.getColor(R.color.freshGreen))
                        setOnClickListener { _ ->
                            viewModel.resolveDoubt(it.apply {
                                status = RESOLVED
                            }, true)
                            showDialog(RESOLVED, cancelable = true) {
                                onBackPressed()
                            }
                        }
                        context.getString(R.string.mark_resolved)
                    }
                }
            }
        }

        viewModel.getComments(doubtId).observer(this) {
            commentsListAdapter.submitList(it)
        }

        commentBox.hint = "${getString(R.string.commenting_as)} ${sharedPrefs.SP_NAME} ...."
        sendBtn.setOnClickListener {
            if (commentBox.text.length < 20)
                rootComment.showSnackbar(
                    "Length is too Short.Minimum of 20 Characters are required",
                    Snackbar.LENGTH_SHORT
                )
            else
                viewModel.createComment(commentBox.text.toString(), doubtId, discourseId)
        }

        viewModel.errorLiveData.observer(this) {
            rootComment.showSnackbar(it, Snackbar.LENGTH_SHORT)
            AppCrashlyticsWrapper.log(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
