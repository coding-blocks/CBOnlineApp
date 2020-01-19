package com.codingblocks.cbonlineapp.course.insturctors

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.InstructorModel
import com.squareup.picasso.Picasso
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.email

class InstructorDataAdapter(private var instructorData: ArrayList<InstructorModel>) : RecyclerView.Adapter<InstructorDataAdapter.InstructorViewHolder>() {

    val ui = InstructorListUi()

    fun setData(instructorData: ArrayList<InstructorModel>) {
        this.instructorData = instructorData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstructorViewHolder {
        return InstructorViewHolder(ui.createView(AnkoContext.create(parent.context, parent)))
    }

    override fun getItemCount(): Int {
        return instructorData.size
    }

    override fun onBindViewHolder(holder: InstructorViewHolder, position: Int) {
        holder.bindView(instructorData[position])
    }

    inner class InstructorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(instructor: InstructorModel) {
            ui.instructorTitle.text = instructor.name
            ui.instructorDescription.text = instructor.description
            ui.instructorTextView.text = "${instructor.sub}, Coding Blocks"
            ui.instructorEmail.text = instructor.email
            Picasso.get().load(instructor.photo).placeholder(R.drawable.defaultavatar).fit().into(ui.instructorImgView)

            ui.instructorEmail.setOnClickListener {
                if (instructor.email?.isNotEmpty()!!) {
                    sendEmail(instructor.email!!)
                }
            }
        }

        private fun sendEmail(emailAddress: String) {
            itemView.context.email(emailAddress)
        }
    }
}
