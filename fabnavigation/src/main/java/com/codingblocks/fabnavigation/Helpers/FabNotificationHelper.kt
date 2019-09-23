package com.codingblocks.fabnavigation.Helpers

import androidx.annotation.ColorInt
import com.codingblocks.fabnavigation.FabNotification

object FabNotificationHelper {

    /**
     * Get text color for given notification. If color is not set (0), returns default value.
     *
     * @param notification     FabNotification, non null
     * @param defaultTextColor int default text color for all notifications
     * @return
     */
    fun getTextColor(notification: FabNotification, @ColorInt defaultTextColor: Int): Int {
        val textColor = notification.textColor
        return if (textColor == 0) defaultTextColor else textColor
    }

    /**
     * Get background color for given notification. If color is not set (0), returns default value.
     *
     * @param notification           FabNotification, non null
     * @param defaultBackgroundColor int default background color for all notifications
     * @return
     */
    fun getBackgroundColor(notification: FabNotification, @ColorInt defaultBackgroundColor: Int): Int {
        val backgroundColor = notification.backgroundColor
        return if (backgroundColor == 0) defaultBackgroundColor else backgroundColor
    }

}// empty
