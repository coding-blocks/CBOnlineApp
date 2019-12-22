package com.codingblocks.cbonlineapp.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.doubts.CommentsListAdapter
import com.codingblocks.cbonlineapp.dashboard.doubts.DashboardDoubtsViewModel
import com.codingblocks.cbonlineapp.util.DOUBT_ID
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import com.codingblocks.cbonlineapp.util.extensions.setToolbar
import com.codingblocks.cbonlineapp.util.extensions.timeAgo
import kotlinx.android.synthetic.main.activity_doubt_comment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DoubtCommentActivity : AppCompatActivity() {

    private val doubtId: String by lazy {
        intent.getStringExtra(DOUBT_ID)
    }

    private val viewModel by viewModel<DashboardDoubtsViewModel>()
    private val commentsListAdapter = CommentsListAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO(Add Chat,Resolve and Post Comment)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doubt_comment)
        setToolbar(commentsToolbar)
        commentRv.setRv(this@DoubtCommentActivity, commentsListAdapter)

        viewModel.getDoubt(doubtId).observer(this) {
            doubtTitleTv.text = it.title
            doubtDescriptionTv.text = it.body
            doubtTimeTv.text = it.createdAt.timeAgo()
            chatTv.isVisible = !it.conversationId.isNullOrEmpty()
        }

        viewModel.getComments(doubtId).observer(this) {
            commentsListAdapter.submitList(it)
        }
    }
}

