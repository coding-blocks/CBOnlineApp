package com.codingblocks.cbonlineapp.dashboard.mycourses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import kotlinx.android.synthetic.main.item_courses.view.*

class MyCourseListAdapter : ListAdapter<CourseInstructorPair, MyCourseListAdapter.ItemViewHolder>(DiffCallback()) {

    var onItemClick: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_courses, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemClickListener = onItemClick
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemClickListener: ItemClickListener? = null

        fun bind(item: CourseInstructorPair) = with(itemView) {
            courseTitleTv.text = item.courseRun.course.title
            if (item.instructor.isNotEmpty())
                courseInstructorTv.text = "Mentor: ${item.instructor.first().name} "
            if (item.instructor.size > 1) {
                courseInstructorTv.append("and ${item.instructor.size - 1} more")
            }
            val expired = item.courseRun.runAttempt.end.toLong() * 1000 < System.currentTimeMillis()
            progressContainer.isVisible = !expired
            openBtn.isVisible = !expired
            extensionTv.isVisible = expired
//            if (expired) {
//                //Todo Fix this
//                ImageViewCompat.setImageTintList(courseLogoImg, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.brownish_grey)))
//            }
            val progress = if (item.courseRun.runAttempt.completedContents > 0) (item.courseRun.runAttempt.completedContents / item.courseRun.run.totalContents.toDouble()) * 100 else 0.0
            progressTv.text = "${progress.toInt()} %"
            progressView1.progress = progress.toFloat()
            if (progress > 90) {
                progressView1.highlightView.colorGradientStart = context.getColor(R.color.kiwigreen)
                progressView1.highlightView.colorGradientEnd = context.getColor(R.color.tealgreen)
            } else {
                progressView1.highlightView.colorGradientStart = context.getColor(R.color.pastel_red)
                progressView1.highlightView.colorGradientEnd = context.getColor(R.color.dusty_orange)
            }
            setOnClickListener {
                if (expired) {
                    // TODO ( show extension modal )
                } else {
                    itemClickListener?.onClick(item.courseRun.course.cid, item.courseRun.run.crUid, item.courseRun.runAttempt.attemptId, item.courseRun.course.title)
                }
            }
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

interface ItemClickListener {
    fun onClick(id: String, runId: String, runAttemptId: String, name: String)
}
