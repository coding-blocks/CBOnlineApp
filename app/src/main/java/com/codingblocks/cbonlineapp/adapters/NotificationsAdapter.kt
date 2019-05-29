package com.codingblocks.cbonlineapp.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.commons.NotificationClickListener
import com.codingblocks.cbonlineapp.commons.NotificationsDiffCallback
import com.codingblocks.cbonlineapp.database.models.Notification
import com.codingblocks.cbonlineapp.ui.NotificationCardUi
import org.jetbrains.anko.AnkoContext

class NotificationsAdapter(diffCallback: NotificationsDiffCallback) :
    ListAdapter<Notification, NotificationsAdapter.NotificationsViewHolder>(
        diffCallback
    ) {

    val ui = NotificationCardUi()
    var onClick: NotificationClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationsViewHolder {

        return NotificationsViewHolder(ui.createView(AnkoContext.create(parent.context, parent)))
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
            ui.title.text = item.heading
            ui.body.text = item.body
            itemView.setOnClickListener {
                onClickListener?.onClick(item.id, item.url, item.videoId)
            }

            if (item.seen) {
                ui.title.alpha = 0.5f
                ui.body.alpha = 0.5f
            }
        }
    }
}
