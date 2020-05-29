package com.codingblocks.cbonlineapp.mycourse.player.doubts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.admin.doubts.ChatClickListener
import com.codingblocks.cbonlineapp.baseclasses.BaseCBFragment
import com.codingblocks.cbonlineapp.dashboard.ChatActivity
import com.codingblocks.cbonlineapp.dashboard.DoubtCommentActivity
import com.codingblocks.cbonlineapp.dashboard.doubts.DashboardDoubtListAdapter
import com.codingblocks.cbonlineapp.dashboard.doubts.DoubtCommentClickListener
import com.codingblocks.cbonlineapp.dashboard.doubts.ResolveDoubtClickListener
import com.codingblocks.cbonlineapp.database.models.DoubtsModel
import com.codingblocks.cbonlineapp.mycourse.player.VideoPlayerViewModel
import com.codingblocks.cbonlineapp.util.CONVERSATION_ID
import com.codingblocks.cbonlineapp.util.DOUBT_ID
import com.codingblocks.cbonlineapp.util.extensions.observer
import com.codingblocks.cbonlineapp.util.extensions.setRv
import kotlinx.android.synthetic.main.fragment_video_doubt.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VideoDoubtFragment : BaseCBFragment(), AnkoLogger {

    private val viewModel by sharedViewModel<VideoPlayerViewModel>()
    private val doubtListAdapter = DashboardDoubtListAdapter()

    private val resolveClickListener: ResolveDoubtClickListener by lazy {
        object : ResolveDoubtClickListener {
            override fun onClick(doubt: DoubtsModel) {
//                if (doubt.status == RESOLVED) {
//                    requireContext().showDialog(RESOLVED)
//                } else {
//                    requireContext().showDialog(REOPENED)
//                }
                viewModel.resolveDoubt(doubt)
            }
        }
    }

    private val commentsClickListener: DoubtCommentClickListener by lazy {
        object : DoubtCommentClickListener {
            override fun onClick(doubtId: String) {
                requireContext().startActivity(
                    requireContext().intentFor<DoubtCommentActivity>(
                        DOUBT_ID to doubtId
                    ).singleTop()
                )
            }
        }
    }

    private val chatClickListener: ChatClickListener by lazy {
        object : ChatClickListener {
            override fun onClick(convId: String, doubtId: String) {
                requireContext().startActivity(
                    requireContext().intentFor<ChatActivity>(
                        CONVERSATION_ID to convId
                    ).singleTop()
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
        View? = inflater.inflate(R.layout.fragment_video_doubt, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerDoubtRv.setRv(requireContext(), doubtListAdapter, true, "thick")

        viewModel.doubts.observer(viewLifecycleOwner) {
            doubtListAdapter.submitList(it)
            showEmptyView(it.isNullOrEmpty())
        }

        doubtListAdapter.apply {
            onResolveClick = resolveClickListener
            onCommentClick = commentsClickListener
            onChatClick = chatClickListener
        }
    }

    private fun showEmptyView(show: Boolean) {
        playerDoubtRv.isVisible = !show
        noDoubtsLayout.isVisible = show
    }

    override fun onDestroyView() {
        doubtListAdapter.apply {
            onResolveClick = null
            onCommentClick = null
            onChatClick = null
        }
        super.onDestroyView()
    }
}
