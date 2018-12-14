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
import com.codingblocks.cbonlineapp.database.Course
import kotlinx.android.synthetic.main.my_course_card_horizontal.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop


class MyCoursesDataAdapter(private var courseData: ArrayList<Course>?, var context: Context) : RecyclerView.Adapter<MyCoursesDataAdapter.CourseViewHolder>(), AnkoLogger {

    val svgLoader = SvgLoader.pluck().with(context as Activity?)


    fun setData(courseData: ArrayList<Course>) {
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

        fun bindView(data: Course) {
            itemView.courseTitle.text = data.title
            itemView.courseDescription.text = data.subtitle
            itemView.courseRatingTv.text = data.rating.toString()
            itemView.courseRatingBar.rating = data.rating
            itemView.courseRunDescription.text = data.subtitle
            itemView.courseProgress.progress = data.progress.toInt()
            svgLoader.setPlaceHolder(R.drawable.ic_ccaf84b6_63df_40f8_b4df_f64b8b9ecd9e, R.drawable.ic_ccaf84b6_63df_40f8_b4df_f64b8b9ecd9e)
                    .load(data.coverImage, itemView.courseCoverImgView)

            svgLoader
                    .load(data.logo, itemView.courseLogo)
            itemView.courseBtn1.setOnClickListener {
                it.context.startActivity(it.context.intentFor<MyCourseActivity>("attempt_id" to data.attempt_id, "courseName" to data.title).singleTop())

            }


            //bind Instructors
            var instructors = ""

//            for (i in 0 until data.course!!.instructors?.size!!) {
//                if (data.course!!.instructors!!.size == 1) {
//                    itemView.courseInstrucImgView2.visibility = View.INVISIBLE
//                }
//                if (i >= 2) {
//                    instructors += "+" + (data.course!!.instructors!!.size - 2) + " more"
//                    break
//                }
//                Clients.onlineV2JsonApi.instructorsById(data.course!!.instructors!![i].id!!).enqueue(retrofitCallback { throwable, response ->
//                    response?.body().let {
//                        instructors += it?.name + ", "
//                        if (i == 0)
//                            Picasso.get().load(it?.photo).into(itemView.courseInstrucImgView1)
//                        else if (i == 1)
//                            Picasso.get().load(it?.photo).into(itemView.courseInstrucImgView2)
//                    }
//                    itemView.courseInstructors.text = instructors
//                })
//            }


        }
    }
}

