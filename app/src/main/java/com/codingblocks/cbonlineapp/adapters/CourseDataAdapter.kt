package com.codingblocks.cbonlineapp.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmadrosid.svgloader.SvgLoader
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.CourseActivity
import com.codingblocks.cbonlineapp.database.Course
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import kotlinx.android.synthetic.main.single_course_card_horizontal.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import java.text.SimpleDateFormat
import java.util.*


class CourseDataAdapter(private var courseData: ArrayList<Course>?, var context: Context) : RecyclerView.Adapter<CourseDataAdapter.CourseViewHolder>(), AnkoLogger {

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
                .inflate(R.layout.single_course_card_horizontal, parent, false))  //single_course_card for horizontal cards
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(data: Course) {
            itemView.courseTitle.text = data.title
//            itemView.courseDescription.text = data.subtitle
            itemView.courseRatingTv.text = data.rating.toString()
            itemView.courseRatingBar.rating = data.rating

            //bind Instructors
            var instructors = ""

//            for (i in 0 until data.instructorList?.size!!) {
//                if (data.instructorList!!.size == 1) {
//                    itemView.courseInstrucImgView2.visibility = View.INVISIBLE
//                }
//                if (i >= 2) {
//                    instructors += "+" + (data.instructorList!!.size - 2) + " more"
//                    break
//                }
//                instructors += data.instructorList!![i].name + ", "
//                if (i == 0)
//                    Picasso.get().load(data.instructorList!![i].photo).into(itemView.courseInstrucImgView1)
//                else if (i == 1)
//                    Picasso.get().load(data.instructorList!![i].photo).into(itemView.courseInstrucImgView2)
//                else
//                    break
//
//
//            }
            itemView.courseInstructors.text = instructors

//            //bind Runs
            try {
                data.courseRun.run {
                    itemView.coursePrice.text = "₹ $crPrice"
                    if (crPrice != crMrp)
                        itemView.courseActualPrice.text = "₹ $crMrp"
                    itemView.courseActualPrice.paintFlags = itemView.courseActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    var sdf = SimpleDateFormat("MMM dd ")
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

