package com.codingblocks.cbonlineapp.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.database.models.CourseInstructorHolder
import com.codingblocks.cbonlineapp.extensions.loadImage
import com.codingblocks.cbonlineapp.ui.MyCourseCardUi
import com.squareup.picasso.Picasso

class AllCoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(model: CourseInstructorHolder.CourseAndItsInstructor, ui: MyCourseCardUi) {


        with(model.courseRun.course) {
            ui.courseTitle.text = title
            ui.courseRatingTv.text = rating.toString()
            ui.courseRatingBar.rating = rating
            ui.courseCoverImageView.loadImage(coverImage)
            ui.courselogo.loadImage(logo)
        }

        var instructors = ""
        with(model.instructors) {
            if (this.size < 2)
                ui.courseInstrucImgView2.visibility = View.INVISIBLE
            for (i in indices) {
                if (i == 0) {
                    Picasso.get().load(this[i].photo)
                        .fit().into(ui.courseInstrucImgView1)
                    instructors += this[i].name
                } else if (i == 1) {
                    Picasso.get().load(this[i].photo)
                        .fit().into(ui.courseInstrucImgView2)
                    instructors += ", ${this[i].name}"
                } else if (i >= 2) {
                    instructors += " + " + (this.size - 2) + " more"
                    break
                }
            }
            ui.courseInstructors.text = instructors
        }
//        if (instructorsList.size < 2) {
//            ui.courseInstrucImgView2.visibility = View.GONE
//        }
//        ui.courseInstructors.text = instructors
//
////                    //bind Runs
//        courseRun.run {
//            ui.coursePrice.text = "₹ $crPrice"
//            if (crPrice != crMrp && crMrp != "") {
//                ui.courseMrp.text = "₹ $crMrp"
//                ui.courseMrp.paintFlags = ui.courseMrp.paintFlags or
//                    Paint.STRIKE_THRU_TEXT_FLAG
//            }
//            val sdf = SimpleDateFormat("MMM dd ")
//            var startDate: String? = ""
//            var endDate: String? = ""
//            try {
//                startDate = sdf.format(Date(crStart.toLong() * 1000))
//                endDate = sdf.format(Date(crEnrollmentEnd.toLong() * 1000))
//            } catch (nfe: NumberFormatException) {
//                nfe.printStackTrace()
//            }
//            ui.courseRun.text = "Batches Starting $startDate"
//            ui.enrollment.text = "Hurry Up! Enrollment ends $endDate"
//        }
//
//        itemView.setOnClickListener {
//            it.context.startActivity(
//                it.context.intentFor<CourseActivity>(
//                    "courseId" to data.cid,
//                    "courseName" to data.title,
//                    "courseLogo" to data.logo
//                )
//            )
//        }
    }
}
