package com.codingblocks.cbonlineapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.CourseModel
import com.codingblocks.cbonlineapp.DataModel
import com.codingblocks.cbonlineapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_course_card.view.*

class CourseDataAdapter(private var courseData: ArrayList<DataModel>?) : RecyclerView.Adapter<CourseDataAdapter.CourseViewHolder>() {

    private lateinit var course: CourseModel

    fun setData(courseData: CourseModel) {
        this.courseData = courseData.data as ArrayList<DataModel>
        this.course = courseData

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
            var instructors = ""
            for (i in 0 until data.relationships.instructors.data.size) {
                if (data.relationships.instructors.data.size == 1) {
                    itemView.courseInstrucImgView2.visibility = View.INVISIBLE
                }
                if (i >= 2) {
                    instructors += "+" + (data.relationships.instructors.data.size - 2) + " more"
                    break
                }
                for (j in 0 until course.included.size) {
                    if (course.included[j].type == "instructors" && course.included[j].id == data.relationships.instructors.data[i].id) {
                        instructors += course.included[j].attributes.name + ", "
                        if (i == 0)
                            Picasso.get().load(course.included[j].attributes.photo).into(itemView.courseInstrucImgView1)
                        else if (i == 1)
                            Picasso.get().load(course.included[j].attributes.photo).into(itemView.courseInstrucImgView2)
                        else
                            break
                    }

                }

            }
            itemView.courseInstructors.text = instructors


        }
    }

}
