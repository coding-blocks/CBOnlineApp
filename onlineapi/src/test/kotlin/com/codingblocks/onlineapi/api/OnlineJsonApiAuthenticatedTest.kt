package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OnlineJsonApiAuthenticatedTest {
    val api = Clients.onlineV2JsonApi

    @Before
    fun `set JWT` () {
        Clients.authJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Ijk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJjb2xsZWdlIjoiQW1pdHkgU2Nob29sIE9mIEVuZ2luZWVyaW5nICYgVGVjaG5vbG9neSAoTm9pZGEpIiwicm9sZUlkIjoyLCJjcmVhdGVkQXQiOiIyMDE4LTA5LTI3VDEzOjEwOjU5LjM5NloiLCJ1cGRhdGVkQXQiOiIyMDE4LTExLTE4VDA4OjIwOjI5LjE4NloiLCJyb2xlIjp7ImlkIjoyLCJuYW1lIjoiU3R1ZGVudCIsImNyZWF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiIsInVwZGF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiJ9LCJjbGllbnRJZCI6IjZkMWI3ZmNiLWZiMDYtNDJiZC1iMzM1LTRiNWMwYmIxMDUwNiIsImlzVG9rZW5Gb3JBZG1pbiI6ZmFsc2UsImlhdCI6MTU0MjUyOTIyOSwiZXhwIjoxNTQyNTMwNzI5fQ.f3-SOcIFQbsORcMYmIJR0wWz8x3RHrGtbCuuogVlWLsIuX1WLVI6bo1v4JUC3_P8WVzNxwjdcxn5hA5vHtfbjMUPWe_Uy5n9S15ECVy5H6NngU4eF6zTfX6CUlIApgNFZcpPnF2yvkqeX7OEJfcyTFjTk4REQaL7-SkuXBTbKJhzd6MCHs1dmYTEXj98fKNcBmfYCBNVy0q_ETtlPE-3ukKwJgoDl6KCVhZc1uemgFGxjnnsKh_UJxUNnmbAXraMC-255ObJy3dAOQZppU5wMNlPtKgqazjXwUnRO1dP8gq2WcLOv-cqLKGnOTZWxLnxyPhflxKd3yfm_8qu1OiwUg"
    }

    @Test
    fun `GET section`() {
        suspend {
            val courses = api.getSections("795").await().body()
            courses?.let {
                assertEquals("Python Basics", it.name)
            }
        }
    }

    @Test
    fun `GET myCourses`() {
        val courses = api.getMyCourses().execute().body()
        courses?.let {
            assertEquals(3, it.size)
        }
    }

    @Test
    fun `GET myCourse`() {
        val attempt = api.enrolledCourseById("8252").execute().body()
        attempt?.let {
            assertEquals(1, 1)
        }
    }
}