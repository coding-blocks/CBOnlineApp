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
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTI4ODE1LCJmaXJzdG5hbWUiOiJSYWh1bCIsImxhc3RuYW1lIjoiUmF5IiwidXNlcm5hbWUiOiJSYWh1bC1SYXktMjM0OTI2MDg3MjAxOTQ4OCIsImVtYWlsIjoicmFodWw5NjUwcmF5QGdtYWlsLmNvbSIsInZlcmlmaWVkZW1haWwiOiJyYWh1bDk2NTByYXlAZ21haWwuY29tIiwidmVyaWZpZWRtb2JpbGUiOm51bGwsIm1vYmlsZSI6Iis5MS05NjUwMTI0NzU2Iiwib25lYXV0aF9pZCI6IjMyODIyIiwibGFzdF9yZWFkX25vdGlmaWNhdGlvbiI6IjAiLCJwaG90byI6Imh0dHBzOi8vZ3JhcGguZmFjZWJvb2suY29tLzIzNDkyNjA4NzIwMTk0ODgvcGljdHVyZT90eXBlPWxhcmdlIiwiY29sbGVnZSI6IjAtLS1PVEhFUiAvIE5PVCBMSVNURUQgLyBOTyBDT0xMRUdFIC0tLTAiLCJvcmdhbml6YXRpb24iOm51bGwsInJvbGVJZCI6MiwiY3JlYXRlZEF0IjoiMjAxOS0wNS0xNFQxODowNToxOC44NTRaIiwidXBkYXRlZEF0IjoiMjAxOS0wNy0xNVQxMDoyNzoxNS40NTJaIiwiY2xpZW50SWQiOiI0M2YyOTY0ZC1jNjBhLTRjMTgtYTI1Ni1kNDFiMDI3NTdmZGIiLCJjbGllbnQiOiJhbmRyb2lkIiwiaXNUb2tlbkZvckFkbWluIjpmYWxzZSwiaWF0IjoxNTYzMjgwOTE0LCJleHAiOjE1NjMyODI0MTR9.j2RZsMZxwez_Yrh1uXCPStjymBpyl_ma78V_3FCZCDAGL4VvPYVCZhBPKhIbc9JJA_7J8O4PPSukWG71xMw9ubHL-HIUQZEiqAcYamIyuZDMtpDaQTkPRnMUHHxXD_krQtTxju5Zg2NqPH0Rsb8BATfC4Kl8AI2BEijmSpMD-mZhTRIbdoHpoPFbgckfchLzM2n-mbWser-sp-XHTS9wrU8Je6pXEGCagcBzoIent5gtpJApE6SifTNJ0uOcBYgUebEqAV62ConJ6ocqOubxm04Qng8p1YfH2GnlWFjt1fsqyfE1cPoRZEDUj9UGliw5FJVhSTA9FJCUZ9gRY1btqQ"
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
