package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import org.junit.Assert.assertEquals
import org.junit.Test

class OnlinePublicApiTest {
    val api = Clients.onlineV2PublicClient

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
            assertEquals(15, it.size)
        }
    }

    @Test
    fun `GET recommended`() {
        val courses = api.getRecommendedCourses().execute().body()
        courses?.let {
            assertEquals(11, it.size)
        }
    }

//    @Test
//    fun `GET section`() {
//        val courses = api.getSections("795").execute().body()
//        courses?.let {
//            assertEquals("Python Basics", it.name)
//        }
//    }

    @Test
    fun `GET myCourses`() {
        val courses = api.getMyCourses("JWT eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Ijk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJjb2xsZWdlIjoiQW1pdHkgU2Nob29sIE9mIEVuZ2luZWVyaW5nICYgVGVjaG5vbG9neSAoTm9pZGEpIiwicm9sZUlkIjoyLCJjcmVhdGVkQXQiOiIyMDE4LTA5LTI3VDEzOjEwOjU5LjM5NloiLCJ1cGRhdGVkQXQiOiIyMDE4LTExLTE4VDA4OjIwOjI5LjE4NloiLCJyb2xlIjp7ImlkIjoyLCJuYW1lIjoiU3R1ZGVudCIsImNyZWF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiIsInVwZGF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiJ9LCJjbGllbnRJZCI6IjZkMWI3ZmNiLWZiMDYtNDJiZC1iMzM1LTRiNWMwYmIxMDUwNiIsImlzVG9rZW5Gb3JBZG1pbiI6ZmFsc2UsImlhdCI6MTU0MjUyOTIyOSwiZXhwIjoxNTQyNTMwNzI5fQ.f3-SOcIFQbsORcMYmIJR0wWz8x3RHrGtbCuuogVlWLsIuX1WLVI6bo1v4JUC3_P8WVzNxwjdcxn5hA5vHtfbjMUPWe_Uy5n9S15ECVy5H6NngU4eF6zTfX6CUlIApgNFZcpPnF2yvkqeX7OEJfcyTFjTk4REQaL7-SkuXBTbKJhzd6MCHs1dmYTEXj98fKNcBmfYCBNVy0q_ETtlPE-3ukKwJgoDl6KCVhZc1uemgFGxjnnsKh_UJxUNnmbAXraMC-255ObJy3dAOQZppU5wMNlPtKgqazjXwUnRO1dP8gq2WcLOv-cqLKGnOTZWxLnxyPhflxKd3yfm_8qu1OiwUg").execute().body()
        courses?.let {
            assertEquals(3, it.size)
        }
    }

    @Test
    fun `GET myCourse`() {
        val attempt = api.enrolledCourseById("JWT eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Ijk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJjb2xsZWdlIjoiQW1pdHkgU2Nob29sIE9mIEVuZ2luZWVyaW5nICYgVGVjaG5vbG9neSAoTm9pZGEpIiwicm9sZUlkIjoyLCJjcmVhdGVkQXQiOiIyMDE4LTA5LTI3VDEzOjEwOjU5LjM5NloiLCJ1cGRhdGVkQXQiOiIyMDE4LTExLTI4VDEwOjM3OjUyLjI4MVoiLCJyb2xlIjp7ImlkIjoyLCJuYW1lIjoiU3R1ZGVudCIsImNyZWF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiIsInVwZGF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiJ9LCJjbGllbnRJZCI6IjM4NDEwNmMwLTk5ZjMtNGI3OS1iMTAxLWZiMGFlNjk2M2Y1MiIsImlzVG9rZW5Gb3JBZG1pbiI6ZmFsc2UsImlhdCI6MTU0MzQwMTQ3MiwiZXhwIjoxNTQzNDAyOTcyfQ.it9tzRMDZKzEv-Lcxw_ZJ78uG4EXLyGsTJhzqX4xseR_4pktk5MRjN9oQx6HsX1yB1hAdwzLcYfjKtCE2gG3SVOsLU_38Ri1dl2NtR4ez7jlAl2wNhM3NC3Dr2QWz1LAKhTIEFV8aNF1HyqlvM7BNRXln7xldGMApn2CUCVawz8-_zqegxf8IH4vPD41M4qjH4FPt_G7HjJXw6yg4a9YAJW4pZjOkR8yf4u8uPV8wH-9JGHlJ4kgH6gUCCCiYvSbzZgCvw1t0lRPqSM9ccuZjMV9DhqlsvgmyTTQ3nD-ip8AsgL9CZ345Ieim-kR5D7LGgpj-o02q_1seDVxESyrFQ", "8252").execute().body()
        attempt?.let {
            assertEquals(1, 1)
        }
    }
}