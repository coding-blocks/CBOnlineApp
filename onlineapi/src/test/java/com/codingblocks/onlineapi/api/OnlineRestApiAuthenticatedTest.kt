package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class OnlineRestApiAuthenticatedTest {

    val restapi = Clients.api

    @Before
    fun `set JWT`() {
        //TODO
        //Add a Static Token
        Clients.authJwt =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsInZlcmlmaWVkZW1haWwiOiJhZ2dhcndhbHB1bGtpdDU5NkBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6Iis5MS05NTgyMDU0NjY0IiwibW9iaWxlIjoiKzkxLTk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJjb2xsZWdlIjoiQW1pdHkgU2Nob29sIE9mIEVuZ2luZWVyaW5nICYgVGVjaG5vbG9neSAoTm9pZGEpIiwib3JnYW5pemF0aW9uIjpudWxsLCJyb2xlSWQiOjEsImNyZWF0ZWRBdCI6IjIwMTgtMDktMjdUMTM6MTA6NTkuMzk2WiIsInVwZGF0ZWRBdCI6IjIwMTktMDctMTRUMTk6NDk6NDYuMzAxWiIsInJvbGUiOnsiaWQiOjEsIm5hbWUiOiJBZG1pbiIsImNyZWF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiIsInVwZGF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiJ9LCJjbGllbnRJZCI6IjU0NjExMTRhLTQyMmItNDFhOC1hODc4LTc5MzEyMjU1Zjc0NSIsImNsaWVudCI6ImFuZHJvaWQiLCJpYXQiOjE1NjMxMzM3ODYsImV4cCI6MTU2ODUzMzc4Nn0.jcYilL0CCTSjD_TfkKxcb2pp2eQv5GcdU8EBkU4cPiJgIoQmoIxPkZ6ckExemjSrzFoPFREl6byrLfMQQP1bKGxkBblFV6_VQVpd6OTgfvUW3Mld-0dFtVRNV4s-vLlqHqTmSjsCEMFfCv06RUTOe4lfFbKfx_PN0E_onRUHCNIiED3B5d6HDlBTVWIkZtfZBx00sGAY2i59FO9cQCG_u110kxW6NxdzfNaDYdyKU7WwERJiGkHkGyF599ugQmepP6d_fSlua0l3-iHNoXrGWzDZZqmD6deJknplLvuDJtIHqy4vVCe67tr-o6H4o09aP1dsR0KGn2lMTTmL65JpZg"
    }

//    @Test
//    fun `GET getMyCourseProgress`() {
//        val progress = restapi.getMyCourseProgress("22684").execute().body()
//        assertNotNull(progress)
//    }

    @Test
    fun `GET getOTP`() {
        val otp = restapi.getOtp("7f97136df7cc4f349e0129040d85b79f", "4688", "22685").execute().body()
        assertNotNull(otp)
    }

    @Test
    fun `GET getMe`() {
        val me = restapi.getMe().execute().body()
        assertNotNull(me)
    }

    @Test
    fun `GET enrollTrial`() {
        val enroll = restapi.enrollTrial("262").execute().body()
        assertNotNull(enroll)
    }

    @Test
    fun `GET Doubts `() {
        val doubts = restapi.getDoubts("25").execute().body()
        assertNotNull(doubts)
    }

}
