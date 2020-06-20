package com.codingblocks.cbonlineapp.course.adapter

import android.widget.ImageView

interface ItemClickListener {
    fun onClick(id: String, name: String, logo: ImageView)
}
