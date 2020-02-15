package com.codingblocks.cbonlineapp.commons

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.util.extensions.sameAndEqual
import com.codingblocks.onlineapi.models.Instructor
import com.squareup.picasso.Picasso
import io.noties.markwon.Markwon
import io.noties.markwon.core.CorePlugin
import kotlinx.android.synthetic.main.item_instructor.view.*
import org.jetbrains.anko.email

class InstructorListAdapter : ListAdapter<Instructor, InstructorListAdapter.ItemViewHolder>(DiffCallback()) {

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
        fun bind(instructor: Instructor) = with(itemView) {
            instructorNameTv.text = instructor.name
            val markdown = instructor.description ?: ""

            val markWon = Markwon.builder(context)
                .usePlugin(CorePlugin.create())
                .build()
            markWon.setMarkdown(instructorDescTv, markdown)
//            instructorTextView.text = "${instructor.sub}, Coding Blocks"
            instructorEmailTv.text = instructor.email
            Picasso.get().load(instructor.photo).placeholder(R.drawable.defaultavatar).fit().into(instructorImg)
            setOnClickListener {
                itemView.context.email(instructor.email.toString())
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Instructor>() {
        override fun areItemsTheSame(oldItem: Instructor, newItem: Instructor): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Instructor, newItem: Instructor): Boolean {
            return oldItem.sameAndEqual(newItem)
        }
    }
}
