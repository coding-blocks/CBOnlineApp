package com.codingblocks.cbonlineapp.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.database.models.CourseInstructorHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.my_course_card_horizontal.view.*
import org.jetbrains.anko.AnkoLogger

class MyCoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), AnkoLogger {

    fun bindView(courseRun: CourseInstructorHolder.CourseAndItsInstructor) {
//
//        thread {
//            doAsync {
//                val data = courseDao.getCourses().value!!.get(0)
//                uiThread {
//        data.run {
        //                        itemView.courseTitle.text = title
//                        itemView.courseDescription.text = subtitle
//                        itemView.courseRatingTv.text = rating.toString()
//                        itemView.courseRatingBar.rating = rating
//                        itemView.courseRunDescription.text = courseRun.crDescription
//                        itemView.courseProgress.progress = courseRun.progress.toInt()
//                        itemView.courseCoverImgView.loadImage(coverImage)
//                        itemView.courseLogo.loadImage(logo)
//                        if (!courseRun.premium) {
//                            itemView.trialTv.visibility = View.VISIBLE
//                        } else {
//                            itemView.trialTv.visibility = View.GONE
//                        }
//                        if (courseRun.crRunEnd.toLong() * 1000 > System.currentTimeMillis()) {
//                            if (courseRun.progress == 0.0) {
//                                itemView.courseBtn1.text = "Start"
//                            } else {
//                                itemView.courseBtn1.text = "Resume"
//                            }
//                        } else {
//                            itemView.courseBtn1.text = "Expired"
////                            itemView.courseBtn1.isEnabled = false
////                            itemView.courseBtn1.background =
////                                context.getDrawable(R.drawable.button_disable)
//                        }
//                        itemView.courseBtn1.isEnabled = true
//                        itemView.courseBtn1.background =
//                            context.getDrawable(R.drawable.button_background)
//                        itemView.courseBtn1.setOnClickListener {
//                            it.context.startActivity(
//                                it.context.intentFor<MyCourseActivity>(
//                                    COURSE_ID to cid,
//                                    RUN_ATTEMPT_ID to courseRun.crAttemptId,
//                                    COURSE_NAME to title,
//                                    RUN_ID to courseRun.crUid
//                                ).singleTop()
//                            )
//                        }
//                    }
//

        /*
        Bind @InstructorModel
         */
        var instructors = ""
        with(courseRun.instructors) {
            for (i in this.indices) {
                if (i == 0) {
                    Picasso.get().load(this[i].photo)
                        .fit().into(itemView.courseInstrucImgView1)
                    instructors += this[i].name
                } else if (i == 1) {
                    itemView.courseInstrucImgView2.visibility = View.VISIBLE
                    Picasso.get().load(this[i].photo)
                        .fit().into(itemView.courseInstrucImgView2)
                    instructors += ", ${this[i].name}"
                } else if (i >= 2) {
                    instructors += "+ " + (this.size - 2) + " more"
                    break
                }
            }
            itemView.courseInstructors.text = instructors
        }
    }
}

