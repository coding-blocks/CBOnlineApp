package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.models.ContentsId
import com.codingblocks.onlineapi.models.Sections
import kotlinx.android.synthetic.main.item_section.view.*

class SectionsDataAdapter(private var sectionData: ArrayList<Sections>?) : RecyclerView.Adapter<SectionsDataAdapter.CourseViewHolder>() {

    private lateinit var context: Context
    private lateinit var arrowAnimation: RotateAnimation

    fun setData(sectionData: ArrayList<Sections>) {
        this.sectionData = sectionData
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        sectionData?.get(position)?.let { holder.bindView(it) }
    }

    override fun getItemCount(): Int {

        return sectionData?.size ?: 0
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        context = parent.context

        return CourseViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_section, parent, false))
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(data: Sections) {

            itemView.title.text = data.name
            itemView.lectures.text = ("${data.contents?.size} Lectures")
            var duration: Long = 0
            for (subItems in data.contents ?: listOf<ContentsId>()) {
                if (subItems.contentable == "lecture" || subItems.contentable == "video")
                    duration += subItems.duration ?: 0L
            }
            val hour = duration / (1000 * 60 * 60) % 24
            val minute = duration / (1000 * 60) % 60
            if (minute >= 1 && hour == 0L)
                itemView.lectureTime.text = ("$minute Min")
            else if (hour >= 1) {
                itemView.lectureTime.text = ("$hour Hours")
            } else
                itemView.lectureTime.text = ("---")

            val ll = itemView.findViewById<LinearLayout>(R.id.sectionContents)
            ll.orientation = LinearLayout.VERTICAL
            ll.visibility = View.GONE

            for (i in data.contents!!) {
                val factory = LayoutInflater.from(context)
                val inflatedView = factory.inflate(R.layout.item_section_content_info, ll, false)
                val subTitle = inflatedView.findViewById(R.id.textView15) as TextView
                val subDuration = inflatedView.findViewById(R.id.textView16) as TextView
                val contentImg = inflatedView.findViewById(R.id.imageView3) as ImageView
                if (i.contentable == "lecture" || i.contentable == "video") {
                    val contentDuration: Long = i.duration!!
                    contentImg.setImageDrawable(context.getDrawable(R.drawable.ic_lecture))
                    val contentHour = contentDuration / (1000 * 60 * 60) % 24
                    val contentMinute = contentDuration / (1000 * 60) % 60
                    when {
                        contentHour <= 0 -> subDuration.text = ("$contentMinute Mins")
                        contentMinute <= 0 -> subDuration.text = ("$contentHour Hours")
                        else -> itemView.lectureTime.text = ("---")
                    }
                } else if (i.contentable == "document") {
                    contentImg.setImageDrawable(context.getDrawable(R.drawable.ic_document))
                } else if (i.contentable == "code-challenge") {
                    contentImg.setImageDrawable(context.getDrawable(R.drawable.ic_lecture))
                }
                subTitle.text = i.title

                ll.addView(inflatedView)
            }

            itemView.setOnClickListener {
                showOrHide(ll, it)
            }

//            itemView.arrow.setOnClickListener {
//                showOrHide(ll, itemView)
//            }
        }

        private fun showOrHide(ll: View, itemView: View) {
            if (ll.visibility == View.GONE) {
                ll.visibility = View.VISIBLE
                arrowAnimation = RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f)
                arrowAnimation.fillAfter = true
                arrowAnimation.duration = 350

                itemView.arrow.startAnimation(arrowAnimation)
            } else {
                ll.visibility = View.GONE
                arrowAnimation = RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f)
                arrowAnimation.fillAfter = true
                arrowAnimation.duration = 350
                itemView.arrow.startAnimation(arrowAnimation)
            }
        }
    }
}
