package com.codingblocks.onlineapi.api

import com.codingblocks.onlineapi.Clients
import org.junit.Assert.assertNotNull
import org.junit.Test

class OnlineJsonApiPublicTest {
    val api = Clients.onlineV2JsonApi

    @Test
    fun `GET courses|{id}`() {
        val course = api.courseById("26").execute().body()
        assertNotNull(course)
    }

    @Test
    fun `GET instructor`() {
        val instructor = api.instructorsById("6").execute().body()
        assertNotNull(instructor)
    }

    @Test
    fun `GET recommended`() {
        val courses = api.getRecommendedCourses().execute().body()
        assertNotNull(courses)
    }

    @Test
    fun `GET instructors`() {
        val instructors = api.instructors().execute().body()
        assertNotNull(instructors)
    }

    @Test
    fun `GET allcourses`(){
        val courses = api.getAllCourses().execute().body()
        assertNotNull(courses)
    }

    @Test
    fun `GET sections`(){
        val sections = api.getSections("1")
        assertNotNull(sections)
    }

    @Test
    fun `GET carouselCards`(){
        val carouselCards = api.carouselCards.execute().body()
        assertNotNull(carouselCards)
    }


}
