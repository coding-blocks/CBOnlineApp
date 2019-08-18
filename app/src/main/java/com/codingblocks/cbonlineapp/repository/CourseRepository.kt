package com.codingblocks.cbonlineapp.repository

import com.codingblocks.onlineapi.Clients

class CourseRepository {

    suspend fun getRating(id: String) = Clients.api.getCourseRating(id)

    suspend fun getCourseSections(id: String) = Clients.onlineV2JsonApi.getSections(id)
}
