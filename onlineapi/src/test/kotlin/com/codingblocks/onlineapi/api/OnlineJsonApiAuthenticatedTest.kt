package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Progress
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class OnlineJsonApiAuthenticatedTest {
    val jsonapi = Clients.onlineV2JsonApi
    val api = Clients.api


    @Before
    fun `set JWT` () {
        Clients.authJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Iis5MS05NTgyMDU0NjY0Iiwib25lYXV0aF9pZCI6IjEyMDM1IiwibGFzdF9yZWFkX25vdGlmaWNhdGlvbiI6IjAiLCJwaG90byI6Imh0dHBzOi8vZ3JhcGguZmFjZWJvb2suY29tLzE3ODMzODk3MzUwMjg0NjAvcGljdHVyZT90eXBlPWxhcmdlIiwiY29sbGVnZSI6IkFtaXR5IFNjaG9vbCBPZiBFbmdpbmVlcmluZyAmIFRlY2hub2xvZ3kgKE5vaWRhKSIsIm9yZ2FuaXphdGlvbiI6bnVsbCwicm9sZUlkIjoyLCJjcmVhdGVkQXQiOiIyMDE4LTA5LTI3VDEzOjEwOjU5LjM5NloiLCJ1cGRhdGVkQXQiOiIyMDE5LTAyLTAzVDE1OjE3OjQwLjU2MloiLCJjbGllbnRJZCI6IjI0MTQ0MDM4LTFmMDUtNGM0ZS1hYmJlLWExYzMxZjgyNTUyMCIsImNsaWVudCI6IndlYiIsImlzVG9rZW5Gb3JBZG1pbiI6ZmFsc2UsImlhdCI6MTU0OTQ1NzE2MCwiZXhwIjoxNTQ5NDU4NjYwfQ.AEhNW3SFiUfwkafuur3GwXwdXVfIYNF7tfPqPjwGLpx-h7AcSSx0c3QLj2VHccWe8pQyr9zN5_5fiU9egC-VFLgMQqSXurPIK9UO5GS-enfH4G5QJJ_QJjIPB7IEQCK0DEY7Dpf66VYG33XegO5TdfPz_tPUyhaXPpVEsDM1AOPnWl9kjMEBGACb0KmdNX4xTg5j6qWyMwxc9pa1d4pjuVvaQjwLpF43Jws0jnmErUvj8FLKXeoE8tdByiw-Bf0RAXIC0c2xa5bfmHQLa1w2io2SsN6un04MOHsPupwJjq8umNHHiMZrGRquApMx71YZ-htTwKfmWAX_bJUMJOJFsw"
    }

    @Test
    fun `GET section`() {
        suspend {
            val courses = jsonapi.getSections("795").await().body()
            courses?.let {
                assertEquals("Python Basics", it.name)
            }
        }
    }

    @Test
    fun `GET myCourses`() {
        val courses = jsonapi.getMyCourses().execute().body()
        courses?.let {
            assertNotEquals(4, it.size)
        }
    }

    @Test
    fun `GET enrolledCourse`() {
        val course = jsonapi.enrolledCourseById("8252").execute().body()
        course?.let {
            assertNotEquals(4, it.run?.sections)
        }
    }

    @Test
    fun `SET progress`() {
        val p  = Progress()
        p.id = "316797"
        p.status = "DONE"
        p.runs?.id = "8252"
        p.content?.id = "443"
        val progress = jsonapi.updateProgress("316797",p).execute().body()
        progress?.let {
            assertEquals(1, 1)
        }
    }

    @Test
    fun `GET Quiz`() {
        val quizzes = jsonapi.getQuizAttempt("3").execute().body()
        quizzes?.let {
            assertNotNull(it.size)
        }
    }
    @Test
    fun `GET Question`() {
        val questions = jsonapi.getQuestionById("22").execute().body()
        questions?.let {
            assertNotNull(it.title)
        }
    }
    @Test
    fun `GET Quiz Attempt`() {
        val quizAttempt = jsonapi.getQuizAttempt("3").execute().body()
        quizAttempt?.let {
            assertNotNull(it.size)
        }
    }
    @Test
    fun `GET Doubts `() {
        val doubts = api.getDoubts("22").execute().body()
        doubts?.let {
            assertNotNull(it)
        }
    }

    @Test
    fun `GET DoubtByAttemptId `() {
        val doubts = jsonapi.getDoubtByAttemptId("8252").execute().body()
        doubts?.let {
            assertNotNull(it)
        }
    }
}