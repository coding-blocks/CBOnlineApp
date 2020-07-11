package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.CBOnlineCommunicator
import com.codingblocks.onlineapi.CBOnlineLib
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class OnlineJsonApiPublicTest {
    lateinit var api: OnlineJsonApi

    @Before
    fun `SET JWT`() {

        val jwt = ""
        val refreshToken = ""

        CBOnlineLib.initialize(object : CBOnlineCommunicator {
            override var authJwt: String
                get() = jwt
                set(value) {}
            override var refreshToken: String
                get() = refreshToken
                set(value) {}
        })
        api = CBOnlineLib.onlineV2JsonApi
    }

    @Test
    fun `GET courses|{id}`() = runBlocking {
        val course = api.getCourse("26").body()
        assertNotNull(course)
    }

    @Test
    fun `GET instructor`() = runBlocking {
        val instructor = api.getInstructor("6").body()
        assertNotNull(instructor)
    }

    @Test
    fun `GET recommended`() = runBlocking {
        val courses = api.getRecommendedCourses().body()
        assertNotNull(courses)
    }

    @Test
    fun `GET instructors`() = runBlocking {
        val instructors = api.getAllInstructors().body()
        assertNotNull(instructors)
    }

    @Test
    fun `GET allcourses`() = runBlocking {
        val courses = api.getAllCourses("0").body()
        assertNotNull(courses)
    }

    @Test
    fun `GET carouselCards`() = runBlocking {
        val carouselCards = api.getCarouselCards().body()
        assertNotNull(carouselCards)
    }

    @Test
    fun `GET upgradePack`() = runBlocking {
        val response = api.upgradePacks("97","LITE","145239")
        val upgradePack = response.body()
        assertNotNull(upgradePack)
        assertEquals(upgradePack!!.first().from,"LITE")
        assertEquals(upgradePack.first().to,"PREMIUM")
    }
}
