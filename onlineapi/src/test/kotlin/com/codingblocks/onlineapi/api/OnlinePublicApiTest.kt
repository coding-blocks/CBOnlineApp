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

//    @Test
//    fun `GET section`() {
//        val courses = api.getSections("795").execute().body()
//        courses?.let {
//            assertEquals("Python Basics", it.name)
//        }
//    }

    @Test
    fun `GET myCourses`() {
        val courses = api.getMyCourses("JWT eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Ijk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJyb2xlSWQiOjIsImNyZWF0ZWRBdCI6IjIwMTgtMDktMjdUMTM6MTA6NTkuMzk2WiIsInVwZGF0ZWRBdCI6IjIwMTgtMTEtMDZUMDI6NDg6MjQuMTY2WiIsImNsaWVudElkIjoiYmExZTgzNjctNzQyZi00ODM2LWFlMDAtN2QxMzNkZDRiNmFmIiwiaXNUb2tlbkZvckFkbWluIjpmYWxzZSwiaWF0IjoxNTQxNjc1NTQ2LCJleHAiOjE1NDE2NzcwNDZ9.XBNwT5vBIEy-PH5qxWukGBgNXg-vA4XKxSsbs-BFREdMmLY_O30TbJ-_qzD4lq9OZ0XAo0XTMD-OZoA336cXiO2Qb5h3TOv0A3zQGBiSHch0tJWVcIqpIc_QyPpRZPL7-iHWH3OG5bvBf-oQJHXUySGKotLwzq0wG-vxE8dH5TFv-iaBLJgh5Llxtp1ivzxK0bRqFyw8sPgAIA9_2U7YFV09VEXvy2nixnp_SQCAg-p5rPnBiTVehb3VWuYL5lX9U9a5FHnGl2Lr2D8gx-0Jb7OlaKEWGf6h4dyjrMJTswV_9gzQkvBCxh058QNOI4ugUZhBbs4jJera1FsQd0fb9g").execute().body()
        courses?.let {
            assertEquals(2, it.size)
        }
    }
    @Test
    fun `GET myCourse`() {
        val courses = api.enrolledCourseById("JWT eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Ijk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJyb2xlSWQiOjIsImNyZWF0ZWRBdCI6IjIwMTgtMDktMjdUMTM6MTA6NTkuMzk2WiIsInVwZGF0ZWRBdCI6IjIwMTgtMTEtMDlUMTA6MTE6MjQuNDc3WiIsImNsaWVudElkIjoiNWQ5NDg3MzItNjJlYi00ZDFmLWFmYjAtZWZmM2U0MTI0NDI5IiwiaXNUb2tlbkZvckFkbWluIjpmYWxzZSwiaWF0IjoxNTQxOTE5NTQ0LCJleHAiOjE1NDE5MjEwNDR9.gaNN76vYPViHImftBN5rg9WaaLgWpCBXOeSiL4axlwHz_Yi-VqJnXQbPe-vDt93Rax_uQYpcSuzzpXmqQy0c3IIuHfiJvMu6BDryAHgHmMXMENo04fQh2h7Tq8b7nUPQuNp9NfhxMTMX5sne8JjGTBcsYjOzCsSWzYigUE6dDQjRmL7sjcxUi0BstoMskey_SMeqFEQQs-wAh9B2Fip7wG96J6bosi5s1fQ11kP9oQRtQWMqeQ6C0ITT99DE2OUHzgU5soE4WFyeEI_9-t2zDE9BhsNwB2dTUh6renIaqBh3jgArAljJggB8tOQ2eEMOMJElp8DD1xFNcbsegN8n5g","8252").execute().body()
        courses?.let {
            assertEquals(1,1)
        }
    }
}