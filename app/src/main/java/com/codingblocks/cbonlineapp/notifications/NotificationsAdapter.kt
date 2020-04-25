package com.codingblocks.cbonlineapp.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.commons.NotificationClickListener
import com.codingblocks.cbonlineapp.database.models.Notification

class NotificationsAdapter(diffCallback: NotificationsDiffCallback) :
    ListAdapter<Notification, NotificationsAdapter.NotificationsViewHolder>(
        diffCallback
    ) {

    var onClick: NotificationClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {

        return NotificationsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_admin_doubt, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        holder.apply {
            bindView(getItem(position))
            onClickListener = onClick
        }
    }

    inner class NotificationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var onClickListener: NotificationClickListener? = null

        fun bindView(item: Notification) {
//            ui.title.text = item.heading
//            ui.body.text = item.body
//            itemView.setOnClickListener {
//                onClickListener?.onClick(item.id, item.url, item.videoId)
//            }
//
//            if (item.seen) {
//                ui.title.alpha = 0.5f
//                ui.body.alpha = 0.5f
//            }
        }
    }
}

/**
 * The DiffUtil ItemCallback class for the [Notification] model class.
 * This enables proper diffing of items in Recycler Views using [DiffUtil]
 */
class NotificationsDiffCallback : DiffUtil.ItemCallback<Notification>() {

    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem == newItem
    }
}
