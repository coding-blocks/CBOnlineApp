package com.codingblocks.cbonlineapp.admin.doubts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.Doubts

class DoubtsAdapter : ListAdapter<Doubts, AdminDoubtsViewHolder>(diffCallback) {

    var onAckClick: AckClickListener? = null
    var onResolveClick: ResolveClickListener? = null
    var onChatClick: ChatClickListener? = null
    var onDiscussClick: DiscussClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminDoubtsViewHolder {
        return AdminDoubtsViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_doubt, parent, false))
    }

    override fun onBindViewHolder(holder: AdminDoubtsViewHolder, position: Int) {
        val doubt = getItem(position)
        if (doubt != null)
            holder.apply {
                bind(doubt)
                ackClickListener = onAckClick
                resolveClickListener = onResolveClick
                chatClickListener = onChatClick
                discussClickListener = onDiscussClick
            }
    }

    /**
     * The function to call when the adapter has to be cleared of items
     */
    fun clear() {
        this.submitList(null)
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<Doubts>() {
            override fun areItemsTheSame(oldItem: Doubts, newItem: Doubts): Boolean =
                oldItem.sameAndEqual(newItem)

            override fun areContentsTheSame(oldItem: Doubts, newItem: Doubts): Boolean =
                oldItem.id.sameAndEqual(newItem.id)
        }
    }
}

interface AckClickListener {
    fun onClick(doubtId: String, doubt: Doubts)
}

interface ResolveClickListener {
    fun onClick(doubtId: String, doubt: Doubts)
}

interface ChatClickListener {
    fun onClick(convId: String, doubtId: String)
}

interface DiscussClickListener {
    fun onClick(discordId: String)
}
