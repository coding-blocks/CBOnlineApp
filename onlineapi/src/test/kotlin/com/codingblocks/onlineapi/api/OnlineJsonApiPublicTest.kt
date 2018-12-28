package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import org.junit.Assert.assertEquals
import org.junit.Test

class OnlineJsonApiPublicTest {
    val api = Clients.onlineV2JsonApi

    @Test
    fun `GET courses`() {
        val courses = api.courses.execute().body()
        courses?.let {
            assertEquals(20, it.size)
        }
    }


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
    fun `GET instructors?include=courses`() {
        val courses = api.instructors(arrayOf("courses")).execute().body()
        courses?.let {
            assertEquals(16, it.size)
        }
    }

    @Test
    fun `GET recommended`() {
        val courses = api.getRecommendedCourses().execute().body()
        courses?.let {
            assertEquals(12, it.size)
        }
    }

}