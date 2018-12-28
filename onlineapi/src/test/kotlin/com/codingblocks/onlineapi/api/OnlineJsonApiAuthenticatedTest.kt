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
        Clients.authJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Iis5MS05NTgyMDU0NjY0Iiwib25lYXV0aF9pZCI6IjEyMDM1IiwibGFzdF9yZWFkX25vdGlmaWNhdGlvbiI6IjAiLCJwaG90byI6Imh0dHBzOi8vZ3JhcGguZmFjZWJvb2suY29tLzE3ODMzODk3MzUwMjg0NjAvcGljdHVyZT90eXBlPWxhcmdlIiwiY29sbGVnZSI6IkFtaXR5IFNjaG9vbCBPZiBFbmdpbmVlcmluZyAmIFRlY2hub2xvZ3kgKE5vaWRhKSIsInJvbGVJZCI6MiwiY3JlYXRlZEF0IjoiMjAxOC0wOS0yN1QxMzoxMDo1OS4zOTZaIiwidXBkYXRlZEF0IjoiMjAxOC0xMi0yOFQwOTo0NDozNy4yNzFaIiwicm9sZSI6eyJpZCI6MiwibmFtZSI6IlN0dWRlbnQiLCJjcmVhdGVkQXQiOiIyMDE3LTA5LTA3VDEwOjU4OjE5Ljk5M1oiLCJ1cGRhdGVkQXQiOiIyMDE3LTA5LTA3VDEwOjU4OjE5Ljk5M1oifSwiY2xpZW50SWQiOiJhMDlhYTgwMy0xNWVjLTQ1MzgtOTY1NC0zYTY5ZTg4MzkxZTYiLCJjbGllbnQiOiJhbmRyb2lkIiwiaWF0IjoxNTQ1OTkwMjc3LCJleHAiOjE1NTEzOTAyNzd9.RYMUV_wa81CLtQl0_0vJyClD4-lGNdELDkPQPG0UH2NCrWNL0N1pdfEql1oOfVuTVk9XaLeAUGDMjRTBMsptjjPOdXqG3jgN3gNvSr328R9Mfixb8dcGDyCsaGmC-4yUbpUtAGwzUs6nHWfHJeFe6QvYBfIKs8gwAqrds5fZGursZaaE3EM0Ung-ZZRX3OAAQt4p8EdNHtlAGTB8bguplP32Jp-0wQsMIlF-mrCJD5Xc0_rcIUwGqjdrQ_wZ6yLwnJk9wVmm9_GA6M3VOqRSGY0zkg2GNzKqULj75LQyxu7ydk0-naLRIWVuhNbhW26kPzFv9owg6IlWS11q0ODCFA"
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