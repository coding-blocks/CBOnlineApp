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
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsInZlcmlmaWVkZW1haWwiOiJhZ2dhcndhbHB1bGtpdDU5NkBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6Iis5MS05NTgyMDU0NjY0IiwibW9iaWxlIjoiKzkxLTk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJjb2xsZWdlIjoiQW1pdHkgU2Nob29sIE9mIEVuZ2luZWVyaW5nICYgVGVjaG5vbG9neSAoTm9pZGEpIiwib3JnYW5pemF0aW9uIjpudWxsLCJyb2xlSWQiOjIsImNyZWF0ZWRBdCI6IjIwMTgtMDktMjdUMTM6MTA6NTkuMzk2WiIsInVwZGF0ZWRBdCI6IjIwMTktMDctMDFUMTU6MTM6MjguNzE1WiIsInJvbGUiOnsiaWQiOjIsIm5hbWUiOiJTdHVkZW50IiwiY3JlYXRlZEF0IjoiMjAxNy0wOS0wN1QxMDo1ODoxOS45OTNaIiwidXBkYXRlZEF0IjoiMjAxNy0wOS0wN1QxMDo1ODoxOS45OTNaIn0sImNsaWVudElkIjoiOGI3YThhNzMtYWJkMS00NDE1LThiNjctMjQyMjZmYTJkYzM0IiwiY2xpZW50IjoiYW5kcm9pZCIsImlhdCI6MTU2MTk5NDAwOCwiZXhwIjoxNTY3Mzk0MDA4fQ.uP7MI9jUUsuK16ljdeM67ymP4Uwieav_6Pet8Le70TcHnLWEzdQCMNE5PU5vm8xKKRtsblk2ZnXq2qdyf-TiWSCQvTevlC63GHWgOdAiaH3MOnv2LgBDP0OBzuRqZYFwxvTkjXHookatI9U1rxVISIR7eUE9YhPQAZI30-R44FQqjOsOdeuuI9iHCqHtIJ3KKJaZgT8pDPVcRYNke0nuN-Nh60f5HgpICNV5XyAjR5T5sXaEDulqKjqaCddnHzhz0vEVgum3wHj3P6qKBY276tjWQZ_1-mrMFQR8WbQvwZHAT8arPE8mOK4FA31qaPBmUT7zb21fDnr8BDtNwIaePg"
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
