package com.codingblocks.cbonlineapp.course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.Sections
import kotlinx.android.synthetic.main.item_course_section.view.*
import kotlinx.android.synthetic.main.item_section_content_info.view.*

class CourseSectionListAdapter : ListAdapter<Sections, CourseSectionListAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_course_section, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var arrowAnimation: RotateAnimation

        fun bind(item: Sections) = with(itemView) {
            title.text = item.name
            lectures.text = " ${item.contents?.size ?: 0} Items |"
            var duration: Long = 0
            for (subItems in item.contents!!) {
                if (subItems.contentable == "lecture" || subItems.contentable == "video")
                    duration += subItems.duration ?: 0L
            }
            val hour = duration / (1000 * 60 * 60) % 24
            val minute = duration / (1000 * 60) % 60
            if (minute >= 1 && hour == 0L)
                lectureTime.text = ("Duration : $minute Min")
            else if (hour >= 1) {
                lectureTime.text = ("Duration : $hour Hours")
            } else
                lectureTime.text = ("---")

            val ll = findViewById<LinearLayout>(R.id.sectionContents)
            ll.orientation = LinearLayout.VERTICAL
            ll.visibility = View.GONE

            for (i in item.contents!!) {
                val factory = LayoutInflater.from(context)
                val inflatedView = factory.inflate(R.layout.item_section_content_info, ll, false)
                val subTitle = inflatedView.findViewById(R.id.textView15) as TextView
                val contentImg = inflatedView.findViewById(R.id.imageView3) as ImageView
                val premiumImg = inflatedView.premiumImg
                if (i.contentable == "lecture" || i.contentable == "video") {
                    contentImg.setImageDrawable(context.getDrawable(R.drawable.ic_play_lock))
                } else if (i.contentable == "document") {
                    contentImg.setImageDrawable(context.getDrawable(R.drawable.ic_document))
                } else if (i.contentable == "code-challenge") {
                    contentImg.setImageDrawable(context.getDrawable(R.drawable.ic_play_lock))
                }
                if (!item.premium) {
                    subTitle.setTextColor(getColor(context, R.color.orangish))
                    premiumImg.isVisible = false
                } else {
                    subTitle.setTextColor(getColor(context, R.color.black))
                    premiumImg.isVisible = true
                }
                subTitle.text = i.title
                ll.addView(inflatedView)
            }

            setOnClickListener {
                showOrHide(ll, title)
            }

            arrow.setOnClickListener {
                showOrHide(ll, title)
            }
        }

        private fun showOrHide(ll: View, title: TextView) {
            if (ll.visibility == View.GONE) {
                title.setTextColor(ll.context.resources.getColor(R.color.orangish))
                ll.visibility = View.VISIBLE
                arrowAnimation = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f)
                arrowAnimation.fillAfter = true
                arrowAnimation.duration = 350

                itemView.arrow.startAnimation(arrowAnimation)
            } else {
                title.setTextColor(ll.context.resources.getColor(R.color.black))
                ll.visibility = View.GONE
                arrowAnimation = RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f)
                arrowAnimation.fillAfter = true
                arrowAnimation.duration = 350
                itemView.arrow.startAnimation(arrowAnimation)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Sections>() {
        override fun areItemsTheSame(oldItem: Sections, newItem: Sections): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Sections, newItem: Sections): Boolean {
            return oldItem.sameAndEqual(newItem)
        }
    }
}
