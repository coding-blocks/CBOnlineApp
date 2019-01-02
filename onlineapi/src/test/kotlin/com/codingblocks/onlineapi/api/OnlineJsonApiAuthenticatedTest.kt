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
        Clients.authJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Iis5MS05NTgyMDU0NjY0Iiwib25lYXV0aF9pZCI6IjEyMDM1IiwibGFzdF9yZWFkX25vdGlmaWNhdGlvbiI6IjAiLCJwaG90byI6Imh0dHBzOi8vZ3JhcGguZmFjZWJvb2suY29tLzE3ODMzODk3MzUwMjg0NjAvcGljdHVyZT90eXBlPWxhcmdlIiwiY29sbGVnZSI6IkFtaXR5IFNjaG9vbCBPZiBFbmdpbmVlcmluZyAmIFRlY2hub2xvZ3kgKE5vaWRhKSIsInJvbGVJZCI6MiwiY3JlYXRlZEF0IjoiMjAxOC0wOS0yN1QxMzoxMDo1OS4zOTZaIiwidXBkYXRlZEF0IjoiMjAxOC0xMi0yOFQxMDowNzozOS4wNDNaIiwiY2xpZW50SWQiOiJmNWFiMDFjMC03YTAwLTQ5MWMtYjI5Ni0yN2EyN2EwYjYxMDYiLCJjbGllbnQiOiJ3ZWIiLCJpc1Rva2VuRm9yQWRtaW4iOmZhbHNlLCJpYXQiOjE1NDU5OTU5OTIsImV4cCI6MTU0NTk5NzQ5Mn0.OJui9f8_adxzFnfhYKyaX_HTicym7TUEntGn0p_LOGRAzQtLKhJJEmGvXSEbI1mqSVGPUqV553xC3XHej0H1-wZTJG8x0HqWqD_tdaHxReyt9MAdFszTXflCKzwYLQvbeD2JASleFfQ2qKkIDxuDX4BvES_ChzJdcKcrPHl4yelgTW3RxfiLU17J-c2hr_RglaHWIDfu0C8rQBN9iePtHgO41Is_qzpYvoOXOmZzy8fELl23NyxVhXFXfs1u0EYz5NBaKOvW_KuaE1fNnTTg_ctEjpAS6mfBtDNulZiPlwkwRA9eTVjkGHWhFD84WbxKveO80eMSge8M6wno8rSCaA"
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
        val quizzes = api.getQuizAttempt("3").execute().body()
        quizzes?.let {
            assertNotNull(it.size)
        }
    }
    @Test
    fun `GET Question`() {
        val questions = api.getQuestionById("22").execute().body()
        questions?.let {
            assertNotNull(it.title)
        }
    }
    @Test
    fun `GET Quiz Attempt`() {
        val quizAttempt = api.getQuizAttempt("3").execute().body()
        quizAttempt?.let {
            assertNotNull(it.size)
        }
    }
}