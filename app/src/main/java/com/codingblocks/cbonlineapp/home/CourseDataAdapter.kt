package com.codingblocks.cbonlineapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import com.codingblocks.cbonlineapp.home.mycourses.MyCoursesViewHolder
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger

class CourseDataAdapter(
    private val type: String = "allCourses"
) : PagedListAdapter<CourseInstructorPair, RecyclerView.ViewHolder>(diffCallback), AnkoLogger {

    val ui = CourseCardUi()

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (type) {
            "allCourses" -> AllCoursesViewHolder(ui.createView(AnkoContext.create(parent.context, parent)))
            else -> MyCoursesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_course_card_horizontal, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (type) {
            "myCourses" -> (holder as MyCoursesViewHolder).bindView(getItem(position))
            "allCourses" -> (holder as AllCoursesViewHolder).bindView(getItem(position), ui)
        }
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<CourseInstructorPair>() {
            override fun areItemsTheSame(oldItem: CourseInstructorPair, newItem: CourseInstructorPair): Boolean =
                oldItem.courseRun.crUid == newItem.courseRun.crUid

            override fun areContentsTheSame(oldItem: CourseInstructorPair, newItem: CourseInstructorPair): Boolean =
                oldItem == newItem
        }
    }
}
