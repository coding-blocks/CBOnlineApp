package com.codingblocks.cbonlineapp.viewmodels

import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.NotificationDao

class NotificationViewModel(
    private val notificationDao: NotificationDao
) : ViewModel()
