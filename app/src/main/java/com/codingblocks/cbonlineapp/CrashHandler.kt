package com.codingblocks.cbonlineapp

import android.app.NotificationManager
import android.content.Context
import com.codingblocks.cbonlineapp.dashboard.DashboardActivity
import com.codingblocks.cbonlineapp.util.JWTUtils
import com.codingblocks.cbonlineapp.util.PreferenceHelper
import com.codingblocks.onlineapi.Clients
import org.jetbrains.anko.newTask
import org.koin.core.KoinComponent
import org.koin.core.inject

class CrashHandler(val context: Context) : Thread.UncaughtExceptionHandler, KoinComponent {

    private val sharedPrefs: PreferenceHelper by inject()

    private val defaultUEH: Thread.UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
    private var notificationManager: NotificationManager?
    override fun uncaughtException(t: Thread, e: Throwable) {
        if (notificationManager != null) {
            try {
                notificationManager!!.cancelAll()
                with(context) {
                    val key = sharedPrefs.SP_JWT_TOKEN_KEY
                    if (key.isNotEmpty() && !JWTUtils.isExpired(key)) {
                        Clients.authJwt = sharedPrefs.SP_JWT_TOKEN_KEY
                        Clients.refreshToken = sharedPrefs.SP_JWT_REFRESH_TOKEN
                        startActivity(DashboardActivity.createDashboardActivityIntent(this, true).newTask())
                    } else {
                        startActivity(DashboardActivity.createDashboardActivityIntent(this, false).newTask())
                    }
                }
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
