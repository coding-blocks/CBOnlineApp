package com.codingblocks.cbonlineapp.dbtest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import androidx.test.runner.AndroidJUnit4
import com.codingblocks.cbonlineapp.database.AppDatabase
import com.codingblocks.cbonlineapp.database.models.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withContext
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class NotificationDaoTest {

    @get:Rule

    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase

    @Before
    fun init(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun closeDb(){
        database.close()
    }

    @Test
    fun insertNotificationAndGetById() {
        val notification = Notification(
            body = "Hi :) This is my First Notification",
            heading = "First Notification",
            url = "cb.lk/android",
            videoId = "bgd6sjk")

        val row  =  database.notificationDao().insertWithId(notification)

        val loaded = database.notificationDao().getNotification(1)

        val list = database.notificationDao().allNotificationNonLive

        assertThat(row,`is`(notNullValue()))
        assertThat(loaded,`is`(nullValue()))
        assertThat(list, `is`(nullValue()))

    }

}
