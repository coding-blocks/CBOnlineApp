package com.codingblocks.cbonlineapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.DataModel
import com.codingblocks.cbonlineapp.R
import kotlinx.android.synthetic.main.single_course_card.view.*

class CourseDataAdapter(private var courseData: ArrayList<DataModel>?) : RecyclerView.Adapter<CourseDataAdapter.CourseViewHolder>() {


    fun setData(courseData: ArrayList<DataModel>) {
        this.courseData = courseData
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bindView(courseData!![position])
    }


    override fun getItemCount(): Int {

        return courseData?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        return CourseViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.single_course_card, parent, false))
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(data: DataModel?) {
            itemView.courseTitle.text = data?.attributes?.title
            itemView.courseDescription.text = data?.attributes?.subtitle
            itemView.courseRatingTv.text = data?.attributes?.rating.toString()
            itemView.courseRatingBar.rating = data?.attributes?.rating!!


        }
    }

}
