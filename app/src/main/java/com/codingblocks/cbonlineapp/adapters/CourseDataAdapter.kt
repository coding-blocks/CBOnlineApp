package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.models.CourseInstructorHolder
import com.codingblocks.cbonlineapp.ui.MyCourseCardUi
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import kotlin.collections.ArrayList

class CourseDataAdapter(
    private var courseData: ArrayList<CourseInstructorHolder.CourseAndItsInstructor>,
    private val type: String = "allCourses"
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), AnkoLogger {

    val ui = MyCourseCardUi()

    fun setData(courseData: ArrayList<CourseInstructorHolder.CourseAndItsInstructor>) {
        this.courseData = courseData

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {

        return courseData.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (type) {
            "allCourses" -> AllCoursesViewHolder(ui.createView(AnkoContext.create(parent.context, parent)))
            // "myCourses" condition
            else -> MyCoursesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_course_card_horizontal, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        courseData?.get(position)?.let {
            when (type) {
                "myCourses" -> (holder as MyCoursesViewHolder).bindView(it)
                "allCourses" -> (holder as AllCoursesViewHolder).bindView(it, ui)
            }
        }
    }
}
