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
        Clients.authJwt = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJ1c2VybmFtZSI6ImFnZ2Fyd2FscHVsa2l0NTk2LWciLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Iis5MS05NTgyMDU0NjY0Iiwib25lYXV0aF9pZCI6IjEyMDM1IiwibGFzdF9yZWFkX25vdGlmaWNhdGlvbiI6IjAiLCJwaG90byI6Imh0dHBzOi8vZ3JhcGguZmFjZWJvb2suY29tLzE3ODMzODk3MzUwMjg0NjAvcGljdHVyZT90eXBlPWxhcmdlIiwiY29sbGVnZSI6IkFtaXR5IFNjaG9vbCBPZiBFbmdpbmVlcmluZyAmIFRlY2hub2xvZ3kgKE5vaWRhKSIsInJvbGVJZCI6MiwiY3JlYXRlZEF0IjoiMjAxOC0wOS0yN1QxMzoxMDo1OS4zOTZaIiwidXBkYXRlZEF0IjoiMjAxOC0xMi0xM1QxOTozMTo0Mi43MzhaIiwicm9sZSI6eyJpZCI6MiwibmFtZSI6IlN0dWRlbnQiLCJjcmVhdGVkQXQiOiIyMDE3LTA5LTA3VDEwOjU4OjE5Ljk5M1oiLCJ1cGRhdGVkQXQiOiIyMDE3LTA5LTA3VDEwOjU4OjE5Ljk5M1oifSwiY2xpZW50SWQiOiI1OGIxNGJlMy1lNTVjLTQzNmItODYxMy1kMDFlYjA1OTFmOTgiLCJpc1Rva2VuRm9yQWRtaW4iOmZhbHNlLCJpYXQiOjE1NDQ3Mjk1MDIsImV4cCI6MTU0NDczMTAwMn0.aLFJG_HLJAPxXwsFJafTXBauS9nuT-XYnWpLOl_5SVVM7KgnM1AL9irr4yUq69JU-04elz7dLP36DbpP1nVF7U3yfoRywfzvluupPPyw6sguklXJKqN6w4fS9eV6pRZG9LdOMqkJZp0vPfu0WEF2-tkvNqlxC9C1BRhNa9pfmHNLTHX0J4uH2k_B3x5UVhJZopk4D1Ofe-Y8JkS8_LgkiWHKYBD7q5zjSOjwTXmljYcby7NU1Tv69I9_91d-rOiPp3WYBH16E98v0-_SvuVvy38fkojUpUZoImGH6p2-xOSCmb8nlD_EJDHNzVLtWiN8keSSgAcmKpK2Ilkfjuz7oQ"
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