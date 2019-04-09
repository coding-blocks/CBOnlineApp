package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.CourseActivity
import com.codingblocks.cbonlineapp.AppDatabase
import com.codingblocks.cbonlineapp.database.CourseDao
import com.codingblocks.cbonlineapp.database.CourseRun
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.ui.MyCourseCardUi
import com.codingblocks.cbonlineapp.utils.loadSvg
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.intentFor
import java.text.SimpleDateFormat
import java.util.*


class CourseDataAdapter(private var courseData: ArrayList<CourseRun>?,
                        val context: Context,
                        private val courseWithInstructorDao: CourseWithInstructorDao, var type: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), AnkoLogger {

    val ui = MyCourseCardUi()

    fun setData(courseData: ArrayList<CourseRun>) {
        this.courseData = courseData

        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {

        return courseData?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var viewHolder: RecyclerView.ViewHolder? = null
        when (type) {
            "myCourses" -> {
                val myCoursesView = LayoutInflater.from(parent.context).inflate(R.layout.my_course_card_horizontal, parent, false)
                viewHolder = MyCoursesViewHolder(myCoursesView) // view holder for normal items
            }
            "allCourses" -> {
                val allCoursesView = ui.createView(AnkoContext.create(parent.context, parent))
                viewHolder = AllCoursesViewHolder(allCoursesView) // view holder for normal items
            }
        }

        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (type) {
            "myCourses" -> {
                val myCoursesViewHolder = holder as MyCoursesViewHolder
                myCoursesViewHolder.bindView(courseData!![position], courseWithInstructorDao, context)
            }
            "allCourses" -> {
                val allCoursesViewHolder = holder as AllCoursesViewHolder
                allCoursesViewHolder.bindView(courseData!![position], courseWithInstructorDao, context)
            }
        }
    }

    inner class AllCoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var courseDao: CourseDao
        private lateinit var database: AppDatabase

        fun bindView(courseRun: CourseRun, courseWithInstructorDao: CourseWithInstructorDao, context: Context) {

            database = AppDatabase.getInstance(context)
            courseDao = database.courseDao()
            val data = courseDao.getCourse(courseRun.crCourseId)
            ui.courseTitle.text = data.title
            data.subtitle
            if (data.coverImage.takeLast(3) == "png") {
                Picasso.get().load(data.coverImage)
                        .fit().into(ui.courseCoverImageView)
                Picasso.get().load(data.logo)
                        .fit().into(ui.courselogo)
            } else {
                ui.courseCoverImageView.loadSvg(data.coverImage)
                ui.courselogo.loadSvg(data.logo)
            }
            val instructorsList = courseWithInstructorDao.getInstructorWithCourseIdNonLive(data.id)
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
                //                            val textPair: Pair<View, String> = Pair(itemView.courseTitle, "textTrans")
//                            val imagePair: Pair<View, String> = Pair(itemView.courseLogo, "imageTrans")
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


        }
    }
}

