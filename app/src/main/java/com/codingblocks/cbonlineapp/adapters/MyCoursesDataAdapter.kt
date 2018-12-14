package com.codingblocks.cbonlineapp.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmadrosid.svgloader.SvgLoader
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.MyCourseActivity
import com.codingblocks.cbonlineapp.database.CourseWithInstructor
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.my_course_card_horizontal.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop


class MyCoursesDataAdapter(private var courseData: ArrayList<CourseWithInstructor>?, var context: Context) : RecyclerView.Adapter<MyCoursesDataAdapter.CourseViewHolder>(), AnkoLogger {

    val svgLoader = SvgLoader.pluck().with(context as Activity?)!!


    fun setData(courseData: ArrayList<CourseWithInstructor>) {
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
                .inflate(R.layout.my_course_card_horizontal, parent, false))
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(data: CourseWithInstructor) {
            data.course?.run {
                itemView.courseTitle.text = title
                itemView.courseDescription.text = subtitle
                itemView.courseRatingTv.text = rating.toString()
                itemView.courseRatingBar.rating = rating
                itemView.courseRunDescription.text = runDescription
                itemView.courseProgress.progress = progress.toInt()
                svgLoader.setPlaceHolder(R.drawable.ic_ccaf84b6_63df_40f8_b4df_f64b8b9ecd9e, R.drawable.ic_ccaf84b6_63df_40f8_b4df_f64b8b9ecd9e)
                        .load(coverImage, itemView.courseCoverImgView)

                svgLoader
                        .load(logo, itemView.courseLogo)
                itemView.courseBtn1.setOnClickListener {
                    it.context.startActivity(it.context.intentFor<MyCourseActivity>("attempt_id" to attempt_id, "courseName" to title).singleTop())

                }
            }
            //bind Instructors
            var instructors = ""

            if (data.instructorList!!.size == 1) {
                itemView.courseInstrucImgView2.visibility = View.INVISIBLE
            }
            for (i in 0 until data.instructorList!!.size) {
                instructors += data.instructorList!![i].name + ", "
                if (i == 0)
                    Picasso.get().load(data.instructorList!![i].photo).into(itemView.courseInstrucImgView1)
                else if (i == 1)
                    Picasso.get().load(data.instructorList!![i].photo).into(itemView.courseInstrucImgView2)
            }
            itemView.courseInstructors.text = instructors
            if (data.instructorList!!.size > 2) {
                instructors += "+" + (data.instructorList!!.size - 2) + " more"
            }
        }
    }
}


