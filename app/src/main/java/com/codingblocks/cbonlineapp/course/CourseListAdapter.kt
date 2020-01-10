package com.codingblocks.cbonlineapp.course

import android.graphics.Paint
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.CustomTypefaceSpan
import com.codingblocks.cbonlineapp.util.extensions.greater
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.Course
import kotlinx.android.synthetic.main.item_course_card.view.*

class CourseListAdapter(val type: String = "") : ListAdapter<Course, CourseListAdapter.ItemViewHolder>(DiffCallback()) {

    var onItemClick: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            when (type) {
                "POPULAR" -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_course_card_secondary, parent, false)
                "LIST" -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_course_card_list, parent, false)
                else -> LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_course_card, parent, false)
            }
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemClickListener = onItemClick
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemClickListener: ItemClickListener? = null

        fun bind(item: Course) = with(itemView) {
            courseLogo.loadImage(item.logo)
            ViewCompat.setTransitionName(courseLogo, item.title)

            chip.text = when (item.difficulty) {
                "0" -> "Beginner"
                "1" -> "Advanced"
                "2" -> "Expert"
                else -> "Beginner"
            }
            courseCardTitleTv.text = item.title
            val ratingText = "${item.rating}/5, ${item.reviewCount} ratings"
            if (type != "LIST") {
                courseCover.loadImage(item.coverImage)
                ratingTv.text = ratingText
            } else {
                val font = Typeface.createFromAsset(context.assets, "fonts/gilroy_bold.ttf")
                val font2 = Typeface.createFromAsset(context.assets, "fonts/gilroy_medium.ttf")
                val ss = SpannableStringBuilder(ratingText)
                ss.setSpan(CustomTypefaceSpan("", font, context.getColor(R.color.black)), 0, 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                ss.setSpan(CustomTypefaceSpan("", font2, context.getColor(R.color.brownish_grey)), 3, ratingText.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                ratingTv.text = ss
            }
            ratingTv.text = ratingText
            setOnClickListener {
                itemClickListener?.onClick(
                    item.id, item.logo, courseLogo
                )
            }
            if (type != "POPULAR") {
                if (!item.instructors.isNullOrEmpty()) {
                    courseCardInstructorsTv.text = "${item.instructors?.first()?.name}"
                    if (type != "LIST") {
                        item.instructors?.first()?.photo?.let { courseCardInstructorImg1.loadImage(it) }
                        if (item.instructors!!.size > 1) {
                            courseCardInstructorsTv.append(", ${item.instructors!![1].name}")
                            item.instructors!![1].photo?.let { courseCardInstructorImg2.loadImage(it) }
                        } else {
                            courseCardInstructorImg2.visibility = View.INVISIBLE
                        }
                    }
                }
                var list = item.runs?.filter { run ->
                    !run.unlisted && run.enrollmentEnd?.greater()!! && !run.enrollmentStart!!.greater()
                }?.sortedWith(compareBy { run -> run.price })
                if (list.isNullOrEmpty()) {
                    list =
                        item.runs?.sortedWith(compareBy { run -> run.price })
                }
                courseCardPriceTv.text = "₹ " + list?.first()?.price
                courseCardMrpTv.text = "₹ " + list?.first()?.mrp
                courseCardMrpTv.paintFlags = courseCardPriceTv.paintFlags or
                    Paint.STRIKE_THRU_TEXT_FLAG
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
