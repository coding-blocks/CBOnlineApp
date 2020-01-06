package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Test

class OnlineJsonApiPublicTest {
    val api = Clients.onlineV2JsonApi

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
        val courses = api.getAllCourses().body()
        assertNotNull(courses)
    }

    @Test
    fun `GET carouselCards`() = runBlocking {
        val carouselCards = api.getCarouselCards().body()
        assertNotNull(carouselCards)
    }


}
