package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.graphics.Paint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.activities.CourseActivity
import com.codingblocks.cbonlineapp.database.*
import com.codingblocks.cbonlineapp.utils.loadSvg
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_course_card_horizontal.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class AllCoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var instructorsList: List<Instructor>? = null


    private lateinit var courseDao: CourseDao
    private lateinit var database: AppDatabase

    fun bindView(courseRun: CourseRun, courseWithInstructorDao: CourseWithInstructorDao, context: Context) {

        database = AppDatabase.getInstance(context)
        courseDao = database.courseDao()
        thread {
            val data = courseDao.getCourse(courseRun.crCourseId)
            itemView.courseTitle.text = data.title
//            itemView.courseDescription.text = data.subtitle
            itemView.courseRatingTv.text = data.rating.toString()
            itemView.courseRatingBar.rating = data.rating

            //bind Instructors
//        val instructorsLiveData = courseWithInstructorDao.getInstructorWithCourseId(data.id)

            doAsync {
                if (instructorsList == null) {
                    instructorsList = courseWithInstructorDao.getInstructorWithCourseIdNonLive(data.id)
                }

                uiThread {
                    var instructors = ""
                    for (i in 0 until instructorsList!!.size) {
                        if (i == 0) {
                            Picasso.get().load(instructorsList!![i].photo)
                                    .fit().into(itemView.courseInstrucImgView1)
                            instructors += instructorsList!![i].name
                        } else if (i == 1) {
                            itemView.courseInstrucImgView2.visibility = View.VISIBLE
                            Picasso.get().load(instructorsList!![i].photo)
                                    .fit().into(itemView.courseInstrucImgView2)
                            instructors += ", ${instructorsList!![i].name}"
                        } else if (i >= 2) {
                            instructors += "+ " + (instructorsList!!.size - 2) + " more"
                            break
                        }
                    }
                    if (instructorsList!!.size < 2) {
                        itemView.courseInstrucImgView2.visibility = View.INVISIBLE
                    }
                    itemView.courseInstructors.text = instructors
                }


            }

//            //bind Runs
            try {
                courseRun.run {
                    itemView.coursePrice.text = "₹ $crPrice"
                    if (crPrice != crMrp && crMrp != "") {
                        itemView.courseActualPrice.text = "₹ $crMrp"
                        itemView.courseActualPrice.paintFlags = itemView.courseActualPrice.paintFlags or
                                Paint.STRIKE_THRU_TEXT_FLAG
                    }
                    val sdf = SimpleDateFormat("MMM dd ")
                    var startDate: String? = ""
                    var endDate: String? = ""
                    try {
                        startDate = sdf.format(Date(crStart.toLong() * 1000))
                        endDate = sdf.format(Date(crEnd.toLong() * 1000))
                    } catch (nfe: NumberFormatException) {
                        nfe.printStackTrace()
                    }
                    itemView.courseRun.text = "Batches Starting $startDate"
                    itemView.enrollmentTv.text = "Hurry Up! Enrollment ends $endDate"
                }

                // TODO: Prefer to cache the created drawables
                itemView.courseCoverImgView.loadSvg(data.coverImage)
                itemView.courseLogo.loadSvg(data.logo)

                itemView.setOnClickListener {
                    val textPair: Pair<View, String> = Pair(itemView.courseTitle, "textTrans")
                    val imagePair: Pair<View, String> = Pair(itemView.courseLogo, "imageTrans")

                    //TODO fix transition
//                    val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity, textPair, imagePair)
                    it.context.startActivity(
                            it.context.intentFor<CourseActivity>(
                                    "courseId" to data.id,
                                    "courseName" to data.title,
                                    "courseLogo" to data.logo
                            )
                    )

                }
            } catch (e: Exception) {
                error {
                    data.promoVideo
                }
            }
        }

    }
}