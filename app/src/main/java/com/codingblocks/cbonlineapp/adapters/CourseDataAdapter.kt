package com.codingblocks.cbonlineapp.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.ahmadrosid.svgloader.SvgLoader
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.CourseActivity
import com.codingblocks.cbonlineapp.database.Course
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_course_card_horizontal.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import java.text.SimpleDateFormat
import java.util.*


class CourseDataAdapter(private var courseData: ArrayList<Course>?,
                        var context: Context,
                        private val courseWithInstructorDao: CourseWithInstructorDao) : RecyclerView.Adapter<CourseDataAdapter.CourseViewHolder>(), AnkoLogger {

    val svgLoader = SvgLoader.pluck().with(context as Activity?)


    fun setData(courseData: ArrayList<Course>) {
        this.courseData = courseData

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bindView(courseData!![position],courseWithInstructorDao)
    }


    override fun getItemCount(): Int {

        return courseData?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {

        return CourseViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.single_course_card_horizontal, parent, false))  //single_course_card for horizontal cards
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(data: Course, courseWithInstructorDao: CourseWithInstructorDao) {
            itemView.courseTitle.text = data.title
//            itemView.courseDescription.text = data.subtitle
            itemView.courseRatingTv.text = data.rating.toString()
            itemView.courseRatingBar.rating = data.rating

            //bind Instructors
            val instructorsLiveData = courseWithInstructorDao.getInstructorWithCourseId(data.id)

            instructorsLiveData.observe({ (context as LifecycleOwner).lifecycle }, {
                val instructorsList = it

                var instructors = ""

                if (instructorsList.size == 1) {
                    itemView.courseInstrucImgView2.visibility = View.INVISIBLE
                }
                instructorsList.forEachIndexed { i, instructor ->
                    instructors += instructor.name + ", "
                    if (i == 0)
                        Picasso.get().load(instructor.photo).into(itemView.courseInstrucImgView1)
                    else if (i == 1)
                        Picasso.get().load(instructor.photo).into(itemView.courseInstrucImgView2)
                    if (i == 2) {
                        instructors += "+" + (instructorsList.size - 2) + " more"
                        return@forEachIndexed
                    }
                }
                itemView.courseInstructors.text = instructors

            })

//            //bind Runs
            try {
                data.courseRun.run {
                    itemView.coursePrice.text = "₹ $crPrice"
                    if (crPrice != crMrp) {
                        itemView.courseActualPrice.text = "₹ $crMrp"
                        itemView.courseActualPrice.paintFlags = itemView.courseActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                    val sdf = SimpleDateFormat("MMM dd ")
                    var date: String? = ""
                    try {
                        date = sdf.format(Date(crStart.toLong() * 1000))
                    } catch (nfe: NumberFormatException) {
                        nfe.printStackTrace()
                    }
                    itemView.courseRun.text = "Batches Starting $date"
                    itemView.enrollmentTv.text = "Hurry Up! Enrollment ends $date"
                }


                svgLoader
                        .load(data.logo, itemView.courseLogo)
                svgLoader
                        .load(data.coverImage, itemView.courseCoverImgView)

                itemView.setOnClickListener {
                    val textPair: Pair<View, String> = Pair(itemView.courseTitle, "textTrans")
                    val imagePair: Pair<View, String> = Pair(itemView.courseLogo, "imageTrans")

                    //TODO fix transition
//                    val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity, textPair, imagePair)
                    it.context.startActivity(it.context.intentFor<CourseActivity>("courseId" to data.id, "courseName" to data.title, "courseLogo" to data.logo))

                }
            } catch (e: IndexOutOfBoundsException) {
                error {
                    data.promoVideo
                }
            }

        }
    }
}

