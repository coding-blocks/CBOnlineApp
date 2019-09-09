package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.activities.CourseActivity
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.database.models.CourseRun
import com.codingblocks.cbonlineapp.extensions.loadImage
import com.codingblocks.cbonlineapp.ui.MyCourseCardUi
import com.squareup.picasso.Picasso
import org.jetbrains.anko.intentFor
import java.text.SimpleDateFormat
import java.util.Date

class AllCoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(courseRun: CourseRun, ui: MyCourseCardUi, courseDao: CourseDao, courseWithInstructorDao: CourseWithInstructorDao, context: Context) {

        val data = courseDao.getCourses().value!!.get(0)
        ui.courseTitle.text = data.title
        ui.courseRatingBar.rating = data.rating
        ui.courseRatingTv.text = data.rating.toString()
        data.subtitle
        ui.courseCoverImageView.loadImage(data.coverImage)
        ui.courselogo.loadImage(data.logo)
        val instructorsList = courseWithInstructorDao.getInstructorWithCourseIdNonLive(data.cid)
        var instructors = ""
        for (i in 0 until instructorsList.size) {
            if (i == 0) {
                Picasso.get().load(instructorsList[i].photo)
                    .fit().into(ui.courseInstrucImgView1)
                instructors += instructorsList[i].name
            } else if (i == 1) {
                ui.courseInstrucImgView2.visibility = View.VISIBLE
                Picasso.get().load(instructorsList[i].photo)
                    .fit().into(ui.courseInstrucImgView2)
                instructors += ", ${instructorsList[i].name}"
            } else if (i >= 2) {
                instructors += "+ " + (instructorsList.size - 2) + " more"
                break
            }
        }
        if (instructorsList.size < 2) {
            ui.courseInstrucImgView2.visibility = View.GONE
        }
        ui.courseInstructors.text = instructors

//                    //bind Runs
        courseRun.run {
            ui.coursePrice.text = "₹ $crPrice"
            if (crPrice != crMrp && crMrp != "") {
                ui.courseMrp.text = "₹ $crMrp"
                ui.courseMrp.paintFlags = ui.courseMrp.paintFlags or
                    Paint.STRIKE_THRU_TEXT_FLAG
            }
            val sdf = SimpleDateFormat("MMM dd ")
            var startDate: String? = ""
            var endDate: String? = ""
            try {
                startDate = sdf.format(Date(crStart.toLong() * 1000))
                endDate = sdf.format(Date(crEnrollmentEnd.toLong() * 1000))
            } catch (nfe: NumberFormatException) {
                nfe.printStackTrace()
            }
            ui.courseRun.text = "Batches Starting $startDate"
            ui.enrollment.text = "Hurry Up! Enrollment ends $endDate"
        }

        itemView.setOnClickListener {
            it.context.startActivity(
                it.context.intentFor<CourseActivity>(
                    "courseId" to data.cid,
                    "courseName" to data.title,
                    "courseLogo" to data.logo
                )
            )
        }
    }
}
