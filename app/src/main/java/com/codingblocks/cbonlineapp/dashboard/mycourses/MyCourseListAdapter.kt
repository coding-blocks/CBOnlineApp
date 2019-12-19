package com.codingblocks.cbonlineapp.dashboard.mycourses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import kotlinx.android.synthetic.main.item_courses.view.*

class MyCourseListAdapter : ListAdapter<CourseInstructorPair, MyCourseListAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_courses, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: CourseInstructorPair) = with(itemView) {
            courseTitleTv.text = item.courseRun.course.title
            if (item.instructor.isNotEmpty())
                courseInstructorTv.text = "Mentor: ${item.instructor.first().name} and ${item.instructor.size} more"
            val progess = (0..100).random()
            progressTv.text = "$progess %"
            progressView1.progress = progess.toFloat()
            if (progess > 90) {
                progressView1.highlightView.colorGradientStart = context.getColor(R.color.kiwigreen)
                progressView1.highlightView.colorGradientEnd = context.getColor(R.color.tealgreen)
            } else {
                progressView1.highlightView.colorGradientStart = context.getColor(R.color.pastel_red)
                progressView1.highlightView.colorGradientEnd = context.getColor(R.color.dusty_orange)
            }

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CourseInstructorPair>() {
        override fun areItemsTheSame(oldItem: CourseInstructorPair, newItem: CourseInstructorPair): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CourseInstructorPair, newItem: CourseInstructorPair): Boolean {
            return oldItem.sameAndEqual(newItem)
        }
    }
}
