package com.codingblocks.cbonlineapp.course.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.models.Course

class CourseListAdapter(val type: String = "") : ListAdapter<Course, CourseViewHolder>(CourseDiffUtil()) {

    init {
        setHasStableIds(type != "WISHLIST")
    }

    var onItemClick: ItemClickListener? = null
    var wishlistListener: WishlistListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        return CourseViewHolder(
            when (type) {
                "POPULAR" ->
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_course_card_secondary, parent, false)
                "LIST" ->
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_course_card_list, parent, false)
                "TRACKS" ->
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_track_course, parent, false)
                "WISHLIST" ->
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_course_wishlist, parent, false)
                else ->
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_course_card, parent, false)
            }
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position), type)
        holder.itemClickListener = onItemClick
        holder.wishlistListener = wishlistListener
    }
}
