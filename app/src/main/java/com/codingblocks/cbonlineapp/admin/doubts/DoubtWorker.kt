package com.codingblocks.cbonlineapp.admin.doubts

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.ADMIN_CHANNEL_ID
import com.codingblocks.cbonlineapp.util.extensions.isotomillisecond
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.ResultWrapper
import com.codingblocks.onlineapi.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DoubtWorker(val context: Context, private val workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun doWork(): Result {

        GlobalScope.launch {
            when (val response = safeApiCall(Dispatchers.IO) { Clients.onlineV2JsonApi.getLiveDoubts() }) {
                is ResultWrapper.Success -> with(response.value) {
                    if (isSuccessful)
                        if (!body()?.get().isNullOrEmpty()) {
                            body()?.get()?.get(0)?.apply {
                                Log.i("Notification Worker", "CurrentTime ${System.currentTimeMillis()} AckTime ${createdAt.isotomillisecond()}")
                                if ((System.currentTimeMillis() - createdAt.isotomillisecond()) / 1000 < 900000) {
                                    showNotification()
                                }
                            }
                        }
                }
            }
        }
        return Result.success()
    }

    private fun showNotification() {
        val notification = NotificationCompat.Builder(context, ADMIN_CHANNEL_ID).apply {
            setContentTitle("New Doubts to Resolve !!!!!")
            setSmallIcon(R.drawable.ic_conversation)
            setOnlyAlertOnce(true)
            setContentText("Students are waiting for your response.")
            setAutoCancel(true)
            setDefaults(NotificationCompat.DEFAULT_VIBRATE)
        }.build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
