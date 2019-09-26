package com.codingblocks.cbonlineapp.home.mycourses

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.CourseInstructorHolder
import com.codingblocks.cbonlineapp.mycourse.MyCourseActivity
import com.codingblocks.cbonlineapp.util.COURSE_ID
import com.codingblocks.cbonlineapp.util.COURSE_NAME
import com.codingblocks.cbonlineapp.util.RUN_ATTEMPT_ID
import com.codingblocks.cbonlineapp.util.RUN_ID
import com.codingblocks.cbonlineapp.util.extensions.loadImage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.my_course_card_horizontal.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop

class MyCoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), AnkoLogger {

    fun bindView(model: CourseInstructorHolder.CourseAndItsInstructor) {

        with(model.courseRun.course) {
            itemView.courseTitle.text = title
            itemView.courseDescription.text = subtitle
            itemView.courseRatingTv.text = rating.toString()
            itemView.courseRatingBar.rating = rating
            itemView.courseCoverImgView.loadImage(coverImage)
            itemView.courseLogo.loadImage(logo)
        }
        with(model.courseRun) {
            itemView.courseRunDescription.text = crDescription
            itemView.courseProgress.progress = progress.toInt()
            itemView.trialTv.isVisible = !premium
            if (crRunEnd.toLong() * 1000 > System.currentTimeMillis()) {
                if (progress == 0.0) {
                    itemView.courseBtn1.text = "Start"
                } else {
                    itemView.courseBtn1.text = "Resume"
                }
            } else {
                itemView.courseBtn1.text = "Expired"
                itemView.courseBtn1.background = itemView.context.getDrawable(R.drawable.button_disable)
            }

            itemView.courseBtn1.setOnClickListener {
                it.context.startActivity(
                    it.context.intentFor<MyCourseActivity>(
                        COURSE_ID to model.courseRun.course.cid,
                        RUN_ATTEMPT_ID to model.courseRun.crAttemptId,
                        COURSE_NAME to model.courseRun.course.title,
                        RUN_ID to model.courseRun.crUid
                    ).singleTop()
                )
            }
            /*
            Bind @InstructorModel
             */
            var instructors = ""
            with(model.instructors) {
                if (this.size < 2)
                    itemView.courseInstrucImgView2.visibility = View.INVISIBLE
                for (i in indices) {
                    if (i == 0) {
                        Picasso.get().load(this[i].photo)
                            .fit().into(itemView.courseInstrucImgView1)
                        instructors += this[i].name
                    } else if (i == 1) {
                        Picasso.get().load(this[i].photo)
                            .fit().into(itemView.courseInstrucImgView2)
                        instructors += ", ${this[i].name}"
                    } else if (i >= 2) {
                        instructors += " + " + (this.size - 2) + " more"
                        break
                    }
                }
                itemView.courseInstructors.text = instructors
            }
        }
    }
}
