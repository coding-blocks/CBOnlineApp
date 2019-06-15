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
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTM1OTM4LCJmaXJzdG5hbWUiOiJBZGl0eWEiLCJsYXN0bmFtZSI6Ikd1cHRhIiwidXNlcm5hbWUiOiJhZGl0eWFzdGljIiwiZW1haWwiOiJhZGl0eWFvZmZpY2lhbGd1cHRhQGdtYWlsLmNvbSIsInZlcmlmaWVkZW1haWwiOiJhZGl0eWFvZmZpY2lhbGd1cHRhQGdtYWlsLmNvbSIsInZlcmlmaWVkbW9iaWxlIjpudWxsLCJtb2JpbGUiOiIrOTEtODQ1ODg5MjIyNiIsIm9uZWF1dGhfaWQiOiIzNjM1NCIsImxhc3RfcmVhZF9ub3RpZmljYXRpb24iOiIwIiwicGhvdG8iOiJodHRwczovL2F2YXRhcnMyLmdpdGh1YnVzZXJjb250ZW50LmNvbS91LzExOTg4NTE3P3Y9NCIsImNvbGxlZ2UiOiJCaGFyYXRpIFZpZHlhcGVldGggVW5pdmVyc2l0eSBDb2xsZWdlIE9mIEVuZ2luZWVyaW5nIChQdW5lKSIsIm9yZ2FuaXphdGlvbiI6bnVsbCwicm9sZUlkIjoyLCJjcmVhdGVkQXQiOiIyMDE5LTA1LTI2VDAzOjQyOjU0Ljg4M1oiLCJ1cGRhdGVkQXQiOiIyMDE5LTA2LTA4VDAzOjQ3OjQ2LjQwOFoiLCJyb2xlIjp7ImlkIjoyLCJuYW1lIjoiU3R1ZGVudCIsImNyZWF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiIsInVwZGF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiJ9LCJjbGllbnRJZCI6IjM0ZDY5NDVjLTE0ZGItNDRhOC05NThhLWUzZGUyYzE3MzJjMCIsImNsaWVudCI6ImFuZHJvaWQiLCJpYXQiOjE1NTk5NjU2NjYsImV4cCI6MTU2NTM2NTY2Nn0.uGSl5tCh4ZGh_Ng7-2R8sLcJdQBzntSMH2WlTt6SIA7z2KD5cJffUxlaMs-z59rwmtMedPvAUZW_MF2peSvHNtfcHOERccoqAGRXXkHE5aDVQQt1yOH3iq1M4BMrbOSvJRclx6OTXvMBWfVpGsvO3A9OMUVm6SpSmO_rrUl2Ktn3oQGNDz9mEDdn8Vmn8dS5RX_cUFYWPkI0qSIzK-yNukafIFFC0ZSfYYqITihA-lYusqiVuktziFzaeO9-1drmr_P3-xA-oNwIQ6QeAmwTz8KwS1KuKWl1M_LQEs5EmfFPvtIb9GI1hSZX-6yD0Y9vjk3wxX8cqTjhA2jvj5u6gw"
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
