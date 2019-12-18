package com.codingblocks.cbonlineapp.dashboard.doubts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.DOUBT_ID
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.timeAgo
import kotlinx.android.synthetic.main.activity_doubt_comment.*
import kotlinx.android.synthetic.main.item_doubts.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DoubtCommentActivity : AppCompatActivity() {

    val doubtId: String by lazy {
        intent.getStringExtra(DOUBT_ID)
    }

    private val viewModel by viewModel<DashboardDoubtsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doubt_comment)
        setSupportActionBar(commentsToolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.getDoubt(doubtId).observer(this) {
            doubtTitleTv.text = it.title
            doubtDescriptionTv.text = it.body
            doubtTimeTv.text = it.createdAt.timeAgo()
            commentTv.isVisible = false
            chatTv.isVisible = !it.conversationId.isNullOrEmpty()
        }
    }
}
