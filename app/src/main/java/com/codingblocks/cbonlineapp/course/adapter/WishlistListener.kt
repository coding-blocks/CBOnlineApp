package com.codingblocks.cbonlineapp.course.adapter

import com.codingblocks.onlineapi.models.Course

interface WishlistListener {
    fun onWishListClickListener(course: Course, position: Int)
}
