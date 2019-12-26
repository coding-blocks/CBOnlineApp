package com.codingblocks.cbonlineapp.course

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.greater
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.Course
import kotlinx.android.synthetic.main.item_course_card.view.*

class CourseListAdapter : ListAdapter<Course, CourseListAdapter.ItemViewHolder>(DiffCallback()) {

    var onItemClick: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_course_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemClickListener = onItemClick
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemClickListener: ItemClickListener? = null

        fun bind(item: Course) = with(itemView) {
            courseLogo.loadImage(item.logo)
            ViewCompat.setTransitionName(courseLogo, item.title)
            courseCover.loadImage(item.coverImage)
            chip.text = when (item.difficulty) {
                "0" -> "Beginner"
                "1" -> "Advanced"
                "2" -> "Expert"
                else -> "Beginner"
            }
            courseCardTitleTv.text = item.title
            if (!item.instructors.isNullOrEmpty()) {
                courseCardInstructorsTv.text = "${item.instructors?.first()?.name}"
                item.instructors?.first()?.photo?.let { courseCardInstructorImg1.loadImage(it) }
                if (item.instructors!!.size > 1) {
                    courseCardInstructorsTv.append(", ${item.instructors!![1].name}")
                    item.instructors!![1].photo?.let { courseCardInstructorImg2.loadImage(it) }
                } else {
                    courseCardInstructorImg2.visibility = View.INVISIBLE
                }
            }
            var list = item.runs?.filter { run ->
                !run.unlisted && run.enrollmentEnd.greater() && !run.enrollmentStart.greater()
            }?.sortedWith(compareBy { run -> run.price })
            if (list.isNullOrEmpty()) {
                list =
                    item.runs?.sortedWith(compareBy { run -> run.price })

            }
            courseCardPriceTv.text = "₹ " + list?.first()?.price
            courseCardMrpTv.text = "₹ " + list?.first()?.mrp
            courseCardMrpTv.paintFlags = courseCardPriceTv.paintFlags or
                Paint.STRIKE_THRU_TEXT_FLAG
            ratingTv.text = "${item.rating}/5, ${item.reviewCount} ratings"
            setOnClickListener {
                itemClickListener?.onClick(
                    item.id, item.logo, courseLogo
                )
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.sameAndEqual(newItem)
        }
    }
}

interface ItemClickListener {
    fun onClick(id: String, name: String, logo: ImageView)
}
