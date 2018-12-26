package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Progress
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OnlineJsonApiAuthenticatedTest {
    val api = Clients.onlineV2JsonApi

    @Before
    fun `set JWT` () {
        Clients.authJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Iis5MS05NTgyMDU0NjY0Iiwib25lYXV0aF9pZCI6IjEyMDM1IiwibGFzdF9yZWFkX25vdGlmaWNhdGlvbiI6IjAiLCJwaG90byI6Imh0dHBzOi8vZ3JhcGguZmFjZWJvb2suY29tLzE3ODMzODk3MzUwMjg0NjAvcGljdHVyZT90eXBlPWxhcmdlIiwiY29sbGVnZSI6IkFtaXR5IFNjaG9vbCBPZiBFbmdpbmVlcmluZyAmIFRlY2hub2xvZ3kgKE5vaWRhKSIsInJvbGVJZCI6MiwiY3JlYXRlZEF0IjoiMjAxOC0wOS0yN1QxMzoxMDo1OS4zOTZaIiwidXBkYXRlZEF0IjoiMjAxOC0xMi0xN1QxMjoyMjo1OC4yNjlaIiwicm9sZSI6eyJpZCI6MiwibmFtZSI6IlN0dWRlbnQiLCJjcmVhdGVkQXQiOiIyMDE3LTA5LTA3VDEwOjU4OjE5Ljk5M1oiLCJ1cGRhdGVkQXQiOiIyMDE3LTA5LTA3VDEwOjU4OjE5Ljk5M1oifSwiY2xpZW50SWQiOiI1OTZiNjc2MS0wZjA0LTRhOWMtODkzYS04ZDA2NmIzZWJkZDkiLCJpc1Rva2VuRm9yQWRtaW4iOmZhbHNlLCJpYXQiOjE1NDUwNDkzNzgsImV4cCI6MTU0NTA1MDg3OH0.Jth4KZ2OYwC5k3sMIQTNTE7YX2TYoCIouucN11xa25snHaU3fq7jjZNCVYESl5MfBOHNcchshpNF3BQmimBU8E0SOT18g_U89FqjSHB4wIVowLCXQXDHGHkFy1bX5IU85g7HPjv4IscfIrF47W3x9cYDAKkGLj12qJV0UOBG0VEaWzecaudeEBLTgMvLSMhKhnNyTLRlmouydHl53qJTkmeRqpbQJHpnf6AflYK9uNUAEom2mCgc5LB7JFvzPq9QSzaf_erlaJf-uNY4NCvvENCC_4oNXwGLtRA-l_xXnB9K38LYNRfTwQBRrBiFd4ny0YKRc_nXHep6f2OS5vcyug"
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
            assertNotEquals(4, it.size)
        }
    }

    @Test
    fun `SET progress`() {
        val p  = Progress()
        p.id = "316797"
        p.status = "DONE"
        p.runs?.id = "8252"
        p.content?.id = "443"
        val progress = api.updateProgress("316797",p).execute().body()
        progress?.let {
            assertEquals(1, 1)
        }
    }

    @Test
    fun `GET Quiz`() {
        val quizzes = api.getQuizById("3").execute().body()
        quizzes?.let {
            assertNotNull(it.title)
        }
    }
    @Test
    fun `GET Question`() {
        val quizzes = api.getQuestionById("22").execute().body()
        quizzes?.let {
            assertNotNull(it.title)
        }
    }
}