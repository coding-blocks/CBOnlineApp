package com.codingblocks.cbonlineapp.Adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmadrosid.svgloader.SvgLoader
import com.codingblocks.cbonlineapp.Attributes
import com.codingblocks.cbonlineapp.CourseModel
import com.codingblocks.cbonlineapp.DataModel
import com.codingblocks.cbonlineapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_course_card.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.text.SimpleDateFormat
import java.util.*


class CourseDataAdapter(private var courseData: ArrayList<DataModel>?) : RecyclerView.Adapter<CourseDataAdapter.CourseViewHolder>(), AnkoLogger {

    private lateinit var course: CourseModel
    private lateinit var context: Context


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
        context = parent.context;

        return CourseViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.single_course_card, parent, false))
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(data: DataModel?) {
            itemView.courseTitle.text = data?.attributes?.title
            itemView.courseDescription.text = data?.attributes?.subtitle
            itemView.courseRatingTv.text = data?.attributes?.rating.toString()
            itemView.courseRatingBar.rating = data?.attributes?.rating!!

            //bind Instructors
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

            //bind Runs
            val currentRuns: ArrayList<Attributes> = arrayListOf()
            val runs: ArrayList<Attributes> = arrayListOf()
            for (i in 0 until data.relationships.runs.data.size) {
                for (j in 0 until course.included.size) {
                    if (course.included[j].type == "runs" && course.included[j].id == data.relationships.runs.data[i].id) {
                        info { "hello" + course.included[j].attributes.enrollmentStart + " " + System.currentTimeMillis().toString() + "    " + course.included[j].attributes.enrollmentEnd }
                        runs.add(course.included[j].attributes)
                        if (course.included[j].attributes.enrollmentStart.toLong() < (System.currentTimeMillis() / 1000) && course.included[j].attributes.enrollmentEnd.toLong() > (System.currentTimeMillis() / 1000)) {
                            info { "inside this loop" }
                            currentRuns.add(course.included[j].attributes)
                        }

                    }
                }

            }
            currentRuns.sortWith(Comparator { o1, o2 -> java.lang.Long.compare(o2.price.toLong(), o1.price.toLong()) })
            itemView.coursePrice.text = "Rs." + currentRuns[0].price
            var sdf = SimpleDateFormat("MMM dd ")
            var date = sdf.format(Date(currentRuns[0].start.toLong() * 1000))
            itemView.courseRun.text = "Batches Starting $date"
            sdf = SimpleDateFormat("dd MMM YYYY")
            date = sdf.format(Date(currentRuns[0].start.toLong() * 1000))
            itemView.enrollmentTv.text = "Hurry Up! Enrollment ends $date "

            SvgLoader.pluck()
                    .with(context as Activity?)
                    .load(data.attributes.coverImage, itemView.courseCoverImgView)
            SvgLoader.pluck()
                    .with(context as Activity?)
                    .load(data.attributes.logo, itemView.courseLogo)


        }
    }
}

