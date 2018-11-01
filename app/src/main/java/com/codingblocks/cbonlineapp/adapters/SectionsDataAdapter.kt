package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.onlineapi.models.Sections
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.*


class SectionsDataAdapter(private var sectionData: ArrayList<Sections>?) : RecyclerView.Adapter<SectionsDataAdapter.CourseViewHolder>(), AnkoLogger {

    private lateinit var context: Context


    fun setData(sectionData: ArrayList<Sections>) {
        this.sectionData = sectionData
        info { sectionData.size }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bindView(sectionData!![position])
    }


    override fun getItemCount(): Int {

        return sectionData!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        context = parent.context

        return CourseViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_section, parent, false))
    }

    inner class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(data: Sections) {

//            itemView.sections.title.text = data.name
//            itemView.sections.setDescription1("${data.contents?.size} Lectures")
//            var duration: Long = 0
//            for (subitems in data.contents!!) {
//                duration += subitems.duration!!
//            }
//            val hour = duration / (1000 * 60 * 60) % 24
//            val minute = duration / (1000 * 60) % 60
//            if (hour <= 0)
//                itemView.sections.setDescription2("$minute Mins")
//            else if (minute <= 0)
//                itemView.sections.setDescription2("$hour Hours")
//            else
//                itemView.sections.setDescription2("---")
//            val myView = factory.inflate(R.layout.item_section_content_info, null)
//            myView.textView15.text = "Pulkit"
//            val params = LinearLayout.LayoutParams(
//                    FrameLayout.LayoutParams.MATCH_PARENT,
//                    FrameLayout.LayoutParams.WRAP_CONTENT)
//
//            val myRoot = itemView.findViewById<LinearLayout>(R.id.sectionRoot)
//            val a = LinearLayout(context)
//            a.orientation = LinearLayout.HORIZONTAL
//
//            val card: ExpandableCardView = itemView.findViewById(R.id.sections)
//            for (i in data.contents!!) {
//                myView.textView15.text = "Pulkit"
//                a.addView(myView)
//            }
//            myRoot.addView(a)

            val ll = itemView.findViewById<LinearLayout>(R.id.sectionRoot)
            ll.orientation = LinearLayout.VERTICAL

            for (i in data.contents!!) {
                val factory = LayoutInflater.from(context)
                val inflatedView = factory.inflate(R.layout.item_section_content_info, ll, false)
                val submittedByTextView = inflatedView.findViewById(R.id.textView15) as TextView
                submittedByTextView.text = i.title
                ll.addView(inflatedView)

            }

//            itemView.sections.setOnExpandedListener { v, isExpanded ->
//                if (isExpanded) {
//                    val view = v.findViewById<LinearLayout>(R.id.rootView)
//                    info {
//                        "view" +
//                                view.toString()
//                    }
//
//                }
//            }
//            for ( i in data.contents!!){
//                myView.textView15.text = i.title
//                val params = RelativeLayout.LayoutParams(
//                        FrameLayout.LayoutParams.MATCH_PARENT,
//                        FrameLayout.LayoutParams.WRAP_CONTENT)
//                itemView.sections.addView(myView, params)
//            }


        }
    }
}