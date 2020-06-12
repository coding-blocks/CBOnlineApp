package com.codingblocks.cbonlineapp.util.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.codingblocks.cbonlineapp.util.Actions
import com.codingblocks.cbonlineapp.util.EndlessService
import com.codingblocks.cbonlineapp.util.ServiceState
import com.codingblocks.cbonlineapp.util.getServiceState

class StartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && getServiceState(context) == ServiceState.STARTED) {
            Intent(context, EndlessService::class.java).also {
                it.action = Actions.START.name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(it)
                    return
                } else {
                    context.startService(it)
                }
            }
        }
    }
}
