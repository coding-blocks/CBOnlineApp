package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.CBOnlineCommunicator
import com.codingblocks.onlineapi.CBOnlineLib
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class OnlineJsonApiPublicTest {
    lateinit var api: OnlineJsonApi
    lateinit var hackApi: OnlineRestApi

    @Before
    fun `SET JWT`() {

        val jwt = "invalid"
        val refreshToken = "invalid"

        CBOnlineLib.initialize(object : CBOnlineCommunicator {
            override var authJwt: String
                get() = jwt
                set(value) {}
            override var refreshToken: String
                get() = refreshToken
                set(value) {}
            override var baseUrl: String
                get() = "api-online.codingblocks.xyz"
                set(value) {}
        })
        api = CBOnlineLib.onlineV2JsonApi
        hackApi = CBOnlineLib.hackapi
    }

    @Test
    fun `GET courses|{id}`() = runBlocking {
        val course = api.getCourse("26").body()!!
        assertThat(course.title).isEqualTo("Algo++  Data Structures & Algorithms")
    }

    @Test
    fun `GET courses|{slug}`() = runBlocking {
        val course = api.getCourse("c-plus-plus-online-course-for-beginners").body()!!
        assertThat(course.subtitle).contains("become a C++ Master")
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
    fun `GET fetchBanners`() = runBlocking {
        val banners = hackApi.getBanner().body()
        assertNotNull(banners)
    }
}
