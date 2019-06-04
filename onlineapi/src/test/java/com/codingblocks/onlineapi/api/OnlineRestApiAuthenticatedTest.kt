package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class OnlineRestApiAuthenticatedTest {

    val restapi = Clients.api

    @Before
    fun `set JWT`() {
        Clients.authJwt =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTI4ODE1LCJmaXJzdG5hbWUiOiJSYWh1bCIsImxhc3RuYW1lIjoiUmF5IiwidXNlcm5hbWUiOiJSYWh1bC1SYXktMjM0OTI2MDg3MjAxOTQ4OCIsImVtYWlsIjoicmFodWw5NjUwcmF5QGdtYWlsLmNvbSIsInZlcmlmaWVkZW1haWwiOiJyYWh1bDk2NTByYXlAZ21haWwuY29tIiwidmVyaWZpZWRtb2JpbGUiOm51bGwsIm1vYmlsZSI6Iis5MS05NjUwMTI0NzU2Iiwib25lYXV0aF9pZCI6IjMyODIyIiwibGFzdF9yZWFkX25vdGlmaWNhdGlvbiI6IjAiLCJwaG90byI6Imh0dHBzOi8vZ3JhcGguZmFjZWJvb2suY29tLzIzNDkyNjA4NzIwMTk0ODgvcGljdHVyZT90eXBlPWxhcmdlIiwiY29sbGVnZSI6IjAtLS1PVEhFUiAvIE5PVCBMSVNURUQgLyBOTyBDT0xMRUdFIC0tLTAiLCJvcmdhbml6YXRpb24iOm51bGwsInJvbGVJZCI6MiwiY3JlYXRlZEF0IjoiMjAxOS0wNS0xNFQxODowNToxOC44NTRaIiwidXBkYXRlZEF0IjoiMjAxOS0wNi0wMlQxMTozMDoyNy4wNTRaIiwicm9sZSI6eyJpZCI6MiwibmFtZSI6IlN0dWRlbnQiLCJjcmVhdGVkQXQiOiIyMDE3LTA5LTA3VDEwOjU4OjE5Ljk5M1oiLCJ1cGRhdGVkQXQiOiIyMDE3LTA5LTA3VDEwOjU4OjE5Ljk5M1oifSwiY2xpZW50SWQiOiI3MGIyZWI0NS03NjFlLTQ3MTItYjdjNi02YWI3MTQ2ODFhMmYiLCJjbGllbnQiOiJhbmRyb2lkIiwiaWF0IjoxNTU5NDc1MDI3LCJleHAiOjE1NjQ4NzUwMjd9.GEnoIyi4zIJEJfLqI11fB_2XF6CXMX4p_A5tmpcUa7Rwxy5U7XhP_iYftwyiadgjOCWW_CcvvVKLZlTOM7rmK23Ms4-ptf4lC6gp0pzjP5UV_Ab6aulfrsBFOwoRyL0NgrUKNHNb4XfuRPtwFiXVtBvp9Ln5uRXbFfbr3ewj9v_C_roBeB6PNYu2b7O8h3G4OqVLI6xjhmM_U89s5Pm7hhBVI_95Eom0LweavDZSisVViwLYuE4L82g1yXpbeDi-Q6BJsqFA05TDlVOalt6cKHzjFlMfgtFOomM6hAFfqL3lWaOqmndWU3ZI3q3x0cJ1Zv7xeZISqXboSQfrhdOkcA"
    }

    @Test
    fun `GET getMyCourseProgress`(){
        val progress = restapi.getMyCourseProgress("22684").execute().body()
        assertNotNull(progress)
    }

    @Test
    fun `GET getOTP`(){
        val otp = restapi.getOtp("7f97136df7cc4f349e0129040d85b79f","4688","22685").execute().body()
        assertNotNull(otp)
    }

    @Test
    fun `GET getMe`(){
        val me = restapi.getMe().execute().body()
        assertNotNull(me)
    }

    @Test
    fun `GET enrollTrial`(){
        val enroll = restapi.enrollTrial("262").execute().body()
        assertNotNull(enroll)
    }

    @Test
    fun `GET Doubts `() {
        val doubts = restapi.getDoubts("25").execute().body()
        assertNotNull(doubts)
    }

}
