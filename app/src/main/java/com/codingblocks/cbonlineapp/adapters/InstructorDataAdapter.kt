package com.codingblocks.cbonlineapp.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.Instructor
import com.codingblocks.cbonlineapp.ui.InstructorListUi
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoContext

class InstructorDataAdapter(private var instructorData: ArrayList<Instructor>?) : RecyclerView.Adapter<InstructorDataAdapter.InstructorViewHolder>() {

    val ui = InstructorListUi()

    fun setData(instructorData: ArrayList<Instructor>) {
        this.instructorData = instructorData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructorViewHolder {
        return InstructorViewHolder(ui.createView(AnkoContext.create(parent.context, parent)))
    }

    override fun getItemCount(): Int {
        return instructorData!!.size
    }

    override fun onBindViewHolder(holder: InstructorViewHolder, position: Int) {
        holder.bindView(instructorData!![position])
    }

    inner class InstructorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(instructor: Instructor) {
            ui.instructorTitle.text = instructor.name
            ui.instructorDescription.text = instructor.description
            Picasso.with(itemView.context).load(instructor.photo).placeholder(R.drawable.defaultavatar).fit().into(ui.instructorImgView)
        }
    }
}
