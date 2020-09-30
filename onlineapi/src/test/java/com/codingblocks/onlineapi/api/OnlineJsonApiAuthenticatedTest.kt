package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.CBOnlineCommunicator
import com.codingblocks.onlineapi.CBOnlineLib
import com.codingblocks.onlineapi.models.SendFeedback
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OnlineJsonApiAuthenticatedTest {

    @Before
    fun `SET JWT`() {

        val jwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NjAzNDMxLCJmaXJzdG5hbWUiOiJQdWxraXQiLCJsYXN0bmFtZSI6IkFnZ2Fyd2FsIiwidXNlcm5hbWUiOiJwdWxraXQxMjM0IiwiZW1haWwiOiJwdWxraXQubWNhMTkuZHVAZ21haWwuY29tIiwidmVyaWZpZWRlbWFpbCI6InB1bGtpdC5tY2ExOS5kdUBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6Iis5MS04NTk1MzUyNjQ3IiwibW9iaWxlIjoiKzkxLTk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiOTMwMjciLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjpudWxsLCJjb2xsZWdlIjoiMC0tLU9USEVSIC8gTk9UIExJU1RFRCAvIE5PIENPTExFR0UgLS0tMCIsImJyYW5jaCI6bnVsbCwiZ3JhZHVhdGlvbnllYXIiOiIyMDE5Iiwib3JnYW5pemF0aW9uIjpudWxsLCJyb2xlSWQiOjMsImNyZWF0ZWRBdCI6IjIwMjAtMDMtMTFUMTM6MDM6MDIuNjg3WiIsInVwZGF0ZWRBdCI6IjIwMjAtMDctMjdUMTM6MzY6NDEuMzY1WiIsInJvbGUiOnsiaWQiOjMsIm5hbWUiOiJNb2RlcmF0b3IiLCJjcmVhdGVkQXQiOiIyMDE4LTA5LTA0VDEzOjM4OjMxLjg4NVoiLCJ1cGRhdGVkQXQiOiIyMDE4LTA5LTA0VDEzOjM4OjMxLjg4NVoifSwiY2xpZW50SWQiOiI0MGY3YWJhNS02ZmQ4LTRlZGQtYjExZC05NGExZGZlN2VlYjAiLCJjbGllbnQiOiJhbmRyb2lkIiwiaWF0IjoxNTk1ODU3MDAxLCJleHAiOjE2MDEyNTcwMDF9.n1ZobWSZCjau_4y4sCpxv8zfKkqaYxELA0D4-clKIgjEdTwkYQMPHXaQJ38B5Vv5Nlnt0MD21z0ZQc41fo-ZP7THttbxUDA_da30jalzY3sIeafQoegalj2GDLGUx_OyVOYsHzv0v_dYfBkc1cMwJ1cVpdWI814RK-LFFdl8ToQHm2ZPVBQSIM4b5_rRXfeYtypCptH-C3lrueAWbrch7KITOGd8DK3RC292aU6NBNFf9IpyGXDaSfHSXPOOIpoqmg4tn8ZrcsIPebnCiWZXizqxdBWqXrjrQox2W6xhp1ghF0cQitiNyt7uKg-aOn27hn4LwgPqWq4-xPyRG3CRsQ"
        val refreshToken = "57672e90-dada-4345-b596-d4133cb473d9"

        CBOnlineLib.initialize(object : CBOnlineCommunicator {
            override var authJwt: String
                get() = jwt
                set(value) {}
            override var refreshToken: String
                get() = refreshToken
                set(value) {}
            override var baseUrl: String
                get() = "api-online.codingblocks.xyz"
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
    fun `GET fetchNotes`() {
        val notes = runBlocking { CBOnlineLib.onlineV2JsonApi.getNotesByAttemptId("80179").body() }
        assertNotNull(notes)
        assertTrue(notes!!.isEmpty())
    }

    @Test
    fun `PATCH pauseCourse`() {
        val runAttempt = runBlocking { CBOnlineLib.onlineV2JsonApi.pauseCourse("80179").body() }
        assertNotNull(runAttempt)
        assertTrue(runAttempt!!.paused)
    }

    @Test
    fun `PATCH unPauseCourse`() {
        val runAttempt = runBlocking { CBOnlineLib.onlineV2JsonApi.unPauseCourse("80179").body() }
        assertNotNull(runAttempt)
        assert(!runAttempt!!.paused)
    }

//    @Test
//    fun `GET addWishlist`() {
//        //89 Course is C++ Fundamentals Course
//        val wishlistAdd = runBlocking { CBOnlineLib.onlineV2JsonApi.addWishlist(Wishlist(Course("89"))).body() }
//        assertNotNull(wishlistAdd)
//        assertTrue(wishlistAdd?.course?.id == "89")
//    }
//
//    @Test
//    fun `GET getWishlist`() {
//        //per page limit is set to 3 by default, so it is increased to 100 for all wishlist courses
//        val wishlistGet = runBlocking { CBOnlineLib.onlineV2JsonApi.getWishlist(page = "100").body()?.get() }
//        assertNotNull(wishlistGet)
//        var found = false
//        for (item in wishlistGet!!)
//            if (item.course?.id == "89") {
//                found = true
//                break
//            }
//        assertTrue(found)
//    }

//    @Test
//    fun `GET checkWishlist`() {
//        val wishlistCheck = runBlocking { CBOnlineLib.onlineV2JsonApi.checkIfWishlisted("89").body() }
//        assertNotNull(wishlistCheck)
//        assertTrue(wishlistCheck?.id != null)
//    }

//    @Test
//    fun `GET removeWishlist`() {
//        val id = runBlocking { CBOnlineLib.onlineV2JsonApi.checkIfWishlisted("89").body()?.id }
//        val wishlistRemove = runBlocking { CBOnlineLib.onlineV2JsonApi.removeWishlist(id ?: "").code() }
//        assertTrue(wishlistRemove == 204)
//    }

    @Test
    fun `POST sendFeedback`() {
        val sendFeedback = runBlocking { CBOnlineLib.api.sendFeedback("45", SendFeedback("Amazing", "Amazing course", 4.9F)) }
        assertTrue(sendFeedback.code() == 200)
    }

    @Test
    fun `GET getFeedback`() {
        val getFeedback = runBlocking { CBOnlineLib.api.getFeedback("45") }
        assertNotNull(getFeedback.body())
        assertTrue(getFeedback.body()?.count != null)
    }
}
