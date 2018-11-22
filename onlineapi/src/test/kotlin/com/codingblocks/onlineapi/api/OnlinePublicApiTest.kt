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
        val attempt = api.enrolledCourseById("JWT eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Ijk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJjb2xsZWdlIjoiQW1pdHkgU2Nob29sIE9mIEVuZ2luZWVyaW5nICYgVGVjaG5vbG9neSAoTm9pZGEpIiwicm9sZUlkIjoyLCJjcmVhdGVkQXQiOiIyMDE4LTA5LTI3VDEzOjEwOjU5LjM5NloiLCJ1cGRhdGVkQXQiOiIyMDE4LTExLTIxVDExOjU0OjA3LjQwNVoiLCJyb2xlIjp7ImlkIjoyLCJuYW1lIjoiU3R1ZGVudCIsImNyZWF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiIsInVwZGF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiJ9LCJjbGllbnRJZCI6IjA5ZjlkMGEyLTUzZDMtNGMxZS05YWJjLTgyZmNkZjhiOTYxMyIsImlzVG9rZW5Gb3JBZG1pbiI6ZmFsc2UsImlhdCI6MTU0MjgwMTI0NywiZXhwIjoxNTQyODAyNzQ3fQ.t7St9IrkbjQ6MWOAQr8r6XgwL57efjYa08ii1ONEsqwYFkBV1KlccGcJJlUjN85pzKrjDWvzOIYtUSa_c_aBHMmUGlpv-evaDXGJavd9yQ5WTCwze_GArK74A-OG4kroDUTsWljpS3zFisDu8qlUSDuQx40MDtX-cJGiH6UeV9FWVmaKGyDVCVO7dvQrEMn63VQKThPGxzWwxihYWHmNevOi8PK8TGk0F7Frg5NbHNIc279MAJEl3aCxcteHwuw7J3rvdDZCD_hd_wYuSoShsS3ka46zPcwsx2F2WSjBpTC3wgMTHij4FfSeiHnIryxbE1P_Is5btaVuYpoKqnxD6A", "11312").execute().body()
        attempt?.let {
            assertEquals(1, 1)
        }
    }
}