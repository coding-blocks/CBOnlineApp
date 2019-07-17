package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.RunAttemptsId
import com.codingblocks.onlineapi.models.ContentsId
import com.codingblocks.onlineapi.models.Notes
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class OnlineJsonApiAuthenticatedTest {
    val jsonapi = Clients.onlineV2JsonApi

    @Before
    fun `set JWT`() {
        Clients.authJwt =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTI4ODE1LCJmaXJzdG5hbWUiOiJSYWh1bCIsImxhc3RuYW1lIjoiUmF5IiwidXNlcm5hbWUiOiJSYWh1bC1SYXktMjM0OTI2MDg3MjAxOTQ4OCIsImVtYWlsIjoicmFodWw5NjUwcmF5QGdtYWlsLmNvbSIsInZlcmlmaWVkZW1haWwiOiJyYWh1bDk2NTByYXlAZ21haWwuY29tIiwidmVyaWZpZWRtb2JpbGUiOm51bGwsIm1vYmlsZSI6Iis5MS05NjUwMTI0NzU2Iiwib25lYXV0aF9pZCI6IjMyODIyIiwibGFzdF9yZWFkX25vdGlmaWNhdGlvbiI6IjAiLCJwaG90byI6Imh0dHBzOi8vZ3JhcGguZmFjZWJvb2suY29tLzIzNDkyNjA4NzIwMTk0ODgvcGljdHVyZT90eXBlPWxhcmdlIiwiY29sbGVnZSI6IjAtLS1PVEhFUiAvIE5PVCBMSVNURUQgLyBOTyBDT0xMRUdFIC0tLTAiLCJvcmdhbml6YXRpb24iOm51bGwsInJvbGVJZCI6MiwiY3JlYXRlZEF0IjoiMjAxOS0wNS0xNFQxODowNToxOC44NTRaIiwidXBkYXRlZEF0IjoiMjAxOS0wNy0xNVQxMDoyNzoxNS40NTJaIiwiY2xpZW50SWQiOiI0M2YyOTY0ZC1jNjBhLTRjMTgtYTI1Ni1kNDFiMDI3NTdmZGIiLCJjbGllbnQiOiJhbmRyb2lkIiwiaXNUb2tlbkZvckFkbWluIjpmYWxzZSwiaWF0IjoxNTYzMjgwOTE0LCJleHAiOjE1NjMyODI0MTR9.j2RZsMZxwez_Yrh1uXCPStjymBpyl_ma78V_3FCZCDAGL4VvPYVCZhBPKhIbc9JJA_7J8O4PPSukWG71xMw9ubHL-HIUQZEiqAcYamIyuZDMtpDaQTkPRnMUHHxXD_krQtTxju5Zg2NqPH0Rsb8BATfC4Kl8AI2BEijmSpMD-mZhTRIbdoHpoPFbgckfchLzM2n-mbWser-sp-XHTS9wrU8Je6pXEGCagcBzoIent5gtpJApE6SifTNJ0uOcBYgUebEqAV62ConJ6ocqOubxm04Qng8p1YfH2GnlWFjt1fsqyfE1cPoRZEDUj9UGliw5FJVhSTA9FJCUZ9gRY1btqQ"
    }

    @Test
    fun `GET section`() {
        suspend {
            val courses = jsonapi.getSections("795").await().body()
            assertNotNull(courses)
        }
    }


    @Test
    fun `GET myCourses`() {
        val courses = jsonapi.getMyCourses().execute().body()
        assertNotNull(courses)
    }

    @Test
    fun `GET enrolledCourse`() {
        val course = jsonapi.enrolledCourseById("22684").execute().body()
        assertNotNull(course)
    }

    @Test
    fun `GET getSectionContents`() {
        val section = jsonapi.getSectionContents("/sections/6874/relationships/contents").execute().body()
        assertNotNull(section)
    }

    @Test
    fun `GET QuizById`() {
        val quiz = jsonapi.getQuizById("23").execute().body()
        assertNotNull(quiz)
    }

    @Test
    fun `GET Quiz`() {
        val quizzes = jsonapi.getQuizAttempt("3").execute().body()
        assertNotNull(quizzes)
    }

    @Test
    fun `GET getQuizAttempById`() {
        val quiz = jsonapi.getQuizAttemptById("6443").execute().body()
        assertNotNull(quiz)
    }


    @Test
    fun `GET Question`() {
        val questions = jsonapi.getQuestionById("22").execute().body()
        assertNotNull(questions)
    }

    @Test
    fun `GET Quiz Attempt`() {
        val quizAttempt = jsonapi.getQuizAttempt("23").execute().body()
        assertNotNull(quizAttempt)
    }

    @Test
    fun `GET DoubtByAttemptId `() {
        val doubts = jsonapi.getDoubtByAttemptId("22684").execute().body()
        assertNotNull((doubts))
    }

    @Test
    fun `GET NoteById `() {
        val notes = jsonapi.getNotesByAttemptId("22684").execute().body()
        assertNotNull(notes)
    }

    @Test
    fun `POST createNote`() {

        val runAttemt = RunAttemptsId("22685")
        val contentsId = ContentsId("233")
        val note = Notes()
        note.text = "demo note"
        note.duration = 1.2
        note.runAttempt = runAttemt
        note.content = contentsId

        val noteResponse = jsonapi.createNote(note).execute().body()
        assertNotNull(noteResponse)
    }

    @Test
    fun `GET Jobs `() {
//        val jobs = jsonapi.getJobs().execute().body()
//        jobs?.let {
//            assertNotNull(it)
//        }
    }
}
