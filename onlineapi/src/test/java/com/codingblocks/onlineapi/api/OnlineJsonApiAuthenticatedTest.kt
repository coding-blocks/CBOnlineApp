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
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTM1OTM4LCJmaXJzdG5hbWUiOiJBZGl0eWEiLCJsYXN0bmFtZSI6Ikd1cHRhIiwidXNlcm5hbWUiOiJhZGl0eWFzdGljIiwiZW1haWwiOiJhZGl0eWFvZmZpY2lhbGd1cHRhQGdtYWlsLmNvbSIsInZlcmlmaWVkZW1haWwiOiJhZGl0eWFvZmZpY2lhbGd1cHRhQGdtYWlsLmNvbSIsInZlcmlmaWVkbW9iaWxlIjpudWxsLCJtb2JpbGUiOiIrOTEtODQ1ODg5MjIyNiIsIm9uZWF1dGhfaWQiOiIzNjM1NCIsImxhc3RfcmVhZF9ub3RpZmljYXRpb24iOiIwIiwicGhvdG8iOiJodHRwczovL2F2YXRhcnMyLmdpdGh1YnVzZXJjb250ZW50LmNvbS91LzExOTg4NTE3P3Y9NCIsImNvbGxlZ2UiOiJCaGFyYXRpIFZpZHlhcGVldGggVW5pdmVyc2l0eSBDb2xsZWdlIE9mIEVuZ2luZWVyaW5nIChQdW5lKSIsIm9yZ2FuaXphdGlvbiI6bnVsbCwicm9sZUlkIjoyLCJjcmVhdGVkQXQiOiIyMDE5LTA1LTI2VDAzOjQyOjU0Ljg4M1oiLCJ1cGRhdGVkQXQiOiIyMDE5LTA2LTA4VDAzOjQ3OjQ2LjQwOFoiLCJyb2xlIjp7ImlkIjoyLCJuYW1lIjoiU3R1ZGVudCIsImNyZWF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiIsInVwZGF0ZWRBdCI6IjIwMTctMDktMDdUMTA6NTg6MTkuOTkzWiJ9LCJjbGllbnRJZCI6IjM0ZDY5NDVjLTE0ZGItNDRhOC05NThhLWUzZGUyYzE3MzJjMCIsImNsaWVudCI6ImFuZHJvaWQiLCJpYXQiOjE1NTk5NjU2NjYsImV4cCI6MTU2NTM2NTY2Nn0.uGSl5tCh4ZGh_Ng7-2R8sLcJdQBzntSMH2WlTt6SIA7z2KD5cJffUxlaMs-z59rwmtMedPvAUZW_MF2peSvHNtfcHOERccoqAGRXXkHE5aDVQQt1yOH3iq1M4BMrbOSvJRclx6OTXvMBWfVpGsvO3A9OMUVm6SpSmO_rrUl2Ktn3oQGNDz9mEDdn8Vmn8dS5RX_cUFYWPkI0qSIzK-yNukafIFFC0ZSfYYqITihA-lYusqiVuktziFzaeO9-1drmr_P3-xA-oNwIQ6QeAmwTz8KwS1KuKWl1M_LQEs5EmfFPvtIb9GI1hSZX-6yD0Y9vjk3wxX8cqTjhA2jvj5u6gw"
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

//    @Test
//    fun `GET enrolledCourse`() {
//        val course = jsonapi.enrolledCourseById("22684").execute().body()
//        assertNotNull(course)
//    }

//    @Test
//    fun `GET getSectionContents`() {
//        val section = jsonapi.getSectionContents("/sections/6874/relationships/contents").execute().body()
//        assertNotNull(section)
//    }

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

//    @Test
//    fun `GET getQuizAttempById`() {
//        val quiz = jsonapi.getQuizAttemptById("6443").execute().body()
//        assertNotNull(quiz)
//    }


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

//    @Test
//    fun `GET DoubtByAttemptId `() {
//        val doubts = jsonapi.getDoubtByAttemptId("22684").execute().body()
//        assertNotNull((doubts))
//    }

//    @Test
//    fun `GET NoteById `() {
//        val notes = jsonapi.getNotesByAttemptId("22684").execute().body()
//        assertNotNull(notes)
//    }

//    @Test
//    fun `POST createNote`() {
//
//        val runAttemt = RunAttemptsId("22685")
//        val contentsId = ContentsId("233")
//        val note = Notes()
//        note.text = "demo note"
//        note.duration = 1.2
//        note.runAttempt = runAttemt
//        note.content = contentsId
//
//        val noteResponse = jsonapi.createNote(note).execute().body()
//        assertNotNull(noteResponse)
//    }
}
