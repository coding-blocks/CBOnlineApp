package com.codingblocks.cbonlineapp.course.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.ListAdapter
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.models.Course

class CourseListAdapter(val type: String = "") : ListAdapter<Course, CourseViewHolder>(CourseDiffUtil()) {

    companion object {
        const val TYPE_WISHLIST = "WISHLIST"
        const val TYPE_POPULAR = "POPULAR"
        const val TYPE_LIST = "LIST"
        const val TYPE_TRACKS = "TRACKS"
    }

    init {
        setHasStableIds(type != TYPE_WISHLIST)
    }

    var onItemClick: ItemClickListener? = null
    var wishlistListener: WishlistListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        return CourseViewHolder(
            when (type) {
                TYPE_POPULAR -> getInflatedView(R.layout.item_course_card_secondary, parent, false)
                TYPE_LIST -> getInflatedView(R.layout.item_course_card_list, parent, false)
                TYPE_TRACKS -> getInflatedView(R.layout.item_track_course, parent, false)
                TYPE_WISHLIST -> getInflatedView(R.layout.item_course_wishlist, parent, false)
                else -> getInflatedView(R.layout.item_course_card, parent, false)
            }
        )
    }

    private fun getInflatedView(@LayoutRes layoutId: Int, parent: ViewGroup, attachToRoot: Boolean): View =
        LayoutInflater.from(parent.context).inflate(layoutId, parent, attachToRoot)

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position), type)
        holder.itemClickListener = onItemClick
        holder.wishlistListener = wishlistListener
    }
}
