package com.codingblocks.cbonlineapp.commons

/**
 * The callback interface for Event item clicks
 */
interface NotificationClickListener {
    /**
     * The function to be invoked when an event item is clicked
     *
     * @param notificationID The ID of the clicked event
     * @param url The url for routing to required activity
     * @param videoId The videoId for playing video
     */
    fun onClick(notificationID: Long, url: String, videoId: String)
}
