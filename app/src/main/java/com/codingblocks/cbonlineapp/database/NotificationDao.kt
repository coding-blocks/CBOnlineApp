package com.codingblocks.cbonlineapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.codingblocks.cbonlineapp.database.models.Notification

@Dao
abstract class NotificationDao : BaseDao<Notification> {

    @Query("UPDATE Notification SET seen = 1 where id = :tid")
    abstract fun updateseen(tid: Long)

    @Query("UPDATE Notification SET seen = 1 where seen != 1")
    abstract fun updateseenAll()

    @Query("DELETE FROM Notification where id = :uid")
    abstract fun deleteNotificationByID(uid: Long)

    @Query("DELETE FROM Notification")
    abstract fun nukeTable()

    @get:Query("SELECT COUNT(id) FROM Notification WHERE seen = 0 ")
    abstract val count: Int

    @get:Query("SELECT * FROM Notification")
    abstract val allNotification: LiveData<List<Notification>>

    @get:Query("SELECT * FROM Notification")
    abstract val allNotificationNonLive: List<Notification>
}
