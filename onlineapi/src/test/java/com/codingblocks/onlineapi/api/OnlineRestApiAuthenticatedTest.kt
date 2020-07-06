package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.CBOnlineCommunicator
import com.codingblocks.onlineapi.CBOnlineLib
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class OnlineRestApiAuthenticatedTest {

    @Before
    fun `SET JWT`() {

        val jwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NjAzNDMxLCJmaXJzdG5hbWUiOiJQdWxraXQiLCJsYXN0bmFtZSI6IkFnZ2Fyd2FsIiwidXNlcm5hbWUiOiJwdWxraXQxMjM0IiwiZW1haWwiOiJwdWxraXQubWNhMTkuZHVAZ21haWwuY29tIiwidmVyaWZpZWRlbWFpbCI6InB1bGtpdC5tY2ExOS5kdUBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6Iis5MS04NTk1MzUyNjQ3IiwibW9iaWxlIjoiKzkxLTk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiOTMwMjciLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjpudWxsLCJjb2xsZWdlIjoiMC0tLU9USEVSIC8gTk9UIExJU1RFRCAvIE5PIENPTExFR0UgLS0tMCIsImJyYW5jaCI6bnVsbCwiZ3JhZHVhdGlvbnllYXIiOiIyMDE5Iiwib3JnYW5pemF0aW9uIjpudWxsLCJyb2xlSWQiOjMsImNyZWF0ZWRBdCI6IjIwMjAtMDMtMTFUMTM6MDM6MDIuNjg3WiIsInVwZGF0ZWRBdCI6IjIwMjAtMDYtMjlUMTA6NDY6MDEuMDUwWiIsInJvbGUiOnsiaWQiOjMsIm5hbWUiOiJNb2RlcmF0b3IiLCJjcmVhdGVkQXQiOiIyMDE4LTA5LTA0VDEzOjM4OjMxLjg4NVoiLCJ1cGRhdGVkQXQiOiIyMDE4LTA5LTA0VDEzOjM4OjMxLjg4NVoifSwiY2xpZW50SWQiOiJlZDc5NGQyZC00YzM1LTRhMjUtOTVhYi04YjgyNjcwMzE2OWUiLCJjbGllbnQiOiJhbmRyb2lkIiwiaWF0IjoxNTkzNDI3NTYxLCJleHAiOjE1OTg4Mjc1NjF9.tkdrsIyRFH8RKNOIStgUacoTSmLP4Byzeu5yjCokZ0TvwDYqop-c7Q1f_LqvIen6OeCSJkvpUQU91usUGGJYKqExaw_S1YpWef5D55a7Uz5Bs_JsBmg3iHr18w7fSg65qwCAd8ev8nrbDs0lMZPJJB0NwoFIpLkWidljqxG-s2Sjtsf6SIQLUmPwl94j_CmyTst4wGfIqaS_PmbZBAG5ZQM1gbBgnAEGy4ybxOHL4apy30ix4cB89_PjpyRCVMbFxNG7GF4q5WFZKMs--Vzg5reJXP_29hZshPwJHAcpA6TlVeEsYy6vd8_Hsb4nH9f9RanRt6FZfzMYGIXRhS41Pw"
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
    fun `GET getCart`() {
        val cart = runBlocking { CBOnlineLib.api.getCart().body() }
        assertNotNull(cart)
        assertNotNull(cart?.get("cartItems")?.asJsonObject?.get("invoice_id"))
    }

    @Test
    fun `GET updateCart`() {
        val map = hashMapOf<String, Any>("stateId" to "DL","invoice_id" to "187595")
        val updateCart = runBlocking { CBOnlineLib.api.updateCart(map).code() }
        assertTrue(updateCart == 204)
    }

}
