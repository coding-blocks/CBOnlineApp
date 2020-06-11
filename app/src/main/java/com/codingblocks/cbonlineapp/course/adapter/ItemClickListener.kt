package com.codingblocks.cbonlineapp.course.adapter

import android.widget.ImageView
import com.codingblocks.onlineapi.models.Course

interface ItemClickListener {
    fun onClick(id: String, name: String, logo: ImageView)
}
