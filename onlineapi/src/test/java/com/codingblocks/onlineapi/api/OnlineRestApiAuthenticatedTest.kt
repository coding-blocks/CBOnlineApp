package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.CBOnlineCommunicator
import com.codingblocks.onlineapi.CBOnlineLib
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class OnlineRestApiAuthenticatedTest {

    @Before
    fun `SET JWT`() {

        val jwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NjAzNDMxLCJmaXJzdG5hbWUiOiJQdWxraXQiLCJs" +
            "YXN0bmFtZSI6IkFnZ2Fyd2FsIiwidXNlcm5hbWUiOiJwdWxraXQxMjM0IiwiZW1haWwiOiJwdWxraXQubWNhMTkuZHVAZ" +
            "21haWwuY29tIiwidmVyaWZpZWRlbWFpbCI6InB1bGtpdC5tY2ExOS5kdUBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6Iis5MS0" +
            "4NTk1MzUyNjQ3IiwibW9iaWxlIjoiKzkxLTk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiOTMwMjciLCJsYXN0X3JlYWRfbm90aWZ" +
            "pY2F0aW9uIjoiMCIsInBob3RvIjpudWxsLCJjb2xsZWdlIjoiMC0tLU9USEVSIC8gTk9UIExJU1RFRCAvIE5PIENPTExFR0UgLS0t" +
            "MCIsImJyYW5jaCI6bnVsbCwiZ3JhZHVhdGlvbnllYXIiOiIyMDE5Iiwib3JnYW5pemF0aW9uIjpudWxsLCJyb2xlSWQiOjMsImNyZW" +
            "F0ZWRBdCI6IjIwMjAtMDMtMTFUMTM6MDM6MDIuNjg3WiIsInVwZGF0ZWRBdCI6IjIwMjAtMDctMjdUMTM6MzY6NDEuMzY1WiIsIn" +
            "JvbGUiOnsiaWQiOjMsIm5hbWUiOiJNb2RlcmF0b3IiLCJjcmVhdGVkQXQiOiIyMDE4LTA5LTA0VDEzOjM4OjMxLjg4NVoiLCJ1c" +
            "GRhdGVkQXQiOiIyMDE4LTA5LTA0VDEzOjM4OjMxLjg4NVoifSwiY2xpZW50SWQiOiI0MGY3YWJhNS02ZmQ4LTRlZGQtYjExZC05N" +
            "GExZGZlN2VlYjAiLCJjbGllbnQiOiJhbmRyb2lkIiwiaWF0IjoxNTk1ODU3MDAxLCJleHAiOjE2MDEyNTcwMDF9.n1ZobWSZCjau" +
            "_4y4sCpxv8zfKkqaYxELA0D4-clKIgjEdTwkYQMPHXaQJ38B5Vv5Nlnt0MD21z0ZQc41fo-ZP7THttbxUDA_da30jalzY3sIeaf" +
            "Qoegalj2GDLGUx_OyVOYsHzv0v_dYfBkc1cMwJ1cVpdWI814RK-LFFdl8ToQHm2ZPVBQSIM4b5_rRXfeYtypCptH-C3lrueAWbrc" +
            "h7KITOGd8DK3RC292aU6NBNFf9IpyGXDaSfHSXPOOIpoqmg4tn8ZrcsIPebnCiWZXizqxdBWqXrjrQox2W6xhp1ghF0cQitiNyt7u" +
            "Kg-aOn27hn4LwgPqWq4-xPyRG3CRsQ"
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
    fun `GET getCart`() {
        val cart = runBlocking { CBOnlineLib.api.getCart().body() }
        assertNotNull(cart)
        assertNotNull(cart?.get("cartItems")?.asJsonObject?.get("invoice_id"))
    }

//    @Test
//    fun `GET updateCart`() {
//        val map = hashMapOf<String, Any>("stateId" to "DL","invoice_id" to "187595")
//        val updateCart = runBlocking { CBOnlineLib.api.updateCart(map).code() }
//        assertTrue(updateCart == 204)
//    }
}
