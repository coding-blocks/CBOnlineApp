package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class OnlineJsonApiPublicTest {
    val api = Clients.onlineV2JsonApi

    @Test
    fun `GET courses|{id}`() {
        val courses = api.courseById("26").execute().body()
        courses?.let {
            assertEquals("Algo++ Online", it.title)
        }
    }

    @Test
    fun `GET instructors`() {
        val courses = api.instructorsById("6").execute().body()
        courses?.let {
            assertEquals("Arnav Gupta", it.name)
        }
    }

    @Test
    fun `GET recommended`() {
        val courses = api.getRecommendedCourses().execute().body()
        assertNotNull(courses)
        courses?.let {
            assertEquals(true, it.size > 1)
        }
    }

}
