package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.codingblocks.cbonlineapp.R
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.activities.MyCourseActivity
import com.codingblocks.cbonlineapp.AppDatabase
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRun
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.utils.loadSvg
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.my_course_card_horizontal.view.*
import org.jetbrains.anko.*
import kotlin.concurrent.thread

class MyCoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), AnkoLogger {

    private lateinit var courseDao: CourseDao
    private lateinit var database: AppDatabase

    fun bindView(courseRun: CourseRun, instructorDao: CourseWithInstructorDao, context: Context) {

        database = AppDatabase.getInstance(context)
        courseDao = database.courseDao()
        thread {
            doAsync {
                val data = courseDao.getCourse(courseRun.crCourseId)
                uiThread {
                data.run {
                    itemView.courseTitle.text = title
                    itemView.courseDescription.text = subtitle
                    itemView.courseRatingTv.text = rating.toString()
                    itemView.courseRatingBar.rating = rating
                    itemView.courseRunDescription.text = courseRun.crDescription
                    itemView.courseProgress.progress = courseRun.progress.toInt()
                    itemView.courseCoverImgView.loadSvg(coverImage)
                    itemView.courseLogo.loadSvg(logo)
                    if(!courseRun.premium){
                        itemView.trialTv.visibility = View.VISIBLE
                    }else{
                        itemView.trialTv.visibility = View.GONE
                    }
                    if (courseRun.crEnd.toLong() * 1000 > System.currentTimeMillis()) {
                        if(courseRun.progress == 0.0){
                            itemView.courseBtn1.text = "Start"
                        }else{
                            itemView.courseBtn1.text = "Resume"
                        }
                        itemView.courseBtn1.isEnabled = true
                        itemView.courseBtn1.background = context.getDrawable(R.drawable.button_background)
                        itemView.courseBtn1.setOnClickListener {
                            it.context.startActivity(it.context.intentFor<MyCourseActivity>("course_id" to id, "attempt_id" to courseRun.crAttemptId, "courseName" to title).singleTop())
                        }
//                        itemView.setOnClickListener {
//                            it.context.startActivity(it.context.intentFor<MyCourseActivity>("course_id" to id, "attempt_id" to courseRun.crAttemptId, "courseName" to title).singleTop())
//
//                        }
                    } else {
                        itemView.courseBtn1.text = "Expired"
                        itemView.courseBtn1.isEnabled = false
                        itemView.courseBtn1.background = context.getDrawable(R.drawable.button_disable)


                    }
                }

                //bind Instructors
                val instructorsLiveData = instructorDao.getInstructorWithCourseId(data.id)
                    instructorsLiveData.observe({ (context as LifecycleOwner).lifecycle }, {
                        val instructorsList = it

                        var instructors = ""

                            for (i in 0 until instructorsList.size) {
                                if (i == 0) {
                                    Picasso.get().load(instructorsList[i].photo)
                                            .fit().into(itemView.courseInstrucImgView1)
                                    instructors += instructorsList[i].name
                                } else if (i == 1) {
                                    itemView.courseInstrucImgView2.visibility = View.VISIBLE
                                    Picasso.get().load(instructorsList[i].photo)
                                            .fit().into(itemView.courseInstrucImgView2)
                                    instructors += ", ${instructorsList[i].name}"
                                } else if (i >= 2) {
                                    instructors += "+ " + (instructorsList.size - 2) + " more"
                                    break
                                }
                            }
                            itemView.courseInstructors.text = instructors
                    })
                }
            }
        }
    }
}
