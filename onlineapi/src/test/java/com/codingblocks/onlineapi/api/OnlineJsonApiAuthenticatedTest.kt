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
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsInZlcmlmaWVkZW1haWwiOiJhZ2dhcndhbHB1bGtpdDU5NkBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6Iis5MS05NTgyMDU0NjY0IiwibW9iaWxlIjoiKzkxLTk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJjb2xsZWdlIjoiQW1pdHkgU2Nob29sIE9mIEVuZ2luZWVyaW5nICYgVGVjaG5vbG9neSAoTm9pZGEpIiwib3JnYW5pemF0aW9uIjpudWxsLCJyb2xlSWQiOjEsImNyZWF0ZWRBdCI6IjIwMTgtMDktMjdUMTM6MTA6NTkuMzk2WiIsInVwZGF0ZWRBdCI6IjIwMTktMDctMTRUMTk6NDk6NDYuMzAxWiIsInJvbGUiOnsiaWQiOjEsIm5hbWUiOiJBZG1pbiIsImNyZWF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiIsInVwZGF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiJ9LCJjbGllbnRJZCI6IjU0NjExMTRhLTQyMmItNDFhOC1hODc4LTc5MzEyMjU1Zjc0NSIsImNsaWVudCI6ImFuZHJvaWQiLCJpYXQiOjE1NjMxMzM3ODYsImV4cCI6MTU2ODUzMzc4Nn0.jcYilL0CCTSjD_TfkKxcb2pp2eQv5GcdU8EBkU4cPiJgIoQmoIxPkZ6ckExemjSrzFoPFREl6byrLfMQQP1bKGxkBblFV6_VQVpd6OTgfvUW3Mld-0dFtVRNV4s-vLlqHqTmSjsCEMFfCv06RUTOe4lfFbKfx_PN0E_onRUHCNIiED3B5d6HDlBTVWIkZtfZBx00sGAY2i59FO9cQCG_u110kxW6NxdzfNaDYdyKU7WwERJiGkHkGyF599ugQmepP6d_fSlua0l3-iHNoXrGWzDZZqmD6deJknplLvuDJtIHqy4vVCe67tr-o6H4o09aP1dsR0KGn2lMTTmL65JpZg"
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
