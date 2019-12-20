package com.codingblocks.cbonlineapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger

class CourseDataAdapter(
    private val type: String = "allCourses",
    var list: ArrayList<CourseInstructorPair> = arrayListOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), AnkoLogger {

    override fun getItemCount(): Int = list.size

    val ui = CourseCardUi()

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun submitList(courses: List<CourseInstructorPair>) {
        list.clear()
        list.addAll(courses)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AllCoursesViewHolder(ui.createView(AnkoContext.create(parent.context, parent)))
//            else -> MyCoursesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_course_card_horizontal, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (type) {
            "allCourses" -> (holder as AllCoursesViewHolder).bindView(list[position], ui)
        }
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<CourseInstructorPair>() {
            override fun areItemsTheSame(oldItem: CourseInstructorPair, newItem: CourseInstructorPair): Boolean =
                oldItem.courseRun == newItem.courseRun

            override fun areContentsTheSame(oldItem: CourseInstructorPair, newItem: CourseInstructorPair): Boolean =
                oldItem.courseRun.crUid == newItem.courseRun.crUid
        }
    }
}
