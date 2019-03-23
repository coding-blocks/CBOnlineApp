package com.codingblocks.cbonlineapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDao {

    @get:Query("SELECT * FROM notificationsData")
    val allNotification: List<NotificationData>


    @get:Query("SELECT COUNT(id) FROM notificationsData WHERE seen = 0 ")
    val count: Int

    @Insert
    fun addtolist(notifications: NotificationData): Long

    @Query("UPDATE notificationsData SET seen = 1 where id = :tid")
    fun updateseen(tid: Long)

    @Query("UPDATE notificationsData SET seen = 1 where seen != 1")
    fun updateseenAll()

    @Query("DELETE FROM notificationsData")
    fun nukeTable()

}