package com.codingblocks.cbonlineapp.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.ahmadrosid.svgloader.SvgLoader
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.CourseActivity
import com.codingblocks.onlineapi.models.Course
import com.codingblocks.onlineapi.models.Runs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_course_card_horizontal.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
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
            itemView.courseRatingBar.rating = data.rating!!

            //bind Instructors
            var instructors = ""

            for (i in 0 until data.instructors?.size!!) {
                if (data.instructors!!.size == 1) {
                    itemView.courseInstrucImgView2.visibility = View.INVISIBLE
                }
                if (i >= 2) {
                    instructors += "+" + (data.instructors!!.size - 2) + " more"
                    break
                }
                instructors += data.instructors!![i].name + ", "
                if (i == 0)
                    Picasso.get().load(data.instructors!![i].photo).into(itemView.courseInstrucImgView1)
                else if (i == 1)
                    Picasso.get().load(data.instructors!![i].photo).into(itemView.courseInstrucImgView2)
                else
                    break


            }
            itemView.courseInstructors.text = instructors

            //bind Runs
            // TODO: reduce operations being done on bindView to increase perf
            val currentRuns: ArrayList<Runs> = arrayListOf()
            for (i in 0 until data.runs!!.size) {
                if (data.runs!![i].enrollmentStart!!.toLong() < (System.currentTimeMillis() / 1000) && data.runs!![i].enrollmentEnd!!.toLong() > (System.currentTimeMillis() / 1000))
                    currentRuns.add(data.runs!![i])
            }
            currentRuns.sortWith(Comparator { o1, o2 -> java.lang.Long.compare(o2.price!!.toLong(), o1.price!!.toLong()) })
            try {
                itemView.coursePrice.text = "₹ ${currentRuns[0].price}"
                if (currentRuns[0].price != currentRuns[0].mrp)
                    itemView.courseActualPrice.text = "₹ ${currentRuns[0].mrp}"
                itemView.courseActualPrice.paintFlags = itemView.courseActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                var sdf = SimpleDateFormat("MMM dd ")
                var date = sdf.format(Date(currentRuns[0].start!!.toLong() * 1000))
                itemView.courseRun.text = "Batches Starting $date"
                sdf = SimpleDateFormat("dd MMM YYYY")
                date = sdf.format(Date(currentRuns[0].start!!.toLong() * 1000))
                itemView.enrollmentTv.text = "Hurry Up! Enrollment ends $date"

                svgLoader
                        .load(data.logo, itemView.courseLogo)
                svgLoader
                        .load(data.coverImage, itemView.courseCoverImgView)

                itemView.setOnClickListener {
                    val textPair: Pair<View, String> = Pair(itemView.courseTitle, "textTrans")
                    val imagePair: Pair<View, String> = Pair(itemView.courseLogo, "imageTrans")
                    val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity, textPair, imagePair)
                    it.context.startActivity(it.context.intentFor<CourseActivity>("courseId" to data.id, "courseName" to data.title, "courseLogo" to data.logo), compat.toBundle())

                }
            } catch (e: IndexOutOfBoundsException) {
                error {
                    data.promoVideo
                }
            }

        }
    }
}

