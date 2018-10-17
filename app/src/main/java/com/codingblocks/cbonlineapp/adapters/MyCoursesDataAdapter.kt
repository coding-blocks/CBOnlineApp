package com.codingblocks.cbonlineapp.Adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmadrosid.svgloader.SvgLoader
import com.codingblocks.cbonlineapp.ui.MyCourseCardUi
import com.codingblocks.onlineapi.models.MyCourseRuns
import org.jetbrains.anko.AnkoContext
import java.util.*


class MyCoursesDataAdapter(private var courseData: ArrayList<MyCourseRuns>?) : RecyclerView.Adapter<MyCoursesDataAdapter.CourseViewHolder>() {

    private lateinit var context: Context
    val ui = MyCourseCardUi()


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

        return CourseViewHolder(ui.createView(AnkoContext.create(parent.context, parent)))
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(data: MyCourseRuns) {
            ui.courseTitle.text = data.course?.title
            ui.courseDescription.text = data.course?.subtitle
            ui.courseRatingTv.text = data.course?.rating.toString()
            ui.courseRatingBar.rating = data.course?.rating!!

            //bind Instructors
//            var instructors = ""
//
//            for (i in 0 until data.instructors?.size!!) {
//                if (data.instructors!!.size == 1) {
//                    itemView.courseInstrucImgView2.visibility = View.INVISIBLE
//                }
//                if (i >= 2) {
//                    instructors += "+" + (data.instructors!!.size - 2) + " more"
//                    break
//                }
//                instructors += data.instructors!![i].name + ", "
//                if (i == 0)
//                    Picasso.get().load(data.instructors!![i].photo).into(itemView.courseInstrucImgView1)
//                else if (i == 1)
//                    Picasso.get().load(data.instructors!![i].photo).into(itemView.courseInstrucImgView2)
//                else
//                    break
//
//
//            }
//            itemView.courseInstructors.text = instructors
//
//            //bind Runs
//            val currentRuns: ArrayList<Runs> = arrayListOf()
//            for (i in 0 until data.runs!!.size) {
//                if (data.runs!![i].enrollmentStart!!.toLong() < (System.currentTimeMillis() / 1000) && data.runs!![i].enrollmentEnd!!.toLong() > (System.currentTimeMillis() / 1000))
//                    currentRuns.add(data.runs!![i])
//            }
//            currentRuns.sortWith(Comparator { o1, o2 -> java.lang.Long.compare(o2.price!!.toLong(), o1.price!!.toLong()) })
//            itemView.coursePrice.text = "Rs." + currentRuns[0].price
//            var sdf = SimpleDateFormat("MMM dd ")
//            var date = sdf.format(Date(currentRuns[0].start!!.toLong() * 1000))
//            itemView.courseRun.text = "Batches Starting $date"
//            sdf = SimpleDateFormat("dd MMM YYYY")
//            date = sdf.format(Date(currentRuns[0].start!!.toLong() * 1000))
//            itemView.enrollmentTv.text = "Hurry Up! Enrollment ends $date "

            SvgLoader.pluck()
                    .with(context as Activity?)
                    .load(data.course?.coverImage, ui.courseCoverImageView)
            SvgLoader.pluck()
                    .with(context as Activity?)
                    .load(data.course?.logo, ui.courslogo)
//            itemView.setOnClickListener {
//                it.context.startActivity(it.context.intentFor<CourseActivity>("courseId" to data.id, "courseName" to data.?title).singleTop())
//
//            }


        }
    }
}

