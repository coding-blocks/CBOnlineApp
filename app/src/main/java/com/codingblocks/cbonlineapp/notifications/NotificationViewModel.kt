package com.codingblocks.cbonlineapp.notifications

import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.NotificationDao

class NotificationViewModel(
    private val notificationDao: NotificationDao
) : ViewModel()
