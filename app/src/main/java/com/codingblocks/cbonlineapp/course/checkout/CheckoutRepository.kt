package com.codingblocks.cbonlineapp.course.checkout

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.safeApiCall

/**
 * @author aggarwalpulkit596
 */
class CheckoutRepository {
    suspend fun getCourse(id: String) = safeApiCall { Clients.onlineV2JsonApi.getCourse(id) }
}
