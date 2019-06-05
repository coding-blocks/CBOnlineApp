package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import org.junit.Assert.assertNotNull
import org.junit.Test

class OnlineRestApiPublicTest {

    val restapi = Clients.api

    @Test
    fun `GET courseRating`(){
        val rating = restapi.getCourseRating("18").execute().body()
        assertNotNull(rating)
    }

}
