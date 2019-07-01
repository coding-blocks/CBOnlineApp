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
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsInZlcmlmaWVkZW1haWwiOiJhZ2dhcndhbHB1bGtpdDU5NkBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6Iis5MS05NTgyMDU0NjY0IiwibW9iaWxlIjoiKzkxLTk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJjb2xsZWdlIjoiQW1pdHkgU2Nob29sIE9mIEVuZ2luZWVyaW5nICYgVGVjaG5vbG9neSAoTm9pZGEpIiwib3JnYW5pemF0aW9uIjpudWxsLCJyb2xlSWQiOjIsImNyZWF0ZWRBdCI6IjIwMTgtMDktMjdUMTM6MTA6NTkuMzk2WiIsInVwZGF0ZWRBdCI6IjIwMTktMDctMDFUMTU6MTM6MjguNzE1WiIsInJvbGUiOnsiaWQiOjIsIm5hbWUiOiJTdHVkZW50IiwiY3JlYXRlZEF0IjoiMjAxNy0wOS0wN1QxMDo1ODoxOS45OTNaIiwidXBkYXRlZEF0IjoiMjAxNy0wOS0wN1QxMDo1ODoxOS45OTNaIn0sImNsaWVudElkIjoiOGI3YThhNzMtYWJkMS00NDE1LThiNjctMjQyMjZmYTJkYzM0IiwiY2xpZW50IjoiYW5kcm9pZCIsImlhdCI6MTU2MTk5NDAwOCwiZXhwIjoxNTY3Mzk0MDA4fQ.uP7MI9jUUsuK16ljdeM67ymP4Uwieav_6Pet8Le70TcHnLWEzdQCMNE5PU5vm8xKKRtsblk2ZnXq2qdyf-TiWSCQvTevlC63GHWgOdAiaH3MOnv2LgBDP0OBzuRqZYFwxvTkjXHookatI9U1rxVISIR7eUE9YhPQAZI30-R44FQqjOsOdeuuI9iHCqHtIJ3KKJaZgT8pDPVcRYNke0nuN-Nh60f5HgpICNV5XyAjR5T5sXaEDulqKjqaCddnHzhz0vEVgum3wHj3P6qKBY276tjWQZ_1-mrMFQR8WbQvwZHAT8arPE8mOK4FA31qaPBmUT7zb21fDnr8BDtNwIaePg"
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
