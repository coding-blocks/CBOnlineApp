package com.codingblocks.cbonlineapp

import android.app.NotificationManager
import android.content.Context
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class CrashHandler(val context: Context) : Thread.UncaughtExceptionHandler {
    private val defaultUEH: Thread.UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
    private var notificationManager: NotificationManager?
    override fun uncaughtException(t: Thread, e: Throwable) {
        if (notificationManager != null) {
            try {
                notificationManager!!.cancelAll()
                with(context) { startActivity(intentFor<DashboardActivity>().newTask()) }
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }
        notificationManager = null
        defaultUEH.uncaughtException(t, e)
    }

    init {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}
