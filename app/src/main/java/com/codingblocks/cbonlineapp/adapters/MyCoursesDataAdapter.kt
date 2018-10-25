package com.codingblocks.cbonlineapp.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmadrosid.svgloader.SvgLoader
import com.codingblocks.cbonlineapp.MyCourseActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.MyCourseRuns
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.my_course_card_horizontal.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import java.util.*


class
MyCoursesDataAdapter(private var courseData: ArrayList<MyCourseRuns>?) : RecyclerView.Adapter<MyCoursesDataAdapter.CourseViewHolder>(), AnkoLogger {

    private lateinit var context: Context
//    val ui = MyCourseCardUi()


    fun setData(courseData: ArrayList<MyCourseRuns>) {
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
        context = parent.context

        return CourseViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.my_course_card_horizontal, parent, false))
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(data: MyCourseRuns) {
            itemView.courseTitle.text = data.course?.title
            itemView.courseDescription.text = data.course?.subtitle
            itemView.courseRatingTv.text = data.course?.rating.toString()
            itemView.courseRatingBar.rating = data.course?.rating!!
            itemView.courseRunDescription.text = data.description

//            bind Instructors
            var instructors = ""

            for (i in 0 until data.course!!.instructors?.size!!) {
                if (data.course!!.instructors!!.size == 1) {
                    itemView.courseInstrucImgView2.visibility = View.INVISIBLE
                }
                if (i >= 2) {
                    instructors += "+" + (data.course!!.instructors!!.size - 2) + " more"
                    break
                }
                Clients.onlineV2PublicClient.instructorsById(data.course!!.instructors!![i].id!!).enqueue(retrofitcallback { throwable, response ->
                    response?.body().let {
                        instructors += it?.name + ", "
                        if (i == 0)
                            Picasso.get().load(it?.photo).into(itemView.courseInstrucImgView1)
                        else if (i == 1)
                            Picasso.get().load(it?.photo).into(itemView.courseInstrucImgView2)
                    }
                    itemView.courseInstructors.text = instructors
                })
            }

            SvgLoader.pluck()
                    .with(context as Activity?)
                    .load(data.course?.coverImage, itemView.courseCoverImgView)

            info { data.course?.logo }
            SvgLoader.pluck()
                    .with(context as Activity?)
                    .load(data.course?.logo, itemView.courseLogo)
            itemView.setOnClickListener {
                it.context.startActivity(it.context.intentFor<MyCourseActivity>("courseId" to data.id, "courseName" to data.course!!.title).singleTop())

            }

            Clients.api.getMyCourseProgress("JWT eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6Mzc5NzUsImZpcnN0bmFtZSI6IlB1bGtpdCIsImxhc3RuYW1lIjoiQWdnYXJ3YWwiLCJlbWFpbCI6ImFnZ2Fyd2FscHVsa2l0NTk2QGdtYWlsLmNvbSIsIm1vYmlsZSI6Ijk1ODIwNTQ2NjQiLCJvbmVhdXRoX2lkIjoiMTIwMzUiLCJsYXN0X3JlYWRfbm90aWZpY2F0aW9uIjoiMCIsInBob3RvIjoiaHR0cHM6Ly9ncmFwaC5mYWNlYm9vay5jb20vMTc4MzM4OTczNTAyODQ2MC9waWN0dXJlP3R5cGU9bGFyZ2UiLCJyb2xlSWQiOjIsImNyZWF0ZWRBdCI6IjIwMTgtMDktMjdUMTM6MTA6NTkuMzk2WiIsInVwZGF0ZWRBdCI6IjIwMTgtMTAtMTJUMTA6MDY6NTMuNjE0WiIsImNsaWVudElkIjoiYjI4NDFlNGQtZmY0Yi00OTI5LWJhOGUtMmQxZmM0ZTYwMTFmIiwiaXNUb2tlbkZvckFkbWluIjpmYWxzZSwiaWF0IjoxNTM5NTY1NzE4LCJleHAiOjE1Mzk1NjcyMTh9.U7JmDSg4L_5bBmMBcFSkpQN_t3lYb_himb88eBJqqUBD2e2xS9PGcB6dFTHbiwHj7qhzcOC85x7Lklbi7oWdHrW7fL25LOxg52JT10GnDX41hxamo1fnvvnJ3HI0hx1gvUElaAmia4Kyg1VVgLp7EiH9rphMRV_lhTLz0nF2usz92eGh01P0V9XYqYiiVWH3H_1-vqktHA0yLWHw27taKqruZPdGAWjBnN7aO7lmk3IhfU0fvQkgumFxtS_Jmy_cPL-kJglDq3sEoDUtuOjpt4H25loy_GMufBQeogevpZfWPkcNqYpSzEAqWb5Rh6oMXd84SnAyUkbr4ytqoE4ZhA",
                    data.run_attempts!![0].id!!).enqueue(retrofitcallback { t, progressResponse ->
                progressResponse?.body().let {
                    var progress = it!!["percent"] as Double
                    itemView.courseProgress.progress = (progress).toInt()
                }
            })


        }
    }
}

