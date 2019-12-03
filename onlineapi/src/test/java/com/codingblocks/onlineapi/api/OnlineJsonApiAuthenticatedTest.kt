package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.ContentsId
import com.codingblocks.onlineapi.models.Notes
import com.codingblocks.onlineapi.models.RunAttemptsId
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@Suppress("UNCHECKED_CAST")
class OnlineJsonApiAuthenticatedTest {
    val jsonapi = Clients.onlineV2JsonApi

    @Before
    fun `set JWT`() {
        Clients.authJwt =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MjM4NTk0LCJmaXJzdG5hbWUiOiJ0ZXN0IiwibGFzdG5hbWUiOiJkdW1teSIsInVzZXJuYW1lIjoidGVzdGR1bW15IiwiZW1haWwiOiJzYXJ0aGFrajI0OThAZ21haWwuY29tIiwidmVyaWZpZWRlbWFpbCI6InNhcnRoYWtqMjQ5OEBnbWFpbC5jb20iLCJ2ZXJpZmllZG1vYmlsZSI6bnVsbCwibW9iaWxlIjoiKzkxLTEyMzQ1Njc4OTAiLCJvbmVhdXRoX2lkIjoiNDY0MTAiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjpudWxsLCJjb2xsZWdlIjoiQUJFUyBFbmdpbmVlcmluZyBDb2xsZWdlLCBOZXcgRGVsaGkiLCJncmFkdWF0aW9ueWVhciI6IjIwMjMiLCJvcmdhbml6YXRpb24iOm51bGwsInJvbGVJZCI6MywiY3JlYXRlZEF0IjoiMjAxOS0wOC0wNFQxODowMTozOC45NTNaIiwidXBkYXRlZEF0IjoiMjAxOS0xMS0yM1QxMzo1NjoxMy4wMzlaIiwicm9sZSI6eyJpZCI6MywibmFtZSI6Ik1vZGVyYXRvciIsImNyZWF0ZWRBdCI6IjIwMTgtMDktMDRUMTM6Mzg6MzEuODg1WiIsInVwZGF0ZWRBdCI6IjIwMTgtMDktMDRUMTM6Mzg6MzEuODg1WiJ9LCJjbGllbnRJZCI6ImRjODlhZDkzLWNiYTYtNGNmOS1hOGQzLTkxNDU0ODZkYjY5MCIsImNsaWVudCI6IndlYi1hZG1pbiIsImlhdCI6MTU3NDUxNzM3MywiZXhwIjoxNTc0NTE3NjczfQ.X9cux9xVeFof4VU6CRrxBzj0skmROd8zFf0qN7mskTuGh1jTMW_5U_SMQ2lmO34rLVDszxxqPUyXECKsdMSoSZL4EqkOPUrSwp6qbBCmMYtijX0UjVBhbrd9grWvyYcmxBcbDo8H3j9h64UV5Xwl1xkaSj5VlGwQA9Bhi6yxZkETBoIcntEwkffa1oYvj-Dhmwf9mj1IwyA1Ajxs5HeENO0Anr-NiVPgY6KE-gEs5A7GI_nRGk7cK7-HNBRRG4RurelV9WynBdtwqi-dKhiwEGd6COj65U6WURi2383uTJCkDQmOoiDi9h5iIK-mgdzIMelCn7Z-1tbvkEJLMrvaNg"
    }


//    @Test
//    fun getDoubts() {
//        runBlocking {
//            val doubts = GlobalScope.async { Clients.onlineV2JsonApi.getMyDoubts(acknowledgedId = "238594") }
//            val response = doubts.await()
//            val meta: HashMap<String, Any> = response.body()?.meta?.get("pagination") as HashMap<String, Any>
//            assertNotNull(meta)
//            assertNotNull(response.body())
//
//        }
//    }

    @Test
    fun `GET section`() {
        suspend {
            val courses = jsonapi.getSections("795")
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

    @Test
    fun `GET Jobs `() {
//        val jobs = jsonapi.getJobs().execute().body()
//        jobs?.let {
//            assertNotNull(it)
//        }
    }
}
