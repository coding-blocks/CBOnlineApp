package com.codingblocks.cbonlineapp.dashboard.mycourses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.dashboard.home.setGradientColor
import com.codingblocks.cbonlineapp.database.models.CourseInstructorPair
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import kotlinx.android.synthetic.main.item_courses.view.*

class MyCourseListAdapter(val type: String = "DEFAULT") : ListAdapter<CourseInstructorPair, RecyclerView.ViewHolder>(DiffCallback()) {

    var onItemClick: ItemClickListener? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (type) {
            "RUN" -> {
                RunViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_courses, parent, false))
            }
            else -> {
                DefaultViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_courses, parent, false))
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (type) {
            "RUN" -> {
                (holder as RunViewHolder).apply {
                    bind(getItem(position))
                    itemClickListener = onItemClick
                }
            }
            else -> {
                (holder as DefaultViewHolder).apply {
                    bind(getItem(position))
                    itemClickListener = onItemClick
                }
            }
        }
    }
}

class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var itemClickListener: ItemClickListener? = null

    fun bind(item: CourseInstructorPair) = with(itemView) {
        courseTitleTv.text = item.courseRun.course.title
        courseInstructorTv.text = item.courseRun.run.crName
        progressContainer.isVisible = false
        courseLogoImg.loadImage(item.courseRun.course.logo)

        setOnClickListener {
            itemClickListener?.onClick(
                item.courseRun.course.cid,
                item.courseRun.run.crUid,
                item.courseRun.runAttempt.attemptId,
                item.courseRun.course.title
            )
        }
    }
}

class DefaultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var itemClickListener: ItemClickListener? = null

    fun bind(item: CourseInstructorPair) = with(itemView) {
        courseTitleTv.text = item.courseRun.course.title
        if (item.instructor.isNotEmpty())
            courseInstructorTv.text = "Mentor: ${item.instructor.first().name} "
        if (item.instructor.size > 1) {
            courseInstructorTv.append("and ${item.instructor.size - 1} more")
        }
        courseLogoImg.loadImage(item.courseRun.course.logo)
        val expired = item.courseRun.runAttempt.end.toLong() * 1000 < System.currentTimeMillis()
        progressContainer.isVisible = !expired
        openBtn.isVisible = !expired
        extensionTv.isVisible = expired
        val progress = item.courseRun.getProgress()
        progressTv.text = context.getString(R.string.progress, progress.toInt())
        progressView1.setGradientColor(progress)
        setOnClickListener {
            itemClickListener?.onClick(
                item.courseRun.course.cid,
                item.courseRun.run.crUid,
                item.courseRun.runAttempt.attemptId,
                item.courseRun.course.title)
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
