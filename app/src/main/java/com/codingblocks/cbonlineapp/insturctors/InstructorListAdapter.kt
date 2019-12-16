package com.codingblocks.cbonlineapp.insturctors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.database.models.InstructorModel
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_instructor.view.*
import org.jetbrains.anko.email

class InstructorListAdapter : ListAdapter<InstructorModel, InstructorListAdapter.ItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_instructor, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(instructor: InstructorModel) = with(itemView) {
            instructorNameTv.text = instructor.name
            instructorDescTv.text = instructor.description
//            instructorTextView.text = "${instructor.sub}, Coding Blocks"
            instructorEmailTv.text = instructor.email
            Picasso.get().load(instructor.photo).placeholder(R.drawable.defaultavatar).fit().into(instructorImg)
            setOnClickListener {
                itemView.context.email(instructor.email.toString())
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<InstructorModel>() {
        override fun areItemsTheSame(oldItem: InstructorModel, newItem: InstructorModel): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: InstructorModel, newItem: InstructorModel): Boolean {
            return oldItem.sameAndEqual(newItem)
        }
    }
}
