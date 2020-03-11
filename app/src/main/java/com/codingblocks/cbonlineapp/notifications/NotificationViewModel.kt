package com.codingblocks.cbonlineapp.notifications

import com.codingblocks.cbonlineapp.baseclasses.BaseCBViewModel
import com.codingblocks.cbonlineapp.database.NotificationDao

class NotificationViewModel(
    private val notificationDao: NotificationDao
) : BaseCBViewModel()
