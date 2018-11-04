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
            assertEquals(14, it.size)
        }
    }

    @Test
    fun `GET recommended`() {
        val courses = api.getRecommendedCourses().execute().body()
        courses?.let {
            assertEquals(11, it.size)
        }
    }

    @Test
    fun `GET section`() {
        val courses = api.getSections("795").execute().body()
        courses?.let {
            assertEquals("Python Basics", it.name)
        }
    }

    @Test
    fun `GET myCourses`() {
        val courses = api.getMyCourses("JWT eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Ijk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJyb2xlSWQiOjIsImNyZWF0ZWRBdCI6IjIwMTgtMDktMjdUMTM6MTA6NTkuMzk2WiIsInVwZGF0ZWRBdCI6IjIwMTgtMTAtMTJUMTA6MDY6NTMuNjE0WiIsImNsaWVudElkIjoiYjI4NDFlNGQtZmY0Yi00OTI5LWJhOGUtMmQxZmM0ZTYwMTFmIiwiaXNUb2tlbkZvckFkbWluIjpmYWxzZSwiaWF0IjoxNTM5NTY1NzE4LCJleHAiOjE1Mzk1NjcyMTh9.U7JmDSg4L_5bBmMBcFSkpQN_t3lYb_himb88eBJqqUBD2e2xS9PGcB6dFTHbiwHj7qhzcOC85x7Lklbi7oWdHrW7fL25LOxg52JT10GnDX41hxamo1fnvvnJ3HI0hx1gvUElaAmia4Kyg1VVgLp7EiH9rphMRV_lhTLz0nF2usz92eGh01P0V9XYqYiiVWH3H_1-vqktHA0yLWHw27taKqruZPdGAWjBnN7aO7lmk3IhfU0fvQkgumFxtS_Jmy_cPL-kJglDq3sEoDUtuOjpt4H25loy_GMufBQeogevpZfWPkcNqYpSzEAqWb5Rh6oMXd84SnAyUkbr4ytqoE4ZhA").execute().body()
        courses?.let {
            assertEquals(2, it.size)
        }
    }
    @Test
    fun `GET myCourse`() {
        val courses = api.enrolledCourseById("JWT eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Ijk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJyb2xlSWQiOjIsImNyZWF0ZWRBdCI6IjIwMTgtMDktMjdUMTM6MTA6NTkuMzk2WiIsInVwZGF0ZWRBdCI6IjIwMTgtMTAtMTJUMTA6MDY6NTMuNjE0WiIsImNsaWVudElkIjoiYjI4NDFlNGQtZmY0Yi00OTI5LWJhOGUtMmQxZmM0ZTYwMTFmIiwiaXNUb2tlbkZvckFkbWluIjpmYWxzZSwiaWF0IjoxNTM5NTY1NzE4LCJleHAiOjE1Mzk1NjcyMTh9.U7JmDSg4L_5bBmMBcFSkpQN_t3lYb_himb88eBJqqUBD2e2xS9PGcB6dFTHbiwHj7qhzcOC85x7Lklbi7oWdHrW7fL25LOxg52JT10GnDX41hxamo1fnvvnJ3HI0hx1gvUElaAmia4Kyg1VVgLp7EiH9rphMRV_lhTLz0nF2usz92eGh01P0V9XYqYiiVWH3H_1-vqktHA0yLWHw27taKqruZPdGAWjBnN7aO7lmk3IhfU0fvQkgumFxtS_Jmy_cPL-kJglDq3sEoDUtuOjpt4H25loy_GMufBQeogevpZfWPkcNqYpSzEAqWb5Rh6oMXd84SnAyUkbr4ytqoE4ZhA","8252").execute().body()
        courses?.let {
            assertEquals(1,1)
        }
    }
}