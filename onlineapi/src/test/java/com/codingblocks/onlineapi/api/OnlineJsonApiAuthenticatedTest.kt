package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import org.junit.Before
import org.junit.Test

class OnlineJsonApiAuthenticatedTest {
    val jsonapi = Clients.onlineV2JsonApi

    @Before
    fun `set JWT`() {
        Clients.authJwt =
            "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwiZmlyc3RuYW1lIjoiQXJuYXYiLCJsYXN0bmFtZSI6Ikd1cHRhIiwidXNlcm5hbWUiOiJjaGFtcGlvbnN3aW1tZXItdCIsImVtYWlsIjoiYXJuYXZAY29kaW5nYmxvY2tzLmNvbSIsInZlcmlmaWVkZW1haWwiOiJhcm5hdkBjb2RpbmdibG9ja3MuY29tIiwidmVyaWZpZWRtb2JpbGUiOiIrOTEtODgwMDIzMzI2NiIsIm1vYmlsZSI6Iis5MS04ODAwMjMzMjY2Iiwib25lYXV0aF9pZCI6IjQiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9taW5pby5jb2RpbmdibG9ja3MuY29tL29uZWF1dGgtYXNzZXRzL3VzZXI0XzE1Mzg1ODI0OTg2OTMucG5nIiwiY29sbGVnZSI6IkRlbGhpIFRlY2hub2xvZ2ljYWwgVW5pdmVyc2l0eSAoTmV3LURlbGhpKSIsImdyYWR1YXRpb255ZWFyIjpudWxsLCJvcmdhbml6YXRpb24iOm51bGwsInJvbGVJZCI6MSwiY3JlYXRlZEF0IjoiMjAxNy0wNC0wM1QwNzoxNTo1MS43MDVaIiwidXBkYXRlZEF0IjoiMjAxOS0xMi0yMlQxODoxMzozMi42ODBaIiwiY2xpZW50SWQiOiIxNmM5ZThjNC03MWNmLTQ5NWMtYjYwZS1kMmVmNzliYzI1YjciLCJjbGllbnQiOiJ3ZWIiLCJpc1Rva2VuRm9yQWRtaW4iOmZhbHNlLCJpYXQiOjE1NzgxODE1MjUsImV4cCI6MTU3ODE4MzAyNX0.MMbLbvFE_xpayBBa8ecQw9zWLPgLf7WXTDOyWY38dn1rGZK6GiaX9_SLRTO3YantjDLa9X6L9lJi8ywfHFvqizdE8x3ghi2KPJgUDSBRAkThN5lPmYBqUMsnGfxKPTiHID6sAdejWf3ObAibvbzTfTSxTpouwKsGSiPhtk8e78NkY1_mWx9WEhdGJ_rUT2q3Vi9Zl1Mf3DI4BHHJpghW2q3bKwAJgQekf29TgH61OcjJSntw2DhUoaQ2UDa6ZCI4thl4zve-ueWZVmMr4GuuSWjola3ipz6wQMuQn9jHAOM7s86ZkrAT7L26tQR3RPpd4K-fNyAd_eIBY5RqhQ9f9A"
    }


//    @Test
//    fun getSectionContent() {
//        runBlocking {
//            val content = GlobalScope.async { Clients.onlineV2JsonApi.getSectionContents(sectionId = "19702") }
//            val response = content.await()
//            assertNotNull(response.body())
//
//        }
//    }
//
//    @Test
//    fun `GET section`() {
//        runBlocking {
//            val courses = jsonapi.getSections("795")
//            assertNotNull(courses)
//        }
//    }
//
//
//    @Test
//    fun `GET myCourses`() {
//        runBlocking {
//            val courses = jsonapi.getMyCourses().body()?.get()
//            assertNotNull(courses)
//            assertTrue(courses?.isNotEmpty() == true)
//        }
//    }
//
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

//    @Test
//    fun `GET QuizById`() {
//        val quiz = jsonapi.getQuizById("23").execute().body()
//        assertNotNull(quiz)
//    }
//
//    @Test
//    fun `GET Quiz`() {
//        val quizzes = jsonapi.getQuizAttempt("3").execute().body()
//        assertNotNull(quizzes)
//    }
//
//    @Test
//    fun `GET getQuizAttempById`() {
//        val quiz = jsonapi.getQuizAttemptById("6443").execute().body()
//        assertNotNull(quiz)
//    }
//
//
//    @Test
//    fun `GET Question`() {
//        val questions = jsonapi.getQuestionById("22").execute().body()
//        assertNotNull(questions)
//    }
//
//    @Test
//    fun `GET Quiz Attempt`() {
//        val quizAttempt = jsonapi.getQuizAttempt("23").execute().body()
//        assertNotNull(quizAttempt)
//    }

//    @Test
//    fun `GET DoubtByAttemptId `() {
//        val doubts = jsonapi.getDoubtByAttemptId("22684").execute().body()
//        assertNotNull((doubts))
//    }
//
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

    @Test
    fun `GET Jobs `() {
//        val jobs = jsonapi.getJobs().execute().body()
//        jobs?.let {
//            assertNotNull(it)
//        }
    }
}
