package com.codingblocks.cbonlineapp.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmadrosid.svgloader.SvgLoader
import com.codingblocks.cbonlineapp.MyCourseActivity
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.Utils.retrofitcallback
import com.codingblocks.cbonlineapp.prefs
import com.codingblocks.onlineapi.Clients
import com.codingblocks.onlineapi.models.MyCourseRuns
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.my_course_card_horizontal.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop
import java.util.*


class
MyCoursesDataAdapter(private var courseData: ArrayList<MyCourseRuns>?, var context: Context) : RecyclerView.Adapter<MyCoursesDataAdapter.CourseViewHolder>(), AnkoLogger {

    val svgLoader = SvgLoader.pluck().with(context as Activity?)


    fun setData(courseData: ArrayList<MyCourseRuns>) {
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
                .inflate(R.layout.my_course_card_horizontal, parent, false))
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(data: MyCourseRuns) {
            itemView.courseTitle.text = data.course?.title
            itemView.courseDescription.text = data.course?.subtitle
            itemView.courseRatingTv.text = data.course?.rating.toString()
            itemView.courseRatingBar.rating = data.course?.rating!!
            itemView.courseRunDescription.text = data.description

//            bind Instructors
            var instructors = ""

            for (i in 0 until data.course!!.instructors?.size!!) {
                if (data.course!!.instructors!!.size == 1) {
                    itemView.courseInstrucImgView2.visibility = View.INVISIBLE
                }
                if (i >= 2) {
                    instructors += "+" + (data.course!!.instructors!!.size - 2) + " more"
                    break
                }
                Clients.onlineV2PublicClient.instructorsById(data.course!!.instructors!![i].id!!).enqueue(retrofitcallback { throwable, response ->
                    response?.body().let {
                        instructors += it?.name + ", "
                        if (i == 0)
                            Picasso.get().load(it?.photo).into(itemView.courseInstrucImgView1)
                        else if (i == 1)
                            Picasso.get().load(it?.photo).into(itemView.courseInstrucImgView2)
                    }
                    itemView.courseInstructors.text = instructors
                })
            }


            svgLoader.setPlaceHolder(R.drawable.ic_ccaf84b6_63df_40f8_b4df_f64b8b9ecd9e,R.drawable.ic_ccaf84b6_63df_40f8_b4df_f64b8b9ecd9e)
                    .load(data.course?.coverImage, itemView.courseCoverImgView)

            info { data.course?.logo }
            svgLoader
                    .load(data.course?.logo, itemView.courseLogo)
            itemView.courseBtn1.setOnClickListener {
                it.context.startActivity(it.context.intentFor<MyCourseActivity>("run_id" to data.run_attempts!![0].id, "courseName" to data.course!!.title).singleTop())

            }

            Clients.api.getMyCourseProgress("JWT "+ prefs.SP_JWT_TOKEN_KEY,
                    data.run_attempts!![0].id!!).enqueue(retrofitcallback { t, progressResponse ->
                progressResponse?.body().let {
                    var progress = it!!["percent"] as Double
                    itemView.courseProgress.progress = (progress).toInt()
                }
            })


        }
    }
}

