package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.CBOnlineCommunicator
import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.Clients
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class OnlineJsonApiAuthenticatedTest {

    @Before
    fun `SET JWT`() {

        val jwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NjAzNDMxLCJmaXJzdG5hbWUiOiJQdWxraXQiLCJsYXN0bmFtZSI6IkFnZ2Fyd2FsIiwidXNlcm5hbWUiOiJwdWxraXQxMjM0IiwiZW1haWwiOiJwdWxraXQubWNhMTkuZHVAZ21haWwuY29tIiwidmVyaWZpZWRlbWFpbCI6InB1bGtpdC5tY2ExOS5kdUBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6Iis5MS04NTk1MzUyNjQ3IiwibW9iaWxlIjoiKzkxLTk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiOTMwMjciLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjpudWxsLCJjb2xsZWdlIjoiMC0tLU9USEVSIC8gTk9UIExJU1RFRCAvIE5PIENPTExFR0UgLS0tMCIsImJyYW5jaCI6bnVsbCwiZ3JhZHVhdGlvbnllYXIiOiIyMDE5Iiwib3JnYW5pemF0aW9uIjpudWxsLCJyb2xlSWQiOjMsImNyZWF0ZWRBdCI6IjIwMjAtMDMtMTFUMTM6MDM6MDIuNjg3WiIsInVwZGF0ZWRBdCI6IjIwMjAtMDYtMjZUMTQ6MjE6NTAuNjQ1WiIsInJvbGUiOnsiaWQiOjMsIm5hbWUiOiJNb2RlcmF0b3IiLCJjcmVhdGVkQXQiOiIyMDE4LTA5LTA0VDEzOjM4OjMxLjg4NVoiLCJ1cGRhdGVkQXQiOiIyMDE4LTA5LTA0VDEzOjM4OjMxLjg4NVoifSwiY2xpZW50SWQiOiIwMGE1NzlmYS1lZTNiLTQ4YjAtOWJmNy05MzczNTUxZGIxNDEiLCJjbGllbnQiOiJhbmRyb2lkIiwiaWF0IjoxNTkzMTgxMzEwLCJleHAiOjE1OTg1ODEzMTB9.gHtbYSN2FWBz8dZeTx_dinEcR5pJC3wo8sP24f90COhPmX6cQ3ifRLpf9-hqebmc8qCLGo256SAzhjBCrxaZLrYn0kwqU6Gte2XmfgE8EjD9bIJjBlVWXOZLhHtcH6l3BdWy-nS6EE7LFZ_FbTR9QpkBlJ7AKXvNV4swqnLIl8KHz_ZqNw4medN-TqodXsiWuVdqGoRLTaJLl5QU9ripJyiCnVrP8R1nYIH8P4abYvdVoSrtYWT3TfgF_DsfjCbz6lh--QhVBB3cZRUYVGzb_bt1M4oDUGNwZUdi8YBoEIg8pgOswvtlU7WR_vs7o2BL4Jm92prA_5waOE_PtIvlfA"
        val refreshToken = "c0ddcd65-e5f8-4bc9-ba45-0cb08d1fc369"

        CBOnlineLib.initialize(object : CBOnlineCommunicator {
            override var authJwt: String
                get() = jwt
                set(value) {}
            override var refreshToken: String
                get() = refreshToken
                set(value) {}
        })
    }

    @Test
    fun `GET ContentList`() {
        val content = runBlocking { CBOnlineLib.onlineV2JsonApi.getSectionContents(sectionId = "93064").body() }
        assertNotNull(content)
        assertTrue(content!!.isNotEmpty())
    }

    @Test
    fun `GET myCourses`() {
        val courses = runBlocking { CBOnlineLib.onlineV2JsonApi.getMyCourses().body()?.get() }
        assertNotNull(courses)
        assertTrue(courses!!.isNotEmpty())
    }

    @Test
    fun `GET enrolledCourse`() {
        val course = runBlocking { CBOnlineLib.onlineV2JsonApi.enrolledCourseById("80179").body() }
        assertNotNull(course)
        assertTrue(course!!.runTier == "LITE")
    }
}
