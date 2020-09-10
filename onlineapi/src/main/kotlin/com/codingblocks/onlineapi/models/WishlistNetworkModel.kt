package com.codingblocks.onlineapi.models

import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type

@Type("user_course_wishlists")
data class WishlistNetworkModel(
    @Relationship("course")
    val course: Course? = null,
    @Relationship("user")
    val user: User? = null
) : BaseNetworkModel()
