package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Progress
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OnlineJsonApiAuthenticatedTest {
    val api = Clients.onlineV2JsonApi

    @Before
    fun `set JWT` () {
        Clients.authJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Iis5MS05NTgyMDU0NjY0Iiwib25lYXV0aF9pZCI6IjEyMDM1IiwibGFzdF9yZWFkX25vdGlmaWNhdGlvbiI6IjAiLCJwaG90byI6Imh0dHBzOi8vZ3JhcGguZmFjZWJvb2suY29tLzE3ODMzODk3MzUwMjg0NjAvcGljdHVyZT90eXBlPWxhcmdlIiwiY29sbGVnZSI6IkFtaXR5IFNjaG9vbCBPZiBFbmdpbmVlcmluZyAmIFRlY2hub2xvZ3kgKE5vaWRhKSIsInJvbGVJZCI6MiwiY3JlYXRlZEF0IjoiMjAxOC0wOS0yN1QxMzoxMDo1OS4zOTZaIiwidXBkYXRlZEF0IjoiMjAxOC0xMi0xMlQxMDozMDo1Ni45MDJaIiwicm9sZSI6eyJpZCI6MiwibmFtZSI6IlN0dWRlbnQiLCJjcmVhdGVkQXQiOiIyMDE3LTA5LTA3VDEwOjU4OjE5Ljk5M1oiLCJ1cGRhdGVkQXQiOiIyMDE3LTA5LTA3VDEwOjU4OjE5Ljk5M1oifSwiY2xpZW50SWQiOiI1ODBhM2Y4Mi1mNjc4LTQ3Y2YtYmRlZi1mYzhjNTQxZDliODIiLCJpc1Rva2VuRm9yQWRtaW4iOmZhbHNlLCJpYXQiOjE1NDQ2MTA2NTYsImV4cCI6MTU0NDYxMjE1Nn0.Vk-S_MFYhkazWQW6zUBgYJbiQa4u3E0ygkn0mFUqfYDtgB1I4qUmju-zeGuJf2bkGZrxZQvo8p6mUtf48IjD27Hbzcg5H5hZxu1X5aHkMxckU4_obD8Rb-7gvEF9ZB57x2ljII7oR7mQCaIipkNTtkVWlH9m0i4zlQ8CTh3XPn5Quijltcg0wNgNqTyHVYT4jCbU9A6zW71UIKXgagQ0ZMxBNYegLfPySUGA02u79kV_J_CIjp2ZCkLHsFRpMJTbGUa4ElRul3gNhnobkWmY10a37J3vr9nFqOMwxOHxExMvpt6yQ_goT0_9wPWK_lRb-jQn85ULLH1fJB_p7ocK-Q"
    }

    @Test
    fun `GET section`() {
        suspend {
            val courses = api.getSections("795").await().body()
            courses?.let {
                assertEquals("Python Basics", it.name)
            }
        }
    }

    @Test
    fun `GET myCourses`() {
        val courses = api.getMyCourses().execute().body()
        courses?.let {
            assertEquals(3, it.size)
        }
    }

    @Test
    fun `SET progress`() {
        val p  = Progress()
        p.status = "DONE"
        p.runs?.id = "8252"
        p.content?.id = "216"
        val progress = api.setProgress(p).execute().body()
        progress?.let {
            assertEquals(1, 1)
        }
    }
}