package com.codingblocks.cbonlineapp.commons

import androidx.recyclerview.widget.DiffUtil
import com.codingblocks.cbonlineapp.database.models.Notification

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
