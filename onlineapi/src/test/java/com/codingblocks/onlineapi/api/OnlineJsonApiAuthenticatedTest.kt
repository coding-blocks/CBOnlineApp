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

        val jwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NjAzNDMxLCJmaXJzdG5hbWUiOiJQdWxraXQiLCJsYXN0bmFtZSI6IkFnZ2Fyd2FsIiwidXNlcm5hbWUiOiJwdWxraXQxMjM0IiwiZW1haWwiOiJwdWxraXQubWNhMTkuZHVAZ21haWwuY29tIiwidmVyaWZpZWRlbWFpbCI6InB1bGtpdC5tY2ExOS5kdUBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6Iis5MS04NTk1MzUyNjQ3IiwibW9iaWxlIjoiKzkxLTk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiOTMwMjciLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjpudWxsLCJjb2xsZWdlIjoiMC0tLU9USEVSIC8gTk9UIExJU1RFRCAvIE5PIENPTExFR0UgLS0tMCIsImJyYW5jaCI6bnVsbCwiZ3JhZHVhdGlvbnllYXIiOiIyMDE5Iiwib3JnYW5pemF0aW9uIjpudWxsLCJyb2xlSWQiOjMsImNyZWF0ZWRBdCI6IjIwMjAtMDMtMTFUMTM6MDM6MDIuNjg3WiIsInVwZGF0ZWRBdCI6IjIwMjAtMDYtMjZUMTQ6NTc6MDQuOTMxWiIsInJvbGUiOnsiaWQiOjMsIm5hbWUiOiJNb2RlcmF0b3IiLCJjcmVhdGVkQXQiOiIyMDE4LTA5LTA0VDEzOjM4OjMxLjg4NVoiLCJ1cGRhdGVkQXQiOiIyMDE4LTA5LTA0VDEzOjM4OjMxLjg4NVoifSwiY2xpZW50SWQiOiIzYmI3YzZmMS1hZGQ0LTRmYzMtOWY1NS04MmE5ZWVmNjM0OTYiLCJjbGllbnQiOiJhbmRyb2lkIiwiaWF0IjoxNTkzMTgzNDI1LCJleHAiOjE1OTg1ODM0MjV9.WrBSE1mNCiV2kFumBrboo9InwfJTalZuxQ4y9jgHeeK_tLMDPIEYHk33EOJ1Rna-7gZ0rRd99K-jUq7YoOOM3obQB_A2symNL2-SHBXO72QZOel9AR0aXjX9MS2vEYPiT1QBM2jEbg9B8wiBhKvPWVNjt1bAVQNwmqas7nvjNkGR9HAuAXHmM1qh98YqXjs9M7AJ84KkKiqG-vvyBWzhmDJDLOEyuKAAVA0Q30XYNsqxKOMf00-g8pFZGat6ot6jbev6ZRtP5Y-DwI7SMMqQo83M5bAqeLDWp0HHvEhBQnS9oaCPF3OH3xCBdRW58ne1uQFt_xUNV-vpM8RboPZR2Q"
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
        val runAttempt = runBlocking { CBOnlineLib.onlineV2JsonApi.enrolledCourseById("80179").body() }
        assertNotNull(runAttempt)
        assertTrue(runAttempt!!.id == "80179")
    }

    @Test
    fun `GET fetchNotes`(){
        //TODO(Add Attempt id here)
        val notes = runBlocking { CBOnlineLib.onlineV2JsonApi.getNotesByAttemptId("").body() }
        assertNotNull(notes)
        assertTrue(notes!!.isNotEmpty())
    }
}
