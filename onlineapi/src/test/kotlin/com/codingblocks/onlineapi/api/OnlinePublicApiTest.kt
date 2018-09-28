package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OnlinePublicApiTest {
    val api = Clients.onlineV2PublicClient

    @Test fun `GET courses`() {
        val courses = api.courses.execute().body()
        courses?.let {
            assertEquals(20, it.size)
        }
    }
    @Test fun `GET courses?include=instructors`() {
        val courses = api.courses(arrayOf("instructors")).execute().body()
        courses?.let {
            assertEquals(20, it.size)
        }
    }

    @Test fun `GET courses|{id}`() {
        val courses = api.courseById("26").execute().body()
        courses?.let {
            assertEquals("Algo++ Online", it.title)
        }
    }

    @Test fun `GET instructors`() {
        val courses = api.instructors.execute().body()
        courses?.let {
            assertEquals(11, it.size)
        }
    }

    @Test fun `GET instructors?include=courses`() {
        val courses = api.instructors(arrayOf("courses")).execute().body()
        courses?.let {
            assertEquals(11, it.size)
        }
    }
}