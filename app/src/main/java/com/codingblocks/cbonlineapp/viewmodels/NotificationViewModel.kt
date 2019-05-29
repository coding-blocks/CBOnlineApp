package com.codingblocks.cbonlineapp.viewmodels

import androidx.lifecycle.ViewModel
import com.codingblocks.cbonlineapp.database.NotificationDao

class NotificationViewModel(
    var notificationDao: NotificationDao
) : ViewModel()
