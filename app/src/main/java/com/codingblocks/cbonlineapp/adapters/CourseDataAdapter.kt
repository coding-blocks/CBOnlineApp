package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.Course
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import org.jetbrains.anko.AnkoLogger
import java.util.*


class CourseDataAdapter(private var courseData: ArrayList<Course>?,
                        val context: Context,
                        private val courseWithInstructorDao: CourseWithInstructorDao, var type: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), AnkoLogger {
    fun setData(courseData: ArrayList<Course>) {
        this.courseData = courseData

        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {

        return courseData?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var viewHolder: RecyclerView.ViewHolder? = null
        when (type) {
            "myCourses" -> {
                val myCoursesView = LayoutInflater.from(parent.context).inflate(R.layout.my_course_card_horizontal, parent, false)
                viewHolder = MyCoursesViewHolder(myCoursesView) // view holder for normal items
            }
            "allCourses" -> {
                val allCoursesView = LayoutInflater.from(parent.context).inflate(R.layout.single_course_card_horizontal, parent, false)
                viewHolder = AllCoursesViewHolder(allCoursesView) // view holder for normal items
            }
        }

        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (type) {
            "myCourses" -> {
                val myCoursesViewHolder = holder as MyCoursesViewHolder
                myCoursesViewHolder.bindView(courseData!![position], courseWithInstructorDao, context)
            }
            "allCourses" -> {
                val allCoursesViewHolder = holder as AllCoursesViewHolder
                allCoursesViewHolder.bindView(courseData!![position], courseWithInstructorDao, context)
            }
        }
    }
}

