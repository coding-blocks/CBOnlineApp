package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.Progress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class OnlineJsonApiAuthenticatedTest {
    val api = Clients.onlineV2JsonApi

    @Before
    fun `set JWT` () {
        Clients.authJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Iis5MS05NTgyMDU0NjY0Iiwib25lYXV0aF9pZCI6IjEyMDM1IiwibGFzdF9yZWFkX25vdGlmaWNhdGlvbiI6IjAiLCJwaG90byI6Imh0dHBzOi8vZ3JhcGguZmFjZWJvb2suY29tLzE3ODMzODk3MzUwMjg0NjAvcGljdHVyZT90eXBlPWxhcmdlIiwiY29sbGVnZSI6IkFtaXR5IFNjaG9vbCBPZiBFbmdpbmVlcmluZyAmIFRlY2hub2xvZ3kgKE5vaWRhKSIsInJvbGVJZCI6MiwiY3JlYXRlZEF0IjoiMjAxOC0wOS0yN1QxMzoxMDo1OS4zOTZaIiwidXBkYXRlZEF0IjoiMjAxOC0xMi0xN1QxMDo1OToxMS4wODdaIiwiY2xpZW50SWQiOiJhNWYwNzRlNi1jYmQzLTQ1NGEtYjk0MS1mYWZjM2YxZTgyZGYiLCJpc1Rva2VuRm9yQWRtaW4iOmZhbHNlLCJpYXQiOjE1NDUwNDU3OTEsImV4cCI6MTU0NTA0NzI5MX0.QKgA-lO6AEcVyb432k02iTk93O33jI8G4NID6eu0Z5VUI2Y6W-XgAGomJILgOOs2cmmBL4OhG2MutTo50_HLlk0bVB9m2TDV-b2q0mRaKU9PzE21lbUWqe9Lm9ONuU2AJngA2lD8rlQhLrx6wKXD526G3YoQHvBZ79FF14Op4Zvx9mjhVe5ixMQtLvgPNrbHx41-GLq1fpqJb96uPg3_UMzhjuqZXWGSZBy1Ugvlru0jTeXI8I36jF-ccUSC4YArYZdyfozuUDxZEubyTO7Dn9JQJLQEixphBqGc55OOxQ9FTFX0Mrhu_4Ubo37jtwhKywSQoz3nAovkde5Cto0kTQ"
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
}